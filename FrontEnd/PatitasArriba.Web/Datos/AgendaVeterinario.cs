using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // Agenda del veterinario que usan su inicio, Mi agenda y Registrar atención: qué servicios tiene y cuáles puede atender.
    // La agenda sale de GET /citas?fecha=AAAA-MM-DD&veterinario={id} (AgendaClinica.DelDia) y lo ya atendido de
    // GET /atenciones/estados (incluidas las atenciones anuladas).
    public static class AgendaVeterinario {
        // Servicios de la agenda asignados al veterinario (una cita puede tener servicios de otros veterinarios)
        public static List<ItemAgendaViewModel> DelVeterinario(int idVeterinario, IEnumerable<ItemAgendaViewModel> agenda) {
            return agenda.Where(i => i.IdVeterinario == idVeterinario).ToList();
        }

        // Le falta atenderse y su cita lo permite: aprobada, o pendiente si es emergencia (RN-AT-04).
        // Un servicio con la atención anulada ya no se atiende (RN-AT-02)
        public static bool EsPorAtender(ItemAgendaViewModel item, AtencionesDeCitas atenciones) {
            if (atenciones.TieneAtencion(item.IdDetalleCita)) {
                return false;
            }
            return item.EstadoCita == EstadoCita.APROBADO || (item.EstadoCita == EstadoCita.PENDIENTE && item.EsEmergencia);
        }

        // Por qué todavía no se puede atender (va en el title del guion); null si ya se puede.
        // Además de RN-AT-04, la atención no puede ser anterior a la hora de su cita (RN-AT-05)
        public static string? MotivoSinAtender(ItemAgendaViewModel item, AtencionesDeCitas atenciones, DateTime ahora) {
            if (atenciones.Activa(item.IdDetalleCita)) {
                return "Ya fue atendido";
            }
            if (atenciones.Anulada(item.IdDetalleCita)) {
                return "Su atención se anuló: queda como no realizado";
            }
            if (!EsPorAtender(item, atenciones)) {
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

        public static bool PuedeAtenderse(ItemAgendaViewModel item, AtencionesDeCitas atenciones, DateTime ahora) {
            return MotivoSinAtender(item, atenciones, ahora) == null;
        }
    }
}
