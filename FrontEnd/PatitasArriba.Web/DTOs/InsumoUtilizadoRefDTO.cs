namespace PatitasArriba.Web.DTOs;

public class InsumoUtilizadoRefDTO {
    public int Id { get; set; }
    public int IdAtencion { get; set; }
    // Solo de salida, en GET /boletas/pendientes: de qué servicio de la cita viene el insumo
    public int? IdDetalleCita { get; set; }
}
