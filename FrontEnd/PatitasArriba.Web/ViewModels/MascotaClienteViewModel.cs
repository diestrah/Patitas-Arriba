using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Mascota que registra o edita el propio cliente (portal del cliente).
    // A diferencia de MascotaViewModel (el del administrador): no hay dueño que elegir (es el cliente de la sesión)
    // y el peso es opcional, porque un cachorro en su primera consulta puede no tener peso conocido.
    public class MascotaClienteViewModel : IValidatableObject {
        public int Id { get; set; }

        [Required(ErrorMessage = "El nombre es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string Nombre { get; set; } = "";

        public TipoMascota TipoMascota { get; set; }

        public SexoMascota Sexo { get; set; }

        // Imagen opcional como texto "data:image/jpeg;base64,..."; vacía = sin foto
        public string? Foto { get; set; }

        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string? Raza { get; set; }

        // Opcional; si se indica debe ser mayor a 0 (RN-MA-02)
        [Range(0.1, 150, ErrorMessage = "Si indica el peso, debe estar entre 0.1 y 150 kg")]
        public double? Peso { get; set; }

        // La base de datos exige la fecha (fecha_nacimiento es NOT NULL), aunque la regla RN-MA-02 la llama opcional
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
