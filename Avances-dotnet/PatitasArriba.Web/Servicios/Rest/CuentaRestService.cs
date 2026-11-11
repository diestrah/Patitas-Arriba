using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class CuentaRestService : ICuentaService {
    private string BaseUrl { get; }
    private string BaseUrlCuenta { get; }

    public CuentaRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlCuenta = $"{BaseUrl}/cuentas";
    }

    public async Task<List<CuentaUsuarioViewModel>> FindAll() {
        string url = BaseUrlCuenta;
        List<CuentaDTO> dtos = await HttpUtils.DoGet<List<CuentaDTO>>(url);
        List<ClienteDTO> clientes = await HttpUtils.DoGet<List<ClienteDTO>>($"{BaseUrl}/clientes");
        List<VeterinarioDTO> veterinarios = await HttpUtils.DoGet<List<VeterinarioDTO>>($"{BaseUrl}/veterinarios");
        List<AdministradorDTO> administradores = await HttpUtils.DoGet<List<AdministradorDTO>>($"{BaseUrl}/administradores");
        return ToViewModels(dtos, clientes, veterinarios, administradores);
    }

    public async Task<CuentaUsuarioViewModel?> FindById(int id) {
        string url = $"{BaseUrlCuenta}/{id}";
        CuentaDTO dto;
        try {
            dto = await HttpUtils.DoGet<CuentaDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        List<ClienteDTO> clientes = await HttpUtils.DoGet<List<ClienteDTO>>($"{BaseUrl}/clientes");
        List<VeterinarioDTO> veterinarios = await HttpUtils.DoGet<List<VeterinarioDTO>>($"{BaseUrl}/veterinarios");
        List<AdministradorDTO> administradores = await HttpUtils.DoGet<List<AdministradorDTO>>($"{BaseUrl}/administradores");
        return ToViewModel(dto, clientes, veterinarios, administradores);
    }

    public async Task Insert(CuentaUsuarioViewModel modelo) {
        string url = BaseUrlCuenta;
        CuentaDTO dto = ToDto(modelo);
        CuentaDTO creado = await HttpUtils.DoPost<CuentaDTO, CuentaDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(CuentaUsuarioViewModel modelo) {
        string url = $"{BaseUrlCuenta}/{modelo.Id}";
        CuentaDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<CuentaDTO, CuentaDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlCuenta}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<CuentaUsuarioViewModel> FindByNombreUsuario(string valor) {
        string url = QueryHelpers.AddQueryString(BaseUrlCuenta, "nombreUsuario", valor);
        List<CuentaDTO> dtos;
        try {
            dtos = await HttpUtils.DoGet<List<CuentaDTO>>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            throw;
        }
        if (dtos.Count == 0) {
            throw new HttpRequestException("No se encontró el registro solicitado.", null, HttpStatusCode.NotFound);
        }
        List<ClienteDTO> clientes = await HttpUtils.DoGet<List<ClienteDTO>>($"{BaseUrl}/clientes");
        List<VeterinarioDTO> veterinarios = await HttpUtils.DoGet<List<VeterinarioDTO>>($"{BaseUrl}/veterinarios");
        List<AdministradorDTO> administradores = await HttpUtils.DoGet<List<AdministradorDTO>>($"{BaseUrl}/administradores");
        return ToViewModel(dtos[0], clientes, veterinarios, administradores);
    }

    public async Task<SesionViewModel> Login(string correo, string password) {
        string url = $"{BaseUrl}/login";
        SesionDTO dto = await HttpUtils.DoPost<object, SesionDTO>(url, new { correo, password });
        return new SesionViewModel {
            Id = dto.Id,
            IdPersona = dto.IdPersona,
            Correo = dto.Correo ?? "",
            NombreUsuario = dto.NombreUsuario ?? "",
            Rol = ParseRolUsuario(dto.Rol),
            Nombres = dto.Nombres ?? "",
            ApellidoPaterno = dto.ApellidoPaterno
        };
    }

    private List<CuentaUsuarioViewModel> ToViewModels(List<CuentaDTO> dtos, List<ClienteDTO> clientes, List<VeterinarioDTO> veterinarios, List<AdministradorDTO> administradores) {
        List<CuentaUsuarioViewModel> modelos = [];
        foreach (CuentaDTO dto in dtos) {
            modelos.Add(ToViewModel(dto, clientes, veterinarios, administradores));
        }
        return modelos;
    }

    private CuentaUsuarioViewModel ToViewModel(CuentaDTO dto, List<ClienteDTO> clientes, List<VeterinarioDTO> veterinarios, List<AdministradorDTO> administradores) {
        CuentaUsuarioViewModel modelo = new CuentaUsuarioViewModel {
            Id = dto.Id,
            NombreUsuario = dto.NombreUsuario ?? "",
            Correo = dto.Correo ?? ""
        };
        foreach (ClienteDTO persona in clientes) {
            if (persona.Cuenta?.Id == dto.Id) {
                modelo.Rol = RolUsuario.CLIENTE;
                modelo.NombrePersona = Nombre(persona);
                return modelo;
            }
        }
        foreach (VeterinarioDTO persona in veterinarios) {
            if (persona.Cuenta?.Id == dto.Id) {
                modelo.Rol = RolUsuario.VETERINARIO;
                modelo.NombrePersona = Nombre(persona);
                return modelo;
            }
        }
        foreach (AdministradorDTO persona in administradores) {
            if (persona.Cuenta?.Id == dto.Id) {
                modelo.Rol = RolUsuario.ADMINISTRADOR;
                modelo.NombrePersona = Nombre(persona);
                return modelo;
            }
        }
        return modelo;
    }

    private CuentaDTO ToDto(CuentaUsuarioViewModel viewModel) {
        CuentaDTO dto = new CuentaDTO {
            Id = viewModel.Id,
            NombreUsuario = viewModel.NombreUsuario,
            Correo = viewModel.Correo,
            Password = viewModel.Password
        };
        return dto;
    }

    private RolUsuario ParseRolUsuario(string? valor) {
        if (Enum.TryParse(valor, true, out RolUsuario resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de RolUsuario: '{valor}'.");
    }

    private string Nombre(string? nombres, string? paterno, string? materno) {
        List<string> partes = [];
        foreach (string? texto in new[] { nombres, paterno, materno }) {
            if (!string.IsNullOrWhiteSpace(texto)) partes.Add(texto);
        }
        return string.Join(" ", partes);
    }

    private string Nombre(ClienteDTO? persona) {
        if (persona == null) return "";
        return Nombre(persona.Nombres, persona.ApellidoPaterno, persona.ApellidoMaterno);
    }

    private string Nombre(VeterinarioDTO? persona) {
        if (persona == null) return "";
        return Nombre(persona.Nombres, persona.ApellidoPaterno, persona.ApellidoMaterno);
    }

    private string Nombre(AdministradorDTO? persona) {
        if (persona == null) return "";
        return Nombre(persona.Nombres, persona.ApellidoPaterno, persona.ApellidoMaterno);
    }
}
