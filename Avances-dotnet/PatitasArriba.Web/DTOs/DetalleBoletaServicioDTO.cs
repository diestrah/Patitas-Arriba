namespace PatitasArriba.Web.DTOs;

public class DetalleBoletaServicioDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public int Cantidad { get; set; }
    public double PrecioUnitario { get; set; }
    public double Subtotal { get; set; }
    public DetalleCitaDTO DetalleCita{ get; set; }

}

