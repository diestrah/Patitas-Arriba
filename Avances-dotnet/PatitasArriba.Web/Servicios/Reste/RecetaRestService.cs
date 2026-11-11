using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Reste;

public class RecetaRestService : RegistroRestService<RecetaViewModel, RecetaDTO>, IRecetaService {
    public RecetaRestService(IConfiguration configuration) : base(configuration, "recetas") { }
    protected override RecetaViewModel ToViewModel(RecetaDTO dto) => RestMapper.ToViewModel(dto);
    protected override RecetaDTO ToDto(RecetaViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(RecetaViewModel modelo) => modelo.Id;
    protected override void SetId(RecetaViewModel modelo, RecetaDTO dto) => modelo.Id = dto.Id;
    public Task<List<RecetaViewModel>> FindByAtencion(int id) => FindList($"{RecursoUrl}?atencion={id}");
    private List<CitaDTO> citas = [];
    protected override async Task PrepareRead() => citas = await HttpUtils.DoGet<List<CitaDTO>>($"{BaseUrl}/citas");
    protected override Task<RecetaViewModel> ConvertAsync(RecetaDTO dto) {
        var modelo = ToViewModel(dto);
        var detalleId = dto.Atencion?.DetalleCita?.Id;
        var cita = citas.FirstOrDefault(c => c.Detalles.Any(d => d.Id == detalleId));
        modelo.NombreMascota = cita?.Mascota?.Nombre ?? "";
        modelo.NombreCliente = RestMapper.Nombre(cita?.Mascota?.Cliente);
        if (cita?.Mascota != null) modelo.TipoMascota = RestMapper.EnumValue<TipoMascota>(cita.Mascota.TipoMascota);
        return Task.FromResult(modelo);
    }
}
