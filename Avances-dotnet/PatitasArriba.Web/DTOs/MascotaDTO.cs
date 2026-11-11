namespace PatitasArriba.Web.DTOs;

public class MascotaDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public ClienteDTO? Cliente { get; set; }
    public string? Nombre { get; set; }
    public string? Sexo { get; set; }
    public double Peso { get; set; }
    public string? FechaNacimiento { get; set; }
    public string? Raza { get; set; }
    public string? TipoMascota { get; set; }
    // Imagen "data:image/...;base64,..." o null (RN-MA-07)
    public string? Foto { get; set; }
}

