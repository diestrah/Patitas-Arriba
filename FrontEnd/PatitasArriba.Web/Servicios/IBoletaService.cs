using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IBoletaService : IRegistroService<BoletaViewModel> {
    // GET /boletas/pendientes?cliente={id}: lo que el cliente todavía no paga (RN-BO-07, RN-BO-08)
    Task<List<LineaPendiente>> Pendientes(int idCliente);
}

// Una línea pendiente de cobro y el servicio de la cita del que viene (para saber de qué mascota es)
public record LineaPendiente(LineaBoletaViewModel Linea, int IdDetalleCita);
