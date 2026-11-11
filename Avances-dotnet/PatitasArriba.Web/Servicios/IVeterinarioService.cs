using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IVeterinarioService : IRegistroService<VeterinarioViewModel>{
    // null si no hay un veterinario activo con ese DNI
    Task<VeterinarioViewModel?> FindByDni(string Dni);
}

