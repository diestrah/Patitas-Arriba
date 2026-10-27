using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class ArticuloViewModel {
        public int Id { get; set; }

        [Required(ErrorMessage = "El nombre es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string Nombre { get; set; } = "";

        [StringLength(200, ErrorMessage = "Máximo 200 caracteres")]
        public string? Descripcion { get; set; }

        [Range(0.01, 99999, ErrorMessage = "El precio debe ser mayor a 0")]
        public double PrecioBase { get; set; }

        [Range(0, 99999, ErrorMessage = "El stock no puede ser negativo")]
        public int StockActual { get; set; }

        [Range(0, 99999, ErrorMessage = "El stock mínimo no puede ser negativo")]
        public int StockMinimo { get; set; }

        // Todas las categorías, marcadas las que tiene el artículo
        public List<OpcionSeleccion> Categorias { get; set; } = [];

        public string NombresCategorias => string.Join(", ", Categorias.Where(c => c.Seleccionado).Select(c => c.Texto));
    }
}
