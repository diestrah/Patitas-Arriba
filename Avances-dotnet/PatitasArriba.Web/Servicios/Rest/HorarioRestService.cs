using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Reste;

public class HorarioRestService : RegistroRestService<HorarioViewModel, HorarioDTO>, IHorarioService {
    public HorarioRestService(IConfiguration configuration) : base(configuration, "horarios") { }
    protected override HorarioViewModel ToViewModel(HorarioDTO dto) => RestMapper.ToViewModel(dto);
    protected override HorarioDTO ToDto(HorarioViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(HorarioViewModel modelo) => modelo.Id;
    protected override void SetId(HorarioViewModel modelo, HorarioDTO dto) => modelo.Id = dto.Id;
}
