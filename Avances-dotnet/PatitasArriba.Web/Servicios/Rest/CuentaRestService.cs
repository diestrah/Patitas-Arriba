using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class CuentaRestService : RegistroRestService<CuentaUsuarioViewModel, CuentaDTO>, ICuentaService {
    public CuentaRestService(IConfiguration configuration) : base(configuration, "cuentas") { }
    protected override CuentaUsuarioViewModel ToViewModel(CuentaDTO dto) => RestMapper.ToViewModel(dto);
    protected override CuentaDTO ToDto(CuentaUsuarioViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(CuentaUsuarioViewModel modelo) => modelo.Id;
    protected override void SetId(CuentaUsuarioViewModel modelo, CuentaDTO dto) => modelo.Id = dto.Id;
    public Task<CuentaUsuarioViewModel> FindByNombreUsuario(string valor) => FindOne("nombreUsuario", valor);
    private readonly Dictionary<int, (RolUsuario Rol, string Nombre)> propietarios = [];
    protected override async Task PrepareRead() {
        propietarios.Clear();
        foreach (var c in await HttpUtils.DoGet<List<ClienteDTO>>($"{BaseUrl}/clientes"))
            if (c.Cuenta != null) propietarios[c.Cuenta.Id] = (RolUsuario.CLIENTE, RestMapper.Nombre(c));
        foreach (var v in await HttpUtils.DoGet<List<VeterinarioDTO>>($"{BaseUrl}/veterinarios"))
            if (v.Cuenta != null) propietarios[v.Cuenta.Id] = (RolUsuario.VETERINARIO, RestMapper.Nombre(v));
        foreach (var a in await HttpUtils.DoGet<List<AdministradorDTO>>($"{BaseUrl}/administradores"))
            if (a.Cuenta != null) propietarios[a.Cuenta.Id] = (RolUsuario.ADMINISTRADOR, RestMapper.Nombre(a));
    }
    protected override Task<CuentaUsuarioViewModel> ConvertAsync(CuentaDTO dto) {
        var modelo = ToViewModel(dto);
        if (propietarios.TryGetValue(dto.Id, out var persona)) {
            modelo.Rol = persona.Rol;
            modelo.NombrePersona = persona.Nombre;
        }
        return Task.FromResult(modelo);
    }
    public async Task<SesionViewModel> Login(string correo, string password) {
        var dto = await HttpUtils.DoPost<object, SesionDTO>($"{BaseUrl}/login", new { correo, password });
        return new SesionViewModel {
            Id = dto.Id, IdPersona = dto.IdPersona, NombreUsuario = dto.NombreUsuario ?? "",
            Rol = RestMapper.EnumValue<RolUsuario>(dto.Rol), Nombres = dto.Nombres ?? "",
            ApellidoPaterno = dto.ApellidoPaterno
        };
    }
}
