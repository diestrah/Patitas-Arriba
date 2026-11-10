namespace PatitasArriba.Web.DTOs;

public class BoletaDTO {

    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public ClienteDTO Cliente { get; set; }
    public string SerieCorrelativo { get; set; }
    public string Fecha{ get; set; }
    public double Total{ get; set; }
    public string MetodoPago { get; set; }
    public List<DetalleBoletaArticuloDTO> DetallesArticulo { get; set; } = [];
    public List<DetalleBoletaServicioDTO> DetallesServicio { get; set; } = [];

}

