using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class RecetaViewModel : IValidatableObject {
        public int Id { get; set; }
        public int IdAtencion { get; set; }
        public string NombreMascota { get; set; } = "";
        public TipoMascota TipoMascota { get; set; }
        public string NombreCliente { get; set; } = "";
        public int IdVeterinario { get; set; }
        public string NombreVeterinario { get; set; } = "";
        public string NumeroColegiatura { get; set; } = "";
        public string Diagnostico { get; set; } = "";

        [Required(ErrorMessage = "Indique la fecha de emisión")]
        public DateOnly? FechaEmision { get; set; }

        // El backend exige las indicaciones (RN-RE-01)
        [Required(ErrorMessage = "Las indicaciones generales son obligatorias")]
        [StringLength(500, ErrorMessage = "Máximo 500 caracteres")]
        public string? IndicacionesGenerales { get; set; }

        public List<DetalleRecetaViewModel> Medicamentos { get; set; } = [];

        // RN-RE-03: una receta tiene al menos un medicamento
        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            if (Medicamentos.Count == 0) {
                yield return new ValidationResult("Agregue al menos un medicamento", [nameof(Medicamentos)]);
            }
        }
    }
}
