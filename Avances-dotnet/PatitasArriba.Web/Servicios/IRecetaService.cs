using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IRecetaService : IRegistroService<RecetaViewModel>{
    Task<List<RecetaViewModel>> FindByAtencion(int IdAtencion);
}
