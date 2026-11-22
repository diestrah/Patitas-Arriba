namespace PatitasArriba.Web.ViewModels {
    // Una fila de la agenda: un servicio de una cita asignado a un veterinario
    public class ItemAgendaViewModel {
        public int IdCita { get; set; }
        public int IdDetalleCita { get; set; }
        public int IdMascota { get; set; }
        public DateTime FechaHora { get; set; }

        // Minutos que dura el servicio (duracionEstimada del servicio; 30 si no tiene, RN-CI-05)
        public int DuracionMinutos { get; set; } = 30;

        public string NombreMascota { get; set; } = "";
        public string NombreCliente { get; set; } = "";
        public string NombreServicio { get; set; } = "";
        public TipoServicio TipoServicio { get; set; }
        public int IdVeterinario { get; set; }
        public string NombreVeterinario { get; set; } = "";
        public bool EsEmergencia { get; set; }

        // Estado de la cita a la que pertenece el servicio
        public EstadoCita EstadoCita { get; set; } = EstadoCita.APROBADO;

        public DateTime FechaHoraFin => FechaHora.AddMinutes(DuracionMinutos);
    }
}
