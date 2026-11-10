namespace PatitasArriba.Web.DTOs;

public class InsumoUtilizado {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public ArticuloDTO Articulo { get; set; }
    public int CantidadUtilizada { get; set; }
    public bool EsCobrable { get; set; }
}

