using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Reste;

public class VeterinarioRestService : RegistroRestService<VeterinarioViewModel, VeterinarioDTO>, IVeterinarioService {
    public VeterinarioRestService(IConfiguration configuration) : base(configuration, "veterinarios") { }
    protected override VeterinarioViewModel ToViewModel(VeterinarioDTO dto) => RestMapper.ToViewModel(dto);
    protected override VeterinarioDTO ToDto(VeterinarioViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(VeterinarioViewModel modelo) => modelo.Id;
    protected override void SetId(VeterinarioViewModel modelo, VeterinarioDTO dto) => modelo.Id = dto.Id;
    public Task<VeterinarioViewModel> FindByDni(string valor) => FindOne("dni", valor);
    // Java requiere una cuenta existente; conservamos el id para no duplicarla si se reintenta.
    // Son dos solicitudes HTTP: Java no ofrece una transacción conjunta cuenta + persona.
    protected override async Task BeforeSave(VeterinarioViewModel modelo) {
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
