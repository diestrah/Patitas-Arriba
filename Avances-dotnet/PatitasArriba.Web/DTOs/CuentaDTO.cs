namespace PatitasArriba.Web.DTOs;

public class CuentaDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public string NombreUsuario { get; set; }
    public string Correo { get; set; }
    public string FechaCreacion { get; set; }
    public string Password { get; set; }
}

