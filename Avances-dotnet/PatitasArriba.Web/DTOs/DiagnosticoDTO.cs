namespace PatitasArriba.Web.DTOs;

public class DiagnosticoDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public string NombreEnfermedad { get; set; }
    public string Descripcion { get; set; }
}

