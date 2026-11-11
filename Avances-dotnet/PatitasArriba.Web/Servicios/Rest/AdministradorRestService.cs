using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Reste;

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
    // Java requiere una cuenta existente; conservamos el id para no duplicarla si se reintenta.
    // Son dos solicitudes HTTP: Java no ofrece una transacción conjunta cuenta + persona.
    protected override async Task BeforeSave(AdministradorViewModel modelo) {
        var cuenta = new CuentaDTO { Id = modelo.IdCuenta, NombreUsuario = modelo.NombreUsuario,
            Correo = modelo.Correo, Password = modelo.Password };
        if (modelo.IdCuenta == 0) {
            var creada = await HttpUtils.DoPost<CuentaDTO, CuentaDTO>($"{BaseUrl}/cuentas", cuenta);
            modelo.IdCuenta = creada.Id;
        } else {
            await HttpUtils.DoPut<CuentaDTO, CuentaDTO>($"{BaseUrl}/cuentas/{modelo.IdCuenta}", cuenta);
        }
    }
}
