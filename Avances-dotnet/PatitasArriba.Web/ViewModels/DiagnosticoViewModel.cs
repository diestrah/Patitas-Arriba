using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class DiagnosticoViewModel {
        public int Id { get; set; }

        [Required(ErrorMessage = "El nombre de la enfermedad es obligatorio")]
        [StringLength(80, ErrorMessage = "Máximo 80 caracteres")]
        public string NombreEnfermedad { get; set; } = "";

        [Required(ErrorMessage = "La descripción es obligatoria")]
        [StringLength(200, ErrorMessage = "Máximo 200 caracteres")]
        public string Descripcion { get; set; } = "";
    }
}
