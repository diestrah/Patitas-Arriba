namespace PatitasArriba.Web.DTOs;

public class DetalleCitaDTO {
    public int Id { get; set; }
    public bool Activo { get; set; } = true;
    public string Observaciones { get; set; }
    public ServicioDTO Servicio { get; set; }
    public VeterinarioDTO Veterinario { get; set; }
}

