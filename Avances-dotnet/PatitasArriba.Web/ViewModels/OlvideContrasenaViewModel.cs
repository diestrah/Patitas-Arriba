using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Pedir el enlace para restablecer la contraseña (RF20). Al enlazar: POST /acceso/recuperacion
    public class OlvideContrasenaViewModel {
        [Required(ErrorMessage = "El correo es obligatorio")]
        [RegularExpression(@"^[^\s@]+@[^\s@]+\.[^\s@]+$", ErrorMessage = "Ingrese un correo válido, por ejemplo nombre@correo.com")]
        public string Correo { get; set; } = "";
    }
}
