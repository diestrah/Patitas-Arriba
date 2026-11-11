using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IVeterinarioService : IRegistroService<VeterinarioViewModel>{
    Task<VeterinarioViewModel> FindByDni(string Dni);
}

