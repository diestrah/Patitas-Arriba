using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class CuentaUsuarioViewModel {
        public int Id { get; set; }

        [Required(ErrorMessage = "El nombre de usuario es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string NombreUsuario { get; set; } = "";

        [Required(ErrorMessage = "El correo es obligatorio")]
        [EmailAddress(ErrorMessage = "Ingrese un correo válido")]
        [StringLength(100, ErrorMessage = "Máximo 100 caracteres")]
        public string Correo { get; set; } = "";

        // Al editar se puede dejar vacía para mantener la contraseña actual
        [StringLength(64, MinimumLength = 8, ErrorMessage = "La contraseña debe tener entre 8 y 64 caracteres")]
        public string? Password { get; set; }

        // El rol se deduce de a quién pertenece la cuenta (cliente, veterinario o administrador)
        public RolUsuario Rol { get; set; }
        public string NombrePersona { get; set; } = "";
    }
}
