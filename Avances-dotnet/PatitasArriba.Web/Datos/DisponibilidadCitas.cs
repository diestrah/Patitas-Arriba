using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // Un horario en el que podría empezar una cita, con el veterinario que atendería cada servicio
    public class HorarioDisponible {
        public DateTime Inicio { get; set; }
        public bool Libre { get; set; }
        public List<DetalleCitaViewModel> Detalles { get; set; } = [];
    }

    // Calcula los horarios libres de un día para una lista de servicios y asigna un veterinario a cada uno.
    // Aplica lo mismo que exigirá el backend: el veterinario atiende todo ese tiempo según sus horarios (RN-CI-05)
    // y no tiene otro servicio que se cruce (RN-CI-06). Servicios sin duración cuentan 30 minutos.
    // Al enlazar: usa GET /veterinarios (con sus horarios), GET /horarios y GET /citas?fecha=AAAA-MM-DD;
    // el backend vuelve a validar todo al registrar la cita, así que esto es solo ayuda para el cliente.
    public static class DisponibilidadCitas {
        private const int PasoMinutos = 30;

        public static List<HorarioDisponible> HorariosDelDia(DateTime fecha, List<ServicioViewModel> servicios) {
            List<HorarioDisponible> resultado = [];
            if (servicios.Count == 0) {
                return resultado;
            }

            DiaSemana dia = TurnosPersonal.DiaDe(fecha.DayOfWeek);
            List<HorarioViewModel> horarios = DatosEjemplo.Horarios().Where(h => h.DiaSemana == dia).ToList();
            if (horarios.Count == 0) {
                return resultado;
            }
            List<VeterinarioViewModel> veterinarios = DatosEjemplo.Veterinarios();
            List<ItemAgendaViewModel> agenda = DatosEjemplo.AgendaDelDia(fecha);

            int totalMinutos = servicios.Sum(s => s.DuracionEstimada ?? 30);
            TimeOnly primero = horarios.Min(h => h.HoraInicio);
            TimeOnly ultimo = horarios.Max(h => h.HoraFin);
            int desdeMinuto = primero.Hour * 60 + primero.Minute;
            int hastaMinuto = ultimo.Hour * 60 + ultimo.Minute;

            // Cada 30 minutos, mientras todos los servicios terminen dentro del horario de atención del día
            for (int minuto = desdeMinuto; minuto + totalMinutos <= hastaMinuto; minuto += PasoMinutos) {
                DateTime inicio = fecha.Date.AddMinutes(minuto);
                HorarioDisponible horario = new HorarioDisponible { Inicio = inicio, Libre = inicio > DateTime.Now };
                if (horario.Libre) {
                    horario.Detalles = AsignarVeterinarios(inicio, servicios, totalMinutos, horarios, veterinarios, agenda);
                    horario.Libre = horario.Detalles.Count == servicios.Count;
                }
                resultado.Add(horario);
            }
            return resultado;
        }

        // Un veterinario por servicio, probando primero con el que atendió el servicio anterior.
        // Si algún servicio no tiene veterinario libre, devuelve una lista incompleta (el horario no está libre).
        private static List<DetalleCitaViewModel> AsignarVeterinarios(DateTime inicio, List<ServicioViewModel> servicios, int totalMinutos,
                List<HorarioViewModel> horarios, List<VeterinarioViewModel> veterinarios, List<ItemAgendaViewModel> agenda) {
            List<DetalleCitaViewModel> detalles = [];
            DateTime finTotal = inicio.AddMinutes(totalMinutos);
            DateTime inicioServicio = inicio;
            VeterinarioViewModel? anterior = null;

            foreach (ServicioViewModel servicio in servicios) {
                DateTime finServicio = inicioServicio.AddMinutes(servicio.DuracionEstimada ?? 30);
                List<VeterinarioViewModel> candidatos = veterinarios
                    .Where(v => AtiendeTodoElTiempo(v, horarios, inicio, finTotal) && !Ocupado(v, agenda, inicioServicio, finServicio))
                    .OrderByDescending(v => anterior != null && v.Id == anterior.Id)
                    .ToList();
                if (candidatos.Count == 0) {
                    return detalles;
                }
                VeterinarioViewModel elegido = candidatos[0];
                detalles.Add(new DetalleCitaViewModel {
                    IdServicio = servicio.Id,
                    NombreServicio = servicio.Nombre,
                    IdVeterinario = elegido.Id,
                    NombreVeterinario = elegido.NombreCompleto
                });
                anterior = elegido;
                inicioServicio = finServicio;
            }
            return detalles;
        }

        // RN-CI-05: el veterinario atiende ese día durante todo el tiempo de los servicios
        private static bool AtiendeTodoElTiempo(VeterinarioViewModel veterinario, List<HorarioViewModel> horarios, DateTime inicio, DateTime fin) {
            TimeOnly desde = TimeOnly.FromDateTime(inicio);
            TimeOnly hasta = TimeOnly.FromDateTime(fin);
            return horarios.Any(h => h.NombresPersonal.Contains(veterinario.NombreCompleto) && h.HoraInicio <= desde && h.HoraFin >= hasta);
        }

        // RN-CI-06: no puede tener otro servicio de una cita pendiente o aprobada que se cruce
        private static bool Ocupado(VeterinarioViewModel veterinario, List<ItemAgendaViewModel> agenda, DateTime inicio, DateTime fin) {
            return agenda.Any(i => i.NombreVeterinario == veterinario.NombreCompleto && i.FechaHora < fin && i.FechaHoraFin > inicio);
        }
    }
}
