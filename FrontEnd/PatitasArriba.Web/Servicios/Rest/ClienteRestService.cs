using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class ClienteRestService : IClienteService {
    private string BaseUrl { get; }
    private string BaseUrlCliente { get; }

    public ClienteRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlCliente = $"{BaseUrl}/clientes";
    }

    public async Task<List<ClienteViewModel>> FindAll() {
        string url = BaseUrlCliente;
        List<ClienteDTO> dtos = await HttpUtils.DoGet<List<ClienteDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<ClienteViewModel?> FindById(int id) {
        string url = $"{BaseUrlCliente}/{id}";
        ClienteDTO dto;
        try {
            dto = await HttpUtils.DoGet<ClienteDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(ClienteViewModel modelo) {
        // Java registra la persona y su cuenta en una sola transacción y envía la invitación.
        string url = $"{BaseUrlCliente}/con-cuenta";
        ClienteDTO dto = ToDto(modelo);
        ClienteDTO creado = await HttpUtils.DoPost<ClienteDTO, ClienteDTO>(url, dto);
        modelo.Id = creado.Id;
        modelo.IdCuenta = creado.Cuenta?.Id ?? modelo.IdCuenta;
    }

    public async Task Update(ClienteViewModel modelo) {
        string url = $"{BaseUrlCliente}/{modelo.Id}";
        ClienteDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<ClienteDTO, ClienteDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlCliente}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<ClienteViewModel?> FindByDni(string valor) {
        string url = QueryHelpers.AddQueryString(BaseUrlCliente, "dni", valor);
        List<ClienteDTO> dtos;
        try {
            dtos = await HttpUtils.DoGet<List<ClienteDTO>>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        if (dtos.Count == 0) {
            return null;
        }
        return ToViewModel(dtos[0]);
    }

    private List<ClienteViewModel> ToViewModels(List<ClienteDTO> dtos) {
        List<ClienteViewModel> modelos = [];
        foreach (ClienteDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private ClienteViewModel ToViewModel(ClienteDTO dto) {
        ClienteViewModel modelo = new ClienteViewModel {
            Id = dto.Id,
            IdCuenta = dto.Cuenta?.Id ?? 0,
            NombreUsuario = dto.Cuenta?.NombreUsuario ?? "",
            Correo = dto.Cuenta?.Correo ?? "",
            Dni = dto.Dni ?? "",
            Nombres = dto.Nombres ?? "",
            ApellidoPaterno = dto.ApellidoPaterno ?? "",
            ApellidoMaterno = dto.ApellidoMaterno ?? "",
            Telefono = dto.Telefono ?? ""
        };
        return modelo;
    }

    private ClienteDTO ToDto(ClienteViewModel viewModel) {
        ClienteDTO dto = new ClienteDTO {
            Id = viewModel.Id,
            Cuenta = new CuentaDTO { Id = viewModel.IdCuenta, Correo = viewModel.Correo },
            Dni = viewModel.Dni,
            Nombres = viewModel.Nombres,
            ApellidoPaterno = viewModel.ApellidoPaterno,
            ApellidoMaterno = viewModel.ApellidoMaterno,
            Telefono = viewModel.Telefono
        };
        return dto;
    }

}
