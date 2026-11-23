using PatitasArriba.Web.Servicios;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // Un horario en el que podría empezar una cita, con el veterinario que atendería cada servicio
    public class HorarioDisponible {
        public DateTime Inicio { get; set; }
        public bool Libre { get; set; }
        public List<DetalleCitaViewModel> Detalles { get; set; } = [];
    }

    // Calcula los horarios libres de un día para una lista de servicios y asigna un veterinario a cada uno, con la
    // misma regla que CitaBLImpl: cada veterinario ocupa desde la hora de la cita la suma de la duración de sus
    // servicios (30 minutos si un servicio no la tiene); ese tramo debe estar dentro de uno de sus horarios
    // (RN-CI-05) y no cruzarse con el de otra cita PENDIENTE o APROBADA suya (RN-CI-06).
    // Recibe los datos ya cargados: PersonalClinica (horarios y veterinarios), GET /citas?fecha=... y GET /servicios.
    // El backend vuelve a validar todo al registrar la cita: esto es solo ayuda para el cliente.
    public static class DisponibilidadCitas {
        private const int PasoMinutos = 30;

        public static List<HorarioDisponible> HorariosDelDia(DateTime fecha, List<ServicioViewModel> elegidos,
                PersonalClinica personal, List<CitaViewModel> citasDelDia, List<ServicioViewModel> catalogo) {
            List<HorarioDisponible> resultado = [];
            if (elegidos.Count == 0) {
                return resultado;
            }

            DiaSemana dia = TurnosPersonal.DiaDe(fecha.DayOfWeek);
            List<HorarioViewModel> horarios = personal.Horarios.Where(h => h.DiaSemana == dia).ToList();
            if (horarios.Count == 0) {
                return resultado;
            }

            // Tramos ya ocupados por cada veterinario ese día
            Dictionary<int, List<(DateTime Inicio, DateTime Fin)>> ocupados = [];
            foreach (CitaViewModel cita in citasDelDia) {
                if (cita.FechaHora?.Date != fecha.Date || (cita.Estado != EstadoCita.PENDIENTE && cita.Estado != EstadoCita.APROBADO)) {
                    continue;
                }
                foreach ((int idVeterinario, int minutos) in AgendaClinica.MinutosPorVeterinario(cita, catalogo)) {
                    if (!ocupados.TryGetValue(idVeterinario, out List<(DateTime, DateTime)>? tramos)) {
                        tramos = [];
                        ocupados[idVeterinario] = tramos;
                    }
                    tramos.Add((cita.FechaHora.Value, cita.FechaHora.Value.AddMinutes(minutos)));
                }
            }

            int totalMinutos = elegidos.Sum(AgendaClinica.Duracion);
            TimeOnly primero = horarios.Min(h => h.HoraInicio);
            TimeOnly ultimo = horarios.Max(h => h.HoraFin);
            int desdeMinuto = primero.Hour * 60 + primero.Minute;
            int hastaMinuto = ultimo.Hour * 60 + ultimo.Minute;

            // Cada 30 minutos, mientras todos los servicios terminen dentro del horario de atención del día
            for (int minuto = desdeMinuto; minuto + totalMinutos <= hastaMinuto; minuto += PasoMinutos) {
                DateTime inicio = fecha.Date.AddMinutes(minuto);
                HorarioDisponible horario = new() { Inicio = inicio, Libre = inicio > DateTime.Now };
                if (horario.Libre) {
                    horario.Detalles = AsignarVeterinarios(inicio, elegidos, personal, ocupados);
                    horario.Libre = horario.Detalles.Count == elegidos.Count;
                }
                resultado.Add(horario);
            }
            return resultado;
        }

        // Un veterinario por servicio, probando primero con el que atendió el servicio anterior.
        // Si algún servicio no tiene veterinario libre, devuelve una lista incompleta (el horario no está libre).
        private static List<DetalleCitaViewModel> AsignarVeterinarios(DateTime inicio, List<ServicioViewModel> servicios,
                PersonalClinica personal, Dictionary<int, List<(DateTime Inicio, DateTime Fin)>> ocupados) {
            List<DetalleCitaViewModel> detalles = [];
            Dictionary<int, int> minutosAsignados = [];
            VeterinarioViewModel? anterior = null;

            foreach (ServicioViewModel servicio in servicios) {
                int duracion = AgendaClinica.Duracion(servicio);
                VeterinarioViewModel? elegido = personal.Veterinarios
                    .OrderByDescending(v => anterior != null && v.Id == anterior.Id)
                    .FirstOrDefault(v => {
                        DateTime fin = inicio.AddMinutes(minutosAsignados.GetValueOrDefault(v.Id) + duracion);
                        return Cubre(personal.HorariosDe(v), inicio, fin) && !Cruza(ocupados.GetValueOrDefault(v.Id), inicio, fin);
                    });
                if (elegido == null) {
                    return detalles;
                }
                minutosAsignados[elegido.Id] = minutosAsignados.GetValueOrDefault(elegido.Id) + duracion;
                detalles.Add(new DetalleCitaViewModel {
                    IdServicio = servicio.Id,
                    NombreServicio = servicio.Nombre,
                    IdVeterinario = elegido.Id,
                    NombreVeterinario = elegido.NombreCompleto
                });
                anterior = elegido;
            }
            return detalles;
        }

        // RN-CI-05: algún horario del veterinario cubre todo el tramo, el mismo día
        private static bool Cubre(List<HorarioViewModel> suyos, DateTime inicio, DateTime fin) {
            if (fin.Date != inicio.Date && fin.TimeOfDay != TimeSpan.Zero) {
                return false;
            }
            DiaSemana dia = TurnosPersonal.DiaDe(inicio.DayOfWeek);
            TimeOnly desde = TimeOnly.FromDateTime(inicio);
            TimeOnly hasta = fin.Date != inicio.Date ? TimeOnly.MaxValue : TimeOnly.FromDateTime(fin);
            return suyos.Any(h => h.DiaSemana == dia && h.HoraInicio <= desde && h.HoraFin >= hasta);
        }

        // RN-CI-06: el tramo no se cruza con otro tramo del mismo veterinario
        private static bool Cruza(List<(DateTime Inicio, DateTime Fin)>? tramos, DateTime inicio, DateTime fin) {
            return tramos != null && tramos.Any(t => inicio < t.Fin && t.Inicio < fin);
        }
    }
}
