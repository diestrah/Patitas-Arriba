namespace PatitasArriba.Web.ViewModels {
    // Qué servicios de cita (detalle_cita) tienen atención y si sigue activa. Sale de GET /atenciones/estados.
    // Un servicio con la atención anulada cuenta como no realizado (RN-CI-10) y no se vuelve a atender (RN-AT-02)
    public class AtencionesDeCitas {
        private readonly Dictionary<int, int> activas;
        private readonly HashSet<int> anuladas;

        public AtencionesDeCitas(Dictionary<int, int> activas, HashSet<int> anuladas) {
            this.activas = activas;
            this.anuladas = anuladas;
        }

        public static AtencionesDeCitas Ninguna => new([], []);

        // Tiene atención, activa o anulada: ya no se atiende ni se quita de la cita
        public bool TieneAtencion(int idDetalleCita) => activas.ContainsKey(idDetalleCita) || anuladas.Contains(idDetalleCita);

        // Se atendió y la atención sigue vigente: cuenta como realizado
        public bool Activa(int idDetalleCita) => activas.ContainsKey(idDetalleCita);

        public bool Anulada(int idDetalleCita) => anuladas.Contains(idDetalleCita);

        // Id de la atención activa del servicio, o null
        public int? IdAtencion(int idDetalleCita) => activas.TryGetValue(idDetalleCita, out int id) ? id : null;
    }
}
