namespace PatitasArriba.Web.ViewModels {
    public class AtencionDiagnosticoViewModel {
        public int IdDiagnostico { get; set; }
        public string NombreEnfermedad { get; set; } = "";
        public NivelGravedad NivelGravedad { get; set; }
        public string? DetalleDiagnostico { get; set; }
    }
}
