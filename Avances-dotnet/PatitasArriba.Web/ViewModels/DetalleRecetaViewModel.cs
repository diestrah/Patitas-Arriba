namespace PatitasArriba.Web.ViewModels {
    public class DetalleRecetaViewModel {
        public int IdArticulo { get; set; }
        public string NombreArticulo { get; set; } = "";
        public string Dosis { get; set; } = "";
        public string Frecuencia { get; set; } = "";
        public int DuracionDias { get; set; }
        public int CantidadTotal { get; set; }
    }
}
