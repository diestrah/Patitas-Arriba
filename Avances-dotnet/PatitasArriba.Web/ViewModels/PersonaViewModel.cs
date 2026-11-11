using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Datos comunes de clientes, veterinarios y administradores.
    // La cuenta se crea junto con la persona, en el mismo formulario: el administrador solo escribe el correo
    // y la persona elige su contraseña con el enlace que recibe (RN-AC-07). Al enlazar: POST /clientes|veterinarios|
    // administradores/con-cuenta, con cuenta: { correo }.
    public class PersonaViewModel {
        public int Id { get; set; }

        // Cuenta de la persona (0 mientras no se registra)
        public int IdCuenta { get; set; }

        // Lo arma el sistema con el correo (RN-AC-08): no se escribe en el formulario
        public string NombreUsuario { get; set; } = "";

        // Mismo formato que valida el backend (RN-CU-01): usuario@dominio.ext
        [Required(ErrorMessage = "El correo es obligatorio")]
        [RegularExpression(@"^[^\s@]+@[^\s@]+\.[^\s@]+$", ErrorMessage = "Ingrese un correo válido (usuario@dominio.ext)")]
        [StringLength(100, ErrorMessage = "Máximo 100 caracteres")]
        public string Correo { get; set; } = "";

        [Required(ErrorMessage = "El DNI es obligatorio")]
        [RegularExpression(@"^\d{8}$", ErrorMessage = "El DNI debe tener 8 dígitos")]
        public string Dni { get; set; } = "";

        [Required(ErrorMessage = "Los nombres son obligatorios")]
        [RegularExpression(@"^\p{L}+(?:[ \'\-]\p{L}+)*$", ErrorMessage = "Los nombres solo pueden tener letras y espacios")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
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
        [RegularExpression(@"^\d{9}$", ErrorMessage = "El teléfono debe tener 9 dígitos")]
        public string Telefono { get; set; } = "";

        public string NombreCompleto => $"{Nombres} {ApellidoPaterno} {ApellidoMaterno}";
    }
}
