using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class CitaViewModel : IValidatableObject {
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

        // Administrador que aprobó o rechazó la cita (vacío mientras está pendiente).
        // El backend exige su id al aprobar o rechazar (RN-CI-09).
        public int? IdAdministrador { get; set; }
        public string? NombreAdministrador { get; set; }

        // Cada servicio solicitado con su veterinario (detalle_cita)
        public List<DetalleCitaViewModel> Detalles { get; set; } = [];

        public string TextoServicios => string.Join(", ", Detalles.Select(d => d.NombreServicio));

        // RN-CI-08: RECHAZADO, CANCELADO y COMPLETADO son estados finales
        public bool EsModificable => Estado == EstadoCita.PENDIENTE || Estado == EstadoCita.APROBADO;

        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            // RN-CI-03 y RN-CI-07: una cita nueva no puede estar en el pasado (emergencia: hasta 1 hora antes)
            if (Id == 0 && FechaHora != null) {
                DateTime limite = EsEmergencia ? DateTime.Now.AddHours(-1) : DateTime.Now;
                if (FechaHora < limite) {
                    yield return new ValidationResult(EsEmergencia
                        ? "Una emergencia se registra con una hora de hasta 1 hora antes"
                        : "La fecha y hora no puede estar en el pasado", [nameof(FechaHora)]);
                }
            }
            // RN-CI-02: una cita incluye uno o más servicios
            if (Detalles.Count == 0) {
                yield return new ValidationResult("Agregue al menos un servicio", [nameof(Detalles)]);
            }
        }
    }
}
