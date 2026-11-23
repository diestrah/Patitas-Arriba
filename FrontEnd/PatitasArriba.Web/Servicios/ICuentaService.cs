using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface ICuentaService : IRegistroService<CuentaUsuarioViewModel>{
    Task<CuentaUsuarioViewModel> FindByNombreUsuario(string NombreUsuario);
    Task<SesionViewModel> Login(string correo, string password);
}

