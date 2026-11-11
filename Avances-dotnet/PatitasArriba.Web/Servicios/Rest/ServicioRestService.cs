using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Reste;

public class ServicioRestService : RegistroRestService<ServicioViewModel, ServicioDTO>, IServicioService {
    public ServicioRestService(IConfiguration configuration) : base(configuration, "servicios") { }
    protected override ServicioViewModel ToViewModel(ServicioDTO dto) => RestMapper.ToViewModel(dto);
    protected override ServicioDTO ToDto(ServicioViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(ServicioViewModel modelo) => modelo.Id;
    protected override void SetId(ServicioViewModel modelo, ServicioDTO dto) => modelo.Id = dto.Id;
    public Task<ServicioViewModel> FindByName(string valor) => FindOne("nombre", valor);
}
