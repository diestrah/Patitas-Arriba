namespace PatitasArriba.Web.DTOs;

public class SesionDTO {
    public int Id { get; set; }
    public int IdPersona { get; set; }
    public string? Correo { get; set; }
    public string? NombreUsuario { get; set; }
    public string? Rol { get; set; }
    public string? Nombres { get; set; }
    public string? ApellidoPaterno { get; set; }
}
