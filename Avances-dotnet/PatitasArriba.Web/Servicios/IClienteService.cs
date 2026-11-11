using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IClienteService : IRegistroService<ClienteViewModel> {
    // null si no hay un cliente activo con ese DNI
    Task<ClienteViewModel?> FindByDni(string Dni);
}
