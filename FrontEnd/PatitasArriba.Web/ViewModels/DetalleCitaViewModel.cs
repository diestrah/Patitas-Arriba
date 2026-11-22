namespace PatitasArriba.Web.ViewModels {
    public class DetalleCitaViewModel {
        public int Id { get; set; }
        public int IdServicio { get; set; }
        public string NombreServicio { get; set; } = "";
        public int IdVeterinario { get; set; }
        public string NombreVeterinario { get; set; } = "";
        public string? Observaciones { get; set; }
    }
}
