using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Auto-registro de un cliente (RF19). Al enlazar: POST /acceso/registro; la cuenta se crea al abrir el enlace del correo.
    // Las reglas son las del backend (RN-AC-01): DNI de 8 dígitos, teléfono de 9 y contraseña de al menos 8 caracteres.
    public class RegistroViewModel {
        [Required(ErrorMessage = "El DNI es obligatorio")]
        [RegularExpression(@"^\d*$", ErrorMessage = "El DNI solo puede tener números")]
        [StringLength(8, MinimumLength = 8, ErrorMessage = "El DNI debe tener exactamente 8 dígitos")]
        public string Dni { get; set; } = "";

        [Required(ErrorMessage = "Los nombres son obligatorios")]
        [RegularExpression(@"^\p{L}+(?:[ \'\-]\p{L}+)*$", ErrorMessage = "Los nombres solo pueden tener letras y espacios")]
        [StringLength(50, ErrorMessage = "Máximo 50 caracteres")]
        public string Nombres { get; set; } = "";

        [Required(ErrorMessage = "El apellido paterno es obligatorio")]
        [RegularExpression(@"^\p{L}+(?:[ \'\-]\p{L}+)*$", ErrorMessage = "El apellido solo puede tener letras y espacios")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string ApellidoPaterno { get; set; } = "";

        [Required(ErrorMessage = "El apellido materno es obligatorio")]
        [RegularExpression(@"^\p{L}+(?:[ \'\-]\p{L}+)*$", ErrorMessage = "El apellido solo puede tener letras y espacios")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string ApellidoMaterno { get; set; } = "";

        [Required(ErrorMessage = "El teléfono es obligatorio")]
        [RegularExpression(@"^\d*$", ErrorMessage = "El teléfono solo puede tener números")]
        [StringLength(9, MinimumLength = 9, ErrorMessage = "El teléfono debe tener exactamente 9 dígitos")]
        public string Telefono { get; set; } = "";

        [Required(ErrorMessage = "El correo es obligatorio")]
        [RegularExpression(@"^[^\s@]+@[^\s@]+\.[^\s@]+$", ErrorMessage = "Ingrese un correo válido, por ejemplo nombre@correo.com")]
        [StringLength(100, ErrorMessage = "Máximo 100 caracteres")]
        public string Correo { get; set; } = "";

        [Required(ErrorMessage = "La contraseña es obligatoria")]
        [MinLength(8, ErrorMessage = "La contraseña debe tener al menos 8 caracteres")]
        [StringLength(64, ErrorMessage = "Máximo 64 caracteres")]
        public string Password { get; set; } = "";

        [Required(ErrorMessage = "Confirme la contraseña")]
        [Compare(nameof(Password), ErrorMessage = "Las contraseñas no coinciden")]
        public string ConfirmarPassword { get; set; } = "";
    }
}
