namespace PatitasArriba.Web.ViewModels {
    // Una fila de la agenda: un servicio de una cita asignado a un veterinario
    public class ItemAgendaViewModel {
        public int IdCita { get; set; }
        public int IdDetalleCita { get; set; }
        public int IdMascota { get; set; }
        public DateTime FechaHora { get; set; }
        public string NombreMascota { get; set; } = "";
        public string NombreCliente { get; set; } = "";
        public string NombreServicio { get; set; } = "";
        public string NombreVeterinario { get; set; } = "";
        public bool EsEmergencia { get; set; }
    }
}
