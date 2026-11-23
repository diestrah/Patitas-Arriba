using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IServicioService : IRegistroService<ServicioViewModel>{
    Task<ServicioViewModel> FindByName(string Name);
}
