using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Rest;

public class MascotaRestService : RegistroRestService<MascotaViewModel, MascotaDTO>, IMascotaService {
    public MascotaRestService(IConfiguration configuration) : base(configuration, "mascotas") { }
    protected override MascotaViewModel ToViewModel(MascotaDTO dto) => RestMapper.ToViewModel(dto);
    protected override MascotaDTO ToDto(MascotaViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(MascotaViewModel modelo) => modelo.Id;
    protected override void SetId(MascotaViewModel modelo, MascotaDTO dto) => modelo.Id = dto.Id;
    public Task<List<MascotaViewModel>> FindByCliente(int id) => FindList($"{RecursoUrl}?cliente={id}");
}
