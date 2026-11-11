namespace PatitasArriba.Web.ViewModels;

// Resultado de POST /login: Id es la cuenta; IdPersona es su propietario.
public class SesionViewModel {
    public int Id { get; set; }
    public int IdPersona { get; set; }
    public string Correo { get; set; } = "";
    public string NombreUsuario { get; set; } = "";
    public RolUsuario Rol { get; set; }
    public string Nombres { get; set; } = "";
    public string? ApellidoPaterno { get; set; }
}
