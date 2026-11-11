using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IClienteService : IRegistroService<ClienteViewModel> {
    Task<ClienteViewModel> FindByDni(string Dni);
}
