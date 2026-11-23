using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class CategoriaViewModel {
        public int Id { get; set; }

        [Required(ErrorMessage = "El nombre es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string Nombre { get; set; } = "";

        [Required(ErrorMessage = "La descripción es obligatoria")]
        [StringLength(200, ErrorMessage = "Máximo 200 caracteres")]
        public string Descripcion { get; set; } = "";
    }
}
