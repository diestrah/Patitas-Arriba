using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class RecetaViewModel {
        public int Id { get; set; }
        public int IdAtencion { get; set; }
        public string NombreMascota { get; set; } = "";
        public TipoMascota TipoMascota { get; set; }
        public string NombreCliente { get; set; } = "";
        public string NombreVeterinario { get; set; } = "";
        public string NumeroColegiatura { get; set; } = "";
        public string Diagnostico { get; set; } = "";

        [Required(ErrorMessage = "Indique la fecha de emisión")]
        public DateOnly? FechaEmision { get; set; }

        [StringLength(500, ErrorMessage = "Máximo 500 caracteres")]
        public string? IndicacionesGenerales { get; set; }

        public List<DetalleRecetaViewModel> Medicamentos { get; set; } = [];
    }
}
