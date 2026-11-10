namespace PatitasArriba.Web.DTOs;

public class DetalleRecetaDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public ArticuloDTO Articulo { get; set; }
    public String Dosis { get; set; }
    public String Frecuencia { get; set; }
    public int DuracionDias { get; set; }
    public int CantidadTotal { get; set; }
}
