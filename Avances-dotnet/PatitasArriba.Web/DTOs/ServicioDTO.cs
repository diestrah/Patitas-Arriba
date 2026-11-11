namespace PatitasArriba.Web.DTOs;

public class ServicioDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public string? Nombre { get; set; }
    public double PrecioBase { get; set; }
    public string? Descripcion { get; set; }
    public string? Tipo { get; set; }
    public int DuracionEstimada { get; set; }
}

