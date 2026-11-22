using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IAtencionService : IRegistroService<AtencionViewModel> {
    Task<List<AtencionViewModel>> FindByMascota(int IdMascota);

    // Id del servicio de la cita (detalle_cita) -> id de su atención. Sus claves son los servicios ya atendidos
    // Qué servicios de cita tienen atención, activa o anulada (GET /atenciones/estados)
    Task<AtencionesDeCitas> AtencionPorDetalleCita();
}

