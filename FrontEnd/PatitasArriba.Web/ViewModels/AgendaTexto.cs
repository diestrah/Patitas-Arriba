namespace PatitasArriba.Web.ViewModels {
    // Cómo se ve un servicio en la agenda: junta el estado de la cita (por aprobar) con el de la atención
    public enum EstadoServicioAgenda {
        PorAprobar,
        PorAtender,
        Atendido,
        // Su atención se anuló: cuenta como no realizado y no se vuelve a atender (RN-CI-10, RN-AT-02)
        Anulado
    }

    public static class AgendaTexto {
        public static EstadoServicioAgenda De(ItemAgendaViewModel item, AtencionesDeCitas atenciones) {
            if (atenciones.Activa(item.IdDetalleCita)) {
                return EstadoServicioAgenda.Atendido;
            }
            if (atenciones.Anulada(item.IdDetalleCita)) {
                return EstadoServicioAgenda.Anulado;
            }
            if (item.EstadoCita == EstadoCita.PENDIENTE) {
                return EstadoServicioAgenda.PorAprobar;
            }
            return EstadoServicioAgenda.PorAtender;
        }

        public static string Texto(EstadoServicioAgenda estado) {
            return estado switch {
                EstadoServicioAgenda.PorAprobar => "Por aprobar",
                EstadoServicioAgenda.Atendido => "Atendido",
                EstadoServicioAgenda.Anulado => "Anulado",
                _ => "Por atender"
            };
        }

        // Clase de la etiqueta de estado (ver app.css)
        public static string ClaseEstado(EstadoServicioAgenda estado) {
            return estado switch {
                EstadoServicioAgenda.PorAprobar => "estado-por-aprobar",
                EstadoServicioAgenda.Atendido => "estado-aprobado",
                EstadoServicioAgenda.Anulado => "estado-cancelado",
                _ => "estado-pendiente"
            };
        }

        // Clase del bloque en la línea de tiempo de la agenda
        public static string ClaseBloque(EstadoServicioAgenda estado) {
            return estado switch {
                EstadoServicioAgenda.PorAprobar => "bloque-por-aprobar",
                EstadoServicioAgenda.Atendido => "bloque-atendido",
                EstadoServicioAgenda.Anulado => "bloque-anulado",
                _ => "bloque-por-atender"
            };
        }
    }
}
