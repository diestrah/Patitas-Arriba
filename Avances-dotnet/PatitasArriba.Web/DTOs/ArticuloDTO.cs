namespace PatitasArriba.Web.DTOs;

public class ArticuloDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public string? Nombre { get; set; }
    public double PrecioBase { get; set; }
    public string? Descripcion { get; set; }
    public int StockActual { get; set; }
    public int StockMinimo { get; set; }
    public List<CategoriaDTO> Categorias { get; set; } = [];

}

