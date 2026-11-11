using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // Agenda del veterinario que usan su inicio, Mi agenda y Registrar atención: qué servicios tiene y cuáles puede atender.
    // La agenda sale de GET /citas?fecha=AAAA-MM-DD&veterinario={id} (AgendaClinica.DelDia) y lo ya atendido de GET /atenciones.
    public static class AgendaVeterinario {
        // Servicios de la agenda asignados al veterinario (una cita puede tener servicios de otros veterinarios)
        public static List<ItemAgendaViewModel> DelVeterinario(int idVeterinario, IEnumerable<ItemAgendaViewModel> agenda) {
            return agenda.Where(i => i.IdVeterinario == idVeterinario).ToList();
        }

        // Le falta atenderse y su cita lo permite: aprobada, o pendiente si es emergencia (RN-AT-04)
        public static bool EsPorAtender(ItemAgendaViewModel item, HashSet<int> atendidos) {
            if (atendidos.Contains(item.IdDetalleCita)) {
                return false;
            }
            return item.EstadoCita == EstadoCita.APROBADO || (item.EstadoCita == EstadoCita.PENDIENTE && item.EsEmergencia);
        }

        // Por qué todavía no se puede atender (va en el title del guion); null si ya se puede.
        // Además de RN-AT-04, la atención no puede ser anterior a la hora de su cita (RN-AT-05)
        public static string? MotivoSinAtender(ItemAgendaViewModel item, HashSet<int> atendidos, DateTime ahora) {
            if (atendidos.Contains(item.IdDetalleCita)) {
                return "Ya fue atendido";
            }
            if (!EsPorAtender(item, atendidos)) {
                return item.EstadoCita == EstadoCita.PENDIENTE ? "La cita todavía no está aprobada" : "La cita ya está cerrada";
            }
            if (item.FechaHora.Date > ahora.Date) {
                return $"Es para el {item.FechaHora:dd/MM}";
            }
            if (item.FechaHora > ahora) {
                return $"Se atiende desde las {item.FechaHora:HH:mm}";
            }
            return null;
        }

        public static bool PuedeAtenderse(ItemAgendaViewModel item, HashSet<int> atendidos, DateTime ahora) {
            return MotivoSinAtender(item, atendidos, ahora) == null;
        }
    }
}
