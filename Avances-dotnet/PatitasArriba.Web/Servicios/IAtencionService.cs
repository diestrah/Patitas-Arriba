using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IAtencionService : IRegistroService<AtencionViewModel> {
    Task<List<AtencionViewModel>> FindByMascota(int IdMascota);
}

