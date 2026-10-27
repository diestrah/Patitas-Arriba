namespace PatitasArriba.Web.ViewModels {
    public class InsumoUtilizadoViewModel {
        public int Id { get; set; }
        public int IdArticulo { get; set; }
        public string NombreArticulo { get; set; } = "";
        public int CantidadUtilizada { get; set; } = 1;

        // true = se cobra aparte al cliente; false = ya está incluido en el precio del servicio
        public bool EsCobrable { get; set; }
        public double PrecioUnitario { get; set; }
    }
}
