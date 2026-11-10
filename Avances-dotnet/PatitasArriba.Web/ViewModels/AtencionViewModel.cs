using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class AtencionViewModel : IValidatableObject {
        public int Id { get; set; }

        // Servicio de la cita que se está atendiendo (relación 1 a 1 con detalle_cita)
        public int IdDetalleCita { get; set; }
        public int IdMascota { get; set; }
        public string NombreMascota { get; set; } = "";
        public string NombreCliente { get; set; } = "";
        public string NombreServicio { get; set; } = "";
        public TipoServicio TipoServicio { get; set; }
        public string NombreVeterinario { get; set; } = "";

        [Required(ErrorMessage = "Indique la fecha y hora de la atención")]
        public DateTime? FechaHora { get; set; }

        [StringLength(200, ErrorMessage = "Máximo 200 caracteres")]
        public string? MotivoConsulta { get; set; }

        [StringLength(500, ErrorMessage = "Máximo 500 caracteres")]
        public string? Observaciones { get; set; }

        public List<AtencionDiagnosticoViewModel> Diagnosticos { get; set; } = [];
        public List<InsumoUtilizadoViewModel> Insumos { get; set; } = [];

        // RN-SE-02: estética es el único servicio no médico (no lleva diagnósticos ni recetas)
        public bool EsServicioMedico => TipoServicio != TipoServicio.ESTETICA;

        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            // RN-AT-06: en servicios médicos el motivo de consulta es obligatorio
            if (EsServicioMedico && string.IsNullOrWhiteSpace(MotivoConsulta)) {
                yield return new ValidationResult("El motivo de consulta es obligatorio en servicios médicos", [nameof(MotivoConsulta)]);
            }
            // RN-AT-05: la atención no puede registrarse con una hora futura
            if (FechaHora > DateTime.Now) {
                yield return new ValidationResult("La fecha y hora no puede ser posterior a este momento", [nameof(FechaHora)]);
            }
        }
    }
}
