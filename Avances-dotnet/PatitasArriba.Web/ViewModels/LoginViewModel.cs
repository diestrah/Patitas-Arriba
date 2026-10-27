using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class LoginViewModel {
        [Required(ErrorMessage = "Ingrese su usuario")]
        public string NombreUsuario { get; set; } = "";

        [Required(ErrorMessage = "Ingrese su contraseña")]
        public string Password { get; set; } = "";

        // Solo para el Lab 9: simula el rol de la cuenta
        public RolUsuario Perfil { get; set; } = RolUsuario.ADMINISTRADOR;
    }
}
