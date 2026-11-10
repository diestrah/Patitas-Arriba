using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class LoginViewModel {
        // Usuario o correo de la cuenta: el rol se deduce de la cuenta, no se elige
        [Required(ErrorMessage = "Ingrese su usuario o correo")]
        public string NombreUsuario { get; set; } = "";

        [Required(ErrorMessage = "Ingrese su contraseña")]
        public string Password { get; set; } = "";
    }
}
