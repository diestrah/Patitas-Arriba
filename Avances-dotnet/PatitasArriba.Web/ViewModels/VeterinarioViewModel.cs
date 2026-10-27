using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class VeterinarioViewModel : PersonaViewModel {
        [Required(ErrorMessage = "El número de colegiatura es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string NumeroColegiatura { get; set; } = "";
    }
}
