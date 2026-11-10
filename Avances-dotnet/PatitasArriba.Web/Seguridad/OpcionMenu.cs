namespace PatitasArriba.Web.Seguridad {
    public class OpcionMenu {
        public string Texto { get; set; } = "";
        public string Href { get; set; } = "";
        public string Permiso { get; set; } = "";

        // Nombre del ícono (ver Components/Shared/Icono.razor)
        public string Icono { get; set; } = "list";
    }
}
