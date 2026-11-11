using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Se usa para editar una cuenta ya existente (las cuentas nuevas se crean junto con la persona)
    public class CuentaUsuarioViewModel : IValidatableObject {
        public int Id { get; set; }

        [Required(ErrorMessage = "El nombre de usuario es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string NombreUsuario { get; set; } = "";

        // Mismo formato que valida el backend (RN-CU-01): usuario@dominio.ext
        [Required(ErrorMessage = "El correo es obligatorio")]
        [RegularExpression(@"^[^\s@]+@[^\s@]+\.[^\s@]+$", ErrorMessage = "Ingrese un correo válido (usuario@dominio.ext)")]
        [StringLength(100, ErrorMessage = "Máximo 100 caracteres")]
        public string Correo { get; set; } = "";

        // Se puede dejar vacía para mantener la contraseña actual
        public string? Password { get; set; }

        // El rol se deduce de a quién pertenece la cuenta (cliente, veterinario o administrador)
        public RolUsuario? Rol { get; set; }
        public string NombrePersona { get; set; } = "";

        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            if (!string.IsNullOrEmpty(Password) && (Password.Length < 8 || Password.Length > 64)) {
                yield return new ValidationResult("La contraseña debe tener entre 8 y 64 caracteres", [nameof(Password)]);
            }
        }
    }
}
