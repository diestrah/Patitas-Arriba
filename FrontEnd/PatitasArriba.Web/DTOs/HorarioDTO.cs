namespace PatitasArriba.Web.DTOs;

public class HorarioDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public string? DiaSemana { get; set; }
    public string? HoraInicio { get; set; }
    public string? HoraFin { get; set; }
}

