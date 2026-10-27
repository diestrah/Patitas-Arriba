using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class CitaViewModel {
        public int Id { get; set; }

        [Range(1, int.MaxValue, ErrorMessage = "Seleccione el cliente")]
        public int IdCliente { get; set; }
        public string NombreCliente { get; set; } = "";

        [Range(1, int.MaxValue, ErrorMessage = "Seleccione la mascota")]
        public int IdMascota { get; set; }
        public string NombreMascota { get; set; } = "";

        [Required(ErrorMessage = "Indique la fecha y hora de la cita")]
        public DateTime? FechaHora { get; set; }

        public EstadoCita Estado { get; set; } = EstadoCita.PENDIENTE;
        public bool EsEmergencia { get; set; }

        // Administrador que aprobó o rechazó la cita (vacío mientras está pendiente)
        public string? NombreAdministrador { get; set; }

        // Cada servicio solicitado con su veterinario (detalle_cita)
        public List<DetalleCitaViewModel> Detalles { get; set; } = [];

        public string TextoServicios => string.Join(", ", Detalles.Select(d => d.NombreServicio));
    }
}
