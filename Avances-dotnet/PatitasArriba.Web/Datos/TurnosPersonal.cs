using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // Estado de turno del personal calculado con los horarios asignados (RN-HO-03): quién está de turno ahora,
    // cuál es su próximo turno y cuántos servicios tiene hoy. No necesita datos nuevos.
    // Al enlazar: usa los horarios de GET /veterinarios y /administradores y la agenda de GET /citas?fecha=...
    public static class TurnosPersonal {
        public static DiaSemana DiaDe(DayOfWeek dia) {
            return dia switch {
                DayOfWeek.Monday => DiaSemana.LUNES,
                DayOfWeek.Tuesday => DiaSemana.MARTES,
                DayOfWeek.Wednesday => DiaSemana.MIERCOLES,
                DayOfWeek.Thursday => DiaSemana.JUEVES,
                DayOfWeek.Friday => DiaSemana.VIERNES,
                DayOfWeek.Saturday => DiaSemana.SABADO,
                _ => DiaSemana.DOMINGO
            };
        }

        // El horario que cubre este momento para la persona, o null si no está de turno
        public static HorarioViewModel? EnTurno(string nombre, DateTime ahora) {
            TimeOnly hora = TimeOnly.FromDateTime(ahora);
            return DatosEjemplo.Horarios().FirstOrDefault(h => h.DiaSemana == DiaDe(ahora.DayOfWeek)
                && h.NombresPersonal.Contains(nombre) && h.HoraInicio <= hora && hora < h.HoraFin);
        }

        // Su siguiente turno (hoy más tarde o dentro de los próximos 7 días) con la fecha y hora en que empieza
        public static (DateTime Inicio, HorarioViewModel Horario)? ProximoTurno(string nombre, DateTime ahora) {
            List<HorarioViewModel> suyos = DatosEjemplo.Horarios().Where(h => h.NombresPersonal.Contains(nombre)).ToList();
            for (int dias = 0; dias <= 7; dias++) {
                DateTime fecha = ahora.Date.AddDays(dias);
                HorarioViewModel? siguiente = suyos
                    .Where(h => h.DiaSemana == DiaDe(fecha.DayOfWeek) && (dias > 0 || h.HoraInicio.ToTimeSpan() > ahora.TimeOfDay))
                    .OrderBy(h => h.HoraInicio)
                    .FirstOrDefault();
                if (siguiente != null) {
                    return (fecha.Add(siguiente.HoraInicio.ToTimeSpan()), siguiente);
                }
            }
            return null;
        }

        // Etiqueta de estado de la persona: "En turno ahora · hasta 14:00" o "Próximo: Mañana 08:00" (con su clase de color)
        public static (string Clase, string Texto) Estado(string nombre, DateTime ahora) {
            HorarioViewModel? actual = EnTurno(nombre, ahora);
            if (actual != null) {
                return ("etiqueta-solida", $"En turno ahora · hasta {actual.HoraFin:HH:mm}");
            }
            (DateTime Inicio, HorarioViewModel Horario)? proximo = ProximoTurno(nombre, ahora);
            return proximo == null
                ? ("etiqueta-mostaza", "Sin horarios asignados")
                : ("etiqueta-gris", $"Próximo: {TextoMomento(proximo.Value.Inicio, ahora)}");
        }

        // "Hoy 14:00", "Mañana 08:00" o "Miércoles 08:00"
        public static string TextoMomento(DateTime momento, DateTime ahora) {
            string dia = momento.Date == ahora.Date ? "Hoy"
                : momento.Date == ahora.Date.AddDays(1) ? "Mañana"
                : TextoEnum.De(DiaDe(momento.DayOfWeek));
            return $"{dia} {momento:HH:mm}";
        }

        // El próximo cambio de turno de toda la clínica: la siguiente hora de inicio o de fin de cualquier horario
        public static string ProximoRelevo(DateTime ahora) {
            List<HorarioViewModel> horarios = DatosEjemplo.Horarios();
            for (int dias = 0; dias <= 7; dias++) {
                DateTime fecha = ahora.Date.AddDays(dias);
                List<TimeSpan> cambios = [];
                foreach (HorarioViewModel horario in horarios.Where(h => h.DiaSemana == DiaDe(fecha.DayOfWeek))) {
                    cambios.Add(horario.HoraInicio.ToTimeSpan());
                    cambios.Add(horario.HoraFin.ToTimeSpan());
                }
                List<TimeSpan> proximos = cambios.Where(c => dias > 0 || c > ahora.TimeOfDay).OrderBy(c => c).ToList();
                if (proximos.Count > 0) {
                    return TextoMomento(fecha.Add(proximos[0]), ahora);
                }
            }
            return "—";
        }

        // Servicios de hoy de un veterinario: programados y ya atendidos (agenda de RF11 y RF13)
        public static (int Programados, int Atendidos) ServiciosDeHoy(string nombreVeterinario) {
            HashSet<int> atendidos = DatosEjemplo.DetallesAtendidos();
            List<ItemAgendaViewModel> hoy = DatosEjemplo.AgendaDeHoy().Where(i => i.NombreVeterinario == nombreVeterinario).ToList();
            return (hoy.Count, hoy.Count(i => atendidos.Contains(i.IdDetalleCita)));
        }
    }
}
