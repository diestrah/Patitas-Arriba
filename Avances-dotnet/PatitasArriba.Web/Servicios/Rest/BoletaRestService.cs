using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Rest;

public class BoletaRestService : RegistroRestService<BoletaViewModel, BoletaDTO>, IBoletaService {
    public BoletaRestService(IConfiguration configuration) : base(configuration, "boletas") { }
    protected override BoletaViewModel ToViewModel(BoletaDTO dto) => RestMapper.ToViewModel(dto);
    protected override BoletaDTO ToDto(BoletaViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(BoletaViewModel modelo) => modelo.Id;
    protected override void SetId(BoletaViewModel modelo, BoletaDTO dto) => modelo.Id = dto.Id;
}
