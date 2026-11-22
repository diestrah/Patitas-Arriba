using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class MascotaViewModel : IValidatableObject {
        public int Id { get; set; }

        [Range(1, int.MaxValue, ErrorMessage = "Seleccione el dueño de la mascota")]
        public int IdCliente { get; set; }
        public string NombreDueno { get; set; } = "";

        [Required(ErrorMessage = "El nombre es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string Nombre { get; set; } = "";

        public TipoMascota TipoMascota { get; set; }

        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string? Raza { get; set; }

        public SexoMascota Sexo { get; set; }

        // Imagen opcional como texto "data:image/jpeg;base64,..." (la sube el cliente desde su portal)
        public string? Foto { get; set; }

        // El backend exige un peso mayor a 0 (RN-MA-02)
        [Required(ErrorMessage = "El peso es obligatorio")]
        [Range(0.1, 150, ErrorMessage = "El peso debe estar entre 0.1 y 150 kg")]
        public double? Peso { get; set; }

        [Required(ErrorMessage = "La fecha de nacimiento es obligatoria")]
        public DateOnly? FechaNacimiento { get; set; }

        // RN-MA-04: la fecha de nacimiento no puede ser futura
        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            if (FechaNacimiento > DateOnly.FromDateTime(DateTime.Today)) {
                yield return new ValidationResult("La fecha de nacimiento no puede ser futura", [nameof(FechaNacimiento)]);
            }
        }
    }
}
