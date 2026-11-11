namespace PatitasArriba.Web.DTOs;

// Body de POST /acceso/registro (auto-registro de un cliente)
public class RegistroDTO {
    public string? Dni { get; set; }
    public string? Nombres { get; set; }
    public string? ApellidoPaterno { get; set; }
    public string? ApellidoMaterno { get; set; }
    public string? Telefono { get; set; }
    public string? Correo { get; set; }
    public string? Password { get; set; }
}
