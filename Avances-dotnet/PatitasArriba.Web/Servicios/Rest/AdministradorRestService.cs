using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class AdministradorRestService : RegistroRestService<AdministradorViewModel, AdministradorDTO>, IAdministradorService {
    public AdministradorRestService(IConfiguration configuration) : base(configuration, "administradores") { }
    protected override AdministradorViewModel ToViewModel(AdministradorDTO dto) => RestMapper.ToViewModel(dto);
    protected override AdministradorDTO ToDto(AdministradorViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(AdministradorViewModel modelo) => modelo.Id;
    protected override void SetId(AdministradorViewModel modelo, AdministradorDTO dto) => modelo.Id = dto.Id;
    public async Task<AdministradorViewModel?> FindByDni(string valor) {
        try { return await FindOne("dni", valor); }
        catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) { return null; }
    }
    // El administrador solo escribe el correo: Java crea la persona y su cuenta en una sola transacción (RN-AC-07)
    // y la persona elige su contraseña con el enlace que recibe por correo. Al modificar solo viaja el id de la cuenta.
    protected override string InsertUrl => $"{RecursoUrl}/con-cuenta";
}
