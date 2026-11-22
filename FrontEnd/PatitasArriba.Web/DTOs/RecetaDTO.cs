namespace PatitasArriba.Web.DTOs;

public class RecetaDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public AtencionDTO? Atencion { get; set; }
    public string? FechaEmision { get; set; }
    public string? IndicacionesGenerales { get; set; }
    public List<DetalleRecetaDTO> Detalles { get; set; } = [];
}
