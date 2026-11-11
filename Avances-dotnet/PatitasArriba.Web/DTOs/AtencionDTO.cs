namespace PatitasArriba.Web.DTOs;

public class AtencionDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public DetalleCitaDTO? DetalleCita { get; set; }
    public string? FechaHora { get; set; }
    public string? MotivoConsulta { get; set; }
    public string? Observaciones { get; set; }
    public List<InsumoUtilizadoDTO> InsumosUtilizados { get; set; } = [];
    public List<AtencionDiagnosticoDTO> Diagnosticos { get; set; } = [];

}
