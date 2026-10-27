using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Datos comunes de clientes, veterinarios y administradores
    public class PersonaViewModel {
        public int Id { get; set; }

        [Range(1, int.MaxValue, ErrorMessage = "Seleccione una cuenta de usuario")]
        public int IdCuenta { get; set; }

        public string NombreUsuario { get; set; } = "";

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
    }
}
