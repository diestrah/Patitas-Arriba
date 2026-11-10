namespace PatitasArriba.Web.DTOs;

public class DetalleBoletaArticuloDTO {

    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public int Cantidad { get; set; }
    public double PrecioUnitario { get; set; }
    public double Subtotal { get; set; }
    public ArticuloDTO Articulo { get; set; }
    public InsumoUtilizadoDTO InsumoUtilizado{ get; set; }
}

