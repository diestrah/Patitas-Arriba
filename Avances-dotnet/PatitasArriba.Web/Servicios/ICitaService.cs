using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface ICitaService : IRegistroService<CitaViewModel>{
    Task<List<CitaViewModel>> FindByCliente(int Id);
    Task<List<CitaViewModel>> FindByFecha(DateOnly fecha, int? idVeterinario = null);
}
