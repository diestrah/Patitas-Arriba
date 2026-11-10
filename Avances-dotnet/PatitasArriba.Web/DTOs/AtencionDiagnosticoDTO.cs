namespace PatitasArriba.Web.DTOs;

public class AtencionDiagnosticoDTO {
    public bool Activo { get; set; } = true;
    public DiagnosticoDTO Diagnostico { get; set; }
    public string NivelGravedad { get; set; }
    public string DetalleDiagnostico { get; set; }

}