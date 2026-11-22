namespace PatitasArriba.Web.DTOs;

// GET /atenciones/estados: de qué servicio de cita es cada atención y si sigue activa (false: anulada)
public class EstadoAtencionDTO {
    public int IdAtencion { get; set; }
    public int IdDetalleCita { get; set; }
    public bool Activo { get; set; }
}
