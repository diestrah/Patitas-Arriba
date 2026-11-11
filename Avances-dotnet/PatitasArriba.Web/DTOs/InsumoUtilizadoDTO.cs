namespace PatitasArriba.Web.DTOs;

public class InsumoUtilizadoDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public ArticuloDTO? Articulo { get; set; }
    public int CantidadUtilizada { get; set; }
    public bool EsCobrable { get; set; }
}

