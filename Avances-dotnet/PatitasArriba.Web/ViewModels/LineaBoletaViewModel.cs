namespace PatitasArriba.Web.ViewModels {
    public class LineaBoletaViewModel {
        // "Servicio", "Insumo" (usado en una atención y cobrable) o "Artículo" (venta directa en caja)
        public string Tipo { get; set; } = "Artículo";
        public string Descripcion { get; set; } = "";
        public int Cantidad { get; set; } = 1;
        public double PrecioUnitario { get; set; }

        public double SubTotal => Cantidad * PrecioUnitario;
    }
}
