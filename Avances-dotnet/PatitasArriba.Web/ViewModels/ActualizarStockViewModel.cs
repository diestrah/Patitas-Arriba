using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class ActualizarStockViewModel {
        public int IdArticulo { get; set; }
        public string NombreArticulo { get; set; } = "";
        public int StockActual { get; set; }

        [Range(0, 99999, ErrorMessage = "El stock no puede ser negativo")]
        public int NuevoStock { get; set; }
    }
}
