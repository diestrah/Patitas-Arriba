namespace PatitasArriba.Web.ViewModels {
    // Cómo se ve un servicio en la agenda: junta el estado de la cita (por aprobar) con el de la atención
    public enum EstadoServicioAgenda {
        PorAprobar,
        PorAtender,
        Atendido
    }

    public static class AgendaTexto {
        // "atendidos" son los ids de los servicios de cita que ya tienen su atención
        public static EstadoServicioAgenda De(ItemAgendaViewModel item, HashSet<int> atendidos) {
            if (atendidos.Contains(item.IdDetalleCita)) {
                return EstadoServicioAgenda.Atendido;
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
                _ => "Por atender"
            };
        }

        // Clase de la etiqueta de estado (ver app.css)
        public static string ClaseEstado(EstadoServicioAgenda estado) {
            return estado switch {
                EstadoServicioAgenda.PorAprobar => "estado-por-aprobar",
                EstadoServicioAgenda.Atendido => "estado-aprobado",
                _ => "estado-pendiente"
            };
        }

        // Clase del bloque en la línea de tiempo de la agenda
        public static string ClaseBloque(EstadoServicioAgenda estado) {
            return estado switch {
                EstadoServicioAgenda.PorAprobar => "bloque-por-aprobar",
                EstadoServicioAgenda.Atendido => "bloque-atendido",
                _ => "bloque-por-atender"
            };
        }
    }
}
