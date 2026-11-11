namespace PatitasArriba.Web.DTOs;

public class VeterinarioDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public CuentaDTO? Cuenta { get; set; }
    public string? Dni { get; set; }
    public string? Nombres { get; set; }
    public string? ApellidoPaterno { get; set; }
    public string? ApellidoMaterno { get; set; }
    public string? Telefono { get; set; }
    public List<HorarioDTO> Horarios { get; set; } = [];
    public string? NumeroColegiatura { get; set; }
}

