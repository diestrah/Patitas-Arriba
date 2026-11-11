using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Rest;

public class ArticuloRestService : RegistroRestService<ArticuloViewModel, ArticuloDTO>, IArticuloService {
    public ArticuloRestService(IConfiguration configuration) : base(configuration, "articulos") { }
    protected override ArticuloViewModel ToViewModel(ArticuloDTO dto) => RestMapper.ToViewModel(dto);
    protected override ArticuloDTO ToDto(ArticuloViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(ArticuloViewModel modelo) => modelo.Id;
    protected override void SetId(ArticuloViewModel modelo, ArticuloDTO dto) => modelo.Id = dto.Id;
}
