using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Reste;

public class CategoriaRestService : RegistroRestService<CategoriaViewModel, CategoriaDTO>, ICategoriaService {
    public CategoriaRestService(IConfiguration configuration) : base(configuration, "categorias") { }
    protected override CategoriaViewModel ToViewModel(CategoriaDTO dto) => RestMapper.ToViewModel(dto);
    protected override CategoriaDTO ToDto(CategoriaViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(CategoriaViewModel modelo) => modelo.Id;
    protected override void SetId(CategoriaViewModel modelo, CategoriaDTO dto) => modelo.Id = dto.Id;
}
