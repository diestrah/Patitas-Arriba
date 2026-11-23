using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IAdministradorService : IRegistroService<AdministradorViewModel> {
   Task<AdministradorViewModel?> FindByDni(string Dni);
}
