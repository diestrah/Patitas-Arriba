using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Datos comunes de clientes, veterinarios y administradores.
    // La cuenta de usuario se escribe en el mismo formulario que la persona (ya no se elige de una lista):
    // al enlazar con el backend se registra primero la cuenta (POST /cuentas) y luego la persona con esa cuenta.
    public class PersonaViewModel : IValidatableObject {
        public int Id { get; set; }

        // Cuenta de la persona (0 mientras no se registra)
        public int IdCuenta { get; set; }

        [Required(ErrorMessage = "El nombre de usuario es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string NombreUsuario { get; set; } = "";

        // Mismo formato que valida el backend (RN-CU-01): usuario@dominio.ext
        [Required(ErrorMessage = "El correo es obligatorio")]
        [RegularExpression(@"^[^\s@]+@[^\s@]+\.[^\s@]+$", ErrorMessage = "Ingrese un correo válido (usuario@dominio.ext)")]
        [StringLength(100, ErrorMessage = "Máximo 100 caracteres")]
        public string Correo { get; set; } = "";

        // Al registrar es obligatoria; al editar, si se deja vacía se mantiene la contraseña actual
        public string? Password { get; set; }

        public string? ConfirmarPassword { get; set; }

        [Required(ErrorMessage = "El DNI es obligatorio")]
        [RegularExpression(@"^\d{8}$", ErrorMessage = "El DNI debe tener 8 dígitos")]
        public string Dni { get; set; } = "";

        [Required(ErrorMessage = "Los nombres son obligatorios")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string Nombres { get; set; } = "";

        [Required(ErrorMessage = "El apellido paterno es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string ApellidoPaterno { get; set; } = "";

        [Required(ErrorMessage = "El apellido materno es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string ApellidoMaterno { get; set; } = "";

        [Required(ErrorMessage = "El teléfono es obligatorio")]
        [RegularExpression(@"^\d{9}$", ErrorMessage = "El teléfono debe tener 9 dígitos")]
        public string Telefono { get; set; } = "";

        public string NombreCompleto => $"{Nombres} {ApellidoPaterno} {ApellidoMaterno}";

        // Reglas que dependen de varios campos. Cada mensaje se muestra debajo de su campo.
        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            if (Id == 0 && string.IsNullOrWhiteSpace(Password)) {
                yield return new ValidationResult("La contraseña es obligatoria", [nameof(Password)]);
            }
            if (!string.IsNullOrEmpty(Password) && (Password.Length < 8 || Password.Length > 64)) {
                yield return new ValidationResult("La contraseña debe tener entre 8 y 64 caracteres", [nameof(Password)]);
            }
            if ((Password ?? "") != (ConfirmarPassword ?? "")) {
                yield return new ValidationResult("Las contraseñas no coinciden", [nameof(ConfirmarPassword)]);
            }
        }
    }
}
