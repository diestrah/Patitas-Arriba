using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class ClienteRestService : RegistroRestService<ClienteViewModel, ClienteDTO>, IClienteService {
    public ClienteRestService(IConfiguration configuration) : base(configuration, "clientes") { }
    protected override ClienteViewModel ToViewModel(ClienteDTO dto) => RestMapper.ToViewModel(dto);
    protected override ClienteDTO ToDto(ClienteViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(ClienteViewModel modelo) => modelo.Id;
    protected override void SetId(ClienteViewModel modelo, ClienteDTO dto) => modelo.Id = dto.Id;
    public Task<ClienteViewModel> FindByDni(string valor) => FindOne("dni", valor);
    // El administrador solo escribe el correo: Java crea la persona y su cuenta en una sola transacción (RN-AC-07)
    // y la persona elige su contraseña con el enlace que recibe por correo. Al modificar solo viaja el id de la cuenta.
    protected override string InsertUrl => $"{RecursoUrl}/con-cuenta";
}
