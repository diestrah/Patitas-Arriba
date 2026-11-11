namespace PatitasArriba.Web.DTOs;

public class CategoriaDTO {

    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public string? Nombre { get; set; }
    public string? Descripcion { get; set; }
}

