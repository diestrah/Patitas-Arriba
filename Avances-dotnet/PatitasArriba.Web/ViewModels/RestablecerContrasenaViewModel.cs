using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Contraseña nueva con el enlace del correo (RF20), también para activar una cuenta creada por el administrador.
    // Al enlazar: POST /acceso/recuperacion/restablecer con el token del enlace
    public class RestablecerContrasenaViewModel {
        [Required(ErrorMessage = "La contraseña es obligatoria")]
        [MinLength(8, ErrorMessage = "La contraseña debe tener al menos 8 caracteres")]
        [StringLength(64, ErrorMessage = "Máximo 64 caracteres")]
        public string Password { get; set; } = "";

        [Required(ErrorMessage = "Confirme la contraseña")]
        [Compare(nameof(Password), ErrorMessage = "Las contraseñas no coinciden")]
        public string ConfirmarPassword { get; set; } = "";
    }
}
