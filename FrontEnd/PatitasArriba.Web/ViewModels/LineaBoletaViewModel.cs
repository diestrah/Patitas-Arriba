namespace PatitasArriba.Web.ViewModels {
    // Una línea de la boleta. Los id dicen al backend qué se cobra (BoletaDTO):
    //   "Servicio" -> IdDetalleCita (detallesServicio)
    //   "Insumo"   -> IdArticulo e IdInsumoUtilizado (detallesArticulo con insumoUtilizado)
    //   "Artículo" -> IdArticulo (detallesArticulo, venta en caja: descuenta stock)
    public class LineaBoletaViewModel {
        public string Tipo { get; set; } = "Artículo";
        public int? IdDetalleCita { get; set; }
        public int? IdArticulo { get; set; }
        public int? IdInsumoUtilizado { get; set; }
        public string Descripcion { get; set; } = "";
        public int Cantidad { get; set; } = 1;
        public double PrecioUnitario { get; set; }

        public double SubTotal => Cantidad * PrecioUnitario;
    }
}
