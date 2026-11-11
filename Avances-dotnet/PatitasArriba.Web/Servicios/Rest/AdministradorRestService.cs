using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class AdministradorRestService : IAdministradorService {
    private string BaseUrl { get; }
    private string BaseUrlAdministrador { get; }

    public AdministradorRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlAdministrador = $"{BaseUrl}/administradores";
    }

    public async Task<List<AdministradorViewModel>> FindAll() {
        string url = BaseUrlAdministrador;
        List<AdministradorDTO> dtos = await HttpUtils.DoGet<List<AdministradorDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<AdministradorViewModel?> FindById(int id) {
        string url = $"{BaseUrlAdministrador}/{id}";
        AdministradorDTO dto;
        try {
            dto = await HttpUtils.DoGet<AdministradorDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(AdministradorViewModel modelo) {
        // Java registra la persona y su cuenta en una sola transacción y envía la invitación.
        string url = $"{BaseUrlAdministrador}/con-cuenta";
        AdministradorDTO dto = ToDto(modelo);
        AdministradorDTO creado = await HttpUtils.DoPost<AdministradorDTO, AdministradorDTO>(url, dto);
        modelo.Id = creado.Id;
        modelo.IdCuenta = creado.Cuenta?.Id ?? modelo.IdCuenta;
    }

    public async Task Update(AdministradorViewModel modelo) {
        string url = $"{BaseUrlAdministrador}/{modelo.Id}";
        AdministradorDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<AdministradorDTO, AdministradorDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlAdministrador}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<AdministradorViewModel?> FindByDni(string valor) {
        string url = QueryHelpers.AddQueryString(BaseUrlAdministrador, "dni", valor);
        List<AdministradorDTO> dtos;
        try {
            dtos = await HttpUtils.DoGet<List<AdministradorDTO>>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        if (dtos.Count == 0) {
            return null;
        }
        return ToViewModel(dtos[0]);
    }

    private List<AdministradorViewModel> ToViewModels(List<AdministradorDTO> dtos) {
        List<AdministradorViewModel> modelos = [];
        foreach (AdministradorDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private AdministradorViewModel ToViewModel(AdministradorDTO dto) {
        List<OpcionSeleccion> horarios = [];
        foreach (HorarioDTO horario in dto.Horarios) {
            horarios.Add(new OpcionSeleccion {
                Id = horario.Id,
                Texto = $"{horario.DiaSemana} {horario.HoraInicio} - {horario.HoraFin}",
                Seleccionado = true
            });
        }
        AdministradorViewModel modelo = new AdministradorViewModel {
            Id = dto.Id,
            IdCuenta = dto.Cuenta?.Id ?? 0,
            NombreUsuario = dto.Cuenta?.NombreUsuario ?? "",
            Correo = dto.Cuenta?.Correo ?? "",
            Dni = dto.Dni ?? "",
            Nombres = dto.Nombres ?? "",
            ApellidoPaterno = dto.ApellidoPaterno ?? "",
            ApellidoMaterno = dto.ApellidoMaterno ?? "",
            Telefono = dto.Telefono ?? "",
            Horarios = horarios
        };
        return modelo;
    }

    private AdministradorDTO ToDto(AdministradorViewModel viewModel) {
        List<HorarioDTO> horarios = [];
        foreach (OpcionSeleccion horario in viewModel.Horarios) {
            if (!(horario.Seleccionado)) continue;
            horarios.Add(new HorarioDTO {
                Id = horario.Id
            });
        }
        AdministradorDTO dto = new AdministradorDTO {
            Id = viewModel.Id,
            Cuenta = new CuentaDTO { Id = viewModel.IdCuenta, Correo = viewModel.Correo },
            Dni = viewModel.Dni,
            Nombres = viewModel.Nombres,
            ApellidoPaterno = viewModel.ApellidoPaterno,
            ApellidoMaterno = viewModel.ApellidoMaterno,
            Telefono = viewModel.Telefono,
            Horarios = horarios
        };
        return dto;
    }

}
