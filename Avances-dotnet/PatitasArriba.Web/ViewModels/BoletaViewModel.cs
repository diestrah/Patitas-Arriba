using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class BoletaViewModel : IValidatableObject {
        public int Id { get; set; }
        public string SerieCorrelativo { get; set; } = "";
        public DateTime Fecha { get; set; } = DateTime.Now;

        [Range(1, int.MaxValue, ErrorMessage = "Busque al cliente por su DNI")]
        public int IdCliente { get; set; }
        public string NombreCliente { get; set; } = "";
        public string DniCliente { get; set; } = "";

        public MetodoPago MetodoPago { get; set; }

        // false = boleta anulada
        public bool IsActivo { get; set; } = true;

        // Servicios realizados, insumos cobrados y artículos vendidos en caja
        public List<LineaBoletaViewModel> Lineas { get; set; } = [];

        public double TotalServicios => Lineas.Where(l => l.Tipo == "Servicio").Sum(l => l.SubTotal);
        public double TotalArticulos => Lineas.Where(l => l.Tipo != "Servicio").Sum(l => l.SubTotal);
        public double Total => TotalServicios + TotalArticulos;

        // RN-BO-01: toda boleta tiene al menos una línea
        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            if (IdCliente > 0 && Lineas.Count == 0) {
                yield return new ValidationResult("La boleta debe tener al menos un concepto a cobrar", [nameof(Lineas)]);
            }
        }
    }
}
