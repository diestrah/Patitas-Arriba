using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Reste;

public class ClienteRestService : RegistroRestService<ClienteViewModel, ClienteDTO>, IClienteService {
    public ClienteRestService(IConfiguration configuration) : base(configuration, "clientes") { }
    protected override ClienteViewModel ToViewModel(ClienteDTO dto) => RestMapper.ToViewModel(dto);
    protected override ClienteDTO ToDto(ClienteViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(ClienteViewModel modelo) => modelo.Id;
    protected override void SetId(ClienteViewModel modelo, ClienteDTO dto) => modelo.Id = dto.Id;
    public Task<ClienteViewModel> FindByDni(string valor) => FindOne("dni", valor);
    // Java requiere una cuenta existente; conservamos el id para no duplicarla si se reintenta.
    // Son dos solicitudes HTTP: Java no ofrece una transacción conjunta cuenta + persona.
    protected override async Task BeforeSave(ClienteViewModel modelo) {
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
