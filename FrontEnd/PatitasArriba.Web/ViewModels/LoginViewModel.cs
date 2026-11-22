using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class LoginViewModel {
        // Se ingresa con el correo de la cuenta (RF01): el rol se deduce de la cuenta, no se elige
        [Required(ErrorMessage = "El correo es obligatorio")]
        [RegularExpression(@"^[^\s@]+@[^\s@]+\.[^\s@]+$", ErrorMessage = "Ingrese un correo válido")]
        public string Correo { get; set; } = "";

        [Required(ErrorMessage = "La contraseña es obligatoria")]
        public string Password { get; set; } = "";
    }
}
