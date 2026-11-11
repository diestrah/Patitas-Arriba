using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // Estado de turno del personal calculado con los horarios asignados (RN-HO-03): quién está de turno ahora,
    // cuál es su próximo turno y cuántos servicios tiene hoy. Recibe los datos ya cargados del backend
    // (PersonalClinica.HorariosDe(persona), GET /citas?fecha=hoy y GET /atenciones); no consulta nada por sí mismo.
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

        // El horario de la persona que cubre este momento, o null si no está de turno
        public static HorarioViewModel? EnTurno(IEnumerable<HorarioViewModel> suyos, DateTime ahora) {
            TimeOnly hora = TimeOnly.FromDateTime(ahora);
            return suyos.FirstOrDefault(h => h.DiaSemana == DiaDe(ahora.DayOfWeek)
                && h.HoraInicio <= hora && hora < h.HoraFin);
        }

        // Su siguiente turno (hoy más tarde o dentro de los próximos 7 días) con la fecha y hora en que empieza
        public static (DateTime Inicio, HorarioViewModel Horario)? ProximoTurno(IEnumerable<HorarioViewModel> suyos, DateTime ahora) {
            List<HorarioViewModel> lista = suyos.ToList();
            for (int dias = 0; dias <= 7; dias++) {
                DateTime fecha = ahora.Date.AddDays(dias);
                HorarioViewModel? siguiente = lista
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
        public static (string Clase, string Texto) Estado(IEnumerable<HorarioViewModel> suyos, DateTime ahora) {
            List<HorarioViewModel> lista = suyos.ToList();
            HorarioViewModel? actual = EnTurno(lista, ahora);
            if (actual != null) {
                return ("etiqueta-solida", $"En turno ahora · hasta {actual.HoraFin:HH:mm}");
            }
            (DateTime Inicio, HorarioViewModel Horario)? proximo = ProximoTurno(lista, ahora);
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
        public static string ProximoRelevo(IEnumerable<HorarioViewModel> horarios, DateTime ahora) {
            List<HorarioViewModel> lista = horarios.ToList();
            for (int dias = 0; dias <= 7; dias++) {
                DateTime fecha = ahora.Date.AddDays(dias);
                List<TimeSpan> cambios = [];
                foreach (HorarioViewModel horario in lista.Where(h => h.DiaSemana == DiaDe(fecha.DayOfWeek))) {
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

        // Servicios de hoy de un veterinario: programados (detalles de las citas de hoy que no fueron
        // rechazadas ni canceladas) y ya atendidos (con atención registrada). Agenda de RF11 y RF13.
        public static (int Programados, int Atendidos) ServiciosDeHoy(int idVeterinario, IEnumerable<CitaViewModel> citasDeHoy, ISet<int> detallesAtendidos) {
            List<DetalleCitaViewModel> suyos = citasDeHoy
                .Where(c => c.Estado != EstadoCita.RECHAZADO && c.Estado != EstadoCita.CANCELADO)
                .SelectMany(c => c.Detalles)
                .Where(d => d.IdVeterinario == idVeterinario)
                .ToList();
            return (suyos.Count, suyos.Count(d => detallesAtendidos.Contains(d.Id)));
        }

        // ---------------------------------------------------------------- Con datos de ejemplo
        // Solo para InicioVeterinario, que todavía no está enlazado (módulo 7): busca a la persona por su nombre
        // en DatosEjemplo. Se eliminan al enlazar esa pantalla.
        public static HorarioViewModel? EnTurno(string nombre, DateTime ahora) =>
            EnTurno(DatosEjemplo.Horarios().Where(h => h.NombresPersonal.Contains(nombre)), ahora);

        public static (DateTime Inicio, HorarioViewModel Horario)? ProximoTurno(string nombre, DateTime ahora) =>
            ProximoTurno(DatosEjemplo.Horarios().Where(h => h.NombresPersonal.Contains(nombre)), ahora);
    }
}
