using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IMascotaService : IRegistroService<MascotaViewModel>{
    Task<List<MascotaViewModel>> FindByCliente(int IdCliente);
}

