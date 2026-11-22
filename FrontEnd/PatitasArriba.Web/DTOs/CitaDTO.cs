namespace PatitasArriba.Web.DTOs;

public class CitaDTO {

    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public string? FechaHora { get; set; }
    public string? Estado { get; set; }
    public MascotaDTO? Mascota { get; set; }
    public AdministradorDTO? Administrador { get; set; }
    public bool EsEmergencia { get; set; }
    public List<DetalleCitaDTO> Detalles { get; set; } = [];

}

