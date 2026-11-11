using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Reste;

public class CitaRestService : RegistroRestService<CitaViewModel, CitaDTO>, ICitaService {
    public CitaRestService(IConfiguration configuration) : base(configuration, "citas") { }
    protected override CitaViewModel ToViewModel(CitaDTO dto) => RestMapper.ToViewModel(dto);
    protected override CitaDTO ToDto(CitaViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(CitaViewModel modelo) => modelo.Id;
    protected override void SetId(CitaViewModel modelo, CitaDTO dto) => modelo.Id = dto.Id;
    public Task<List<CitaViewModel>> FindByCliente(int id) => FindList($"{RecursoUrl}?cliente={id}");
    public Task<List<CitaViewModel>> FindByFecha(DateOnly fecha, int? idVeterinario = null) =>
        FindList($"{RecursoUrl}?fecha={fecha.ToString("yyyy-MM-dd", System.Globalization.CultureInfo.InvariantCulture)}"
            + (idVeterinario.HasValue ? $"&veterinario={idVeterinario.Value}" : ""));
}
