using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Reste;

public class AtencionRestService : RegistroRestService<AtencionViewModel, AtencionDTO>, IAtencionService {
    public AtencionRestService(IConfiguration configuration) : base(configuration, "atenciones") { }
    protected override AtencionViewModel ToViewModel(AtencionDTO dto) => RestMapper.ToViewModel(dto);
    protected override AtencionDTO ToDto(AtencionViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(AtencionViewModel modelo) => modelo.Id;
    protected override void SetId(AtencionViewModel modelo, AtencionDTO dto) => modelo.Id = dto.Id;
    public Task<List<AtencionViewModel>> FindByMascota(int id) => FindList($"{RecursoUrl}?mascota={id}");
    private List<CitaDTO> citas = [];
    protected override async Task PrepareRead() => citas = await HttpUtils.DoGet<List<CitaDTO>>($"{BaseUrl}/citas");
    protected override Task<AtencionViewModel> ConvertAsync(AtencionDTO dto) {
        var modelo = ToViewModel(dto);
        var cita = citas.FirstOrDefault(c => c.Detalles.Any(d => d.Id == modelo.IdDetalleCita));
        modelo.IdMascota = cita?.Mascota?.Id ?? 0;
        modelo.NombreMascota = cita?.Mascota?.Nombre ?? "";
        modelo.NombreCliente = RestMapper.Nombre(cita?.Mascota?.Cliente);
        return Task.FromResult(modelo);
    }
}
