using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class ServicioViewModel {
        public int Id { get; set; }

        [Required(ErrorMessage = "El nombre es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string Nombre { get; set; } = "";

        [StringLength(200, ErrorMessage = "Máximo 200 caracteres")]
        public string? Descripcion { get; set; }

        [Range(0.01, 99999, ErrorMessage = "El precio debe ser mayor a 0")]
        public double PrecioBase { get; set; }

        [Range(5, 600, ErrorMessage = "La duración debe estar entre 5 y 600 minutos")]
        public int? DuracionEstimada { get; set; }

        public TipoServicio TipoServicio { get; set; }
    }
}
