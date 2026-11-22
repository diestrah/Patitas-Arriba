namespace PatitasArriba.Web.ViewModels {
    // Opción para listas de casillas (categorías de un artículo, personal de un horario, etc.)
    public class OpcionSeleccion {
        public int Id { get; set; }
        public string Texto { get; set; } = "";
        public string Detalle { get; set; } = "";
        public bool Seleccionado { get; set; }
    }
}
