using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class VeterinarioRestService : IVeterinarioService {
    private string BaseUrl { get; }
    private string BaseUrlVeterinario { get; }

    public VeterinarioRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlVeterinario = $"{BaseUrl}/veterinarios";
    }

    public async Task<List<VeterinarioViewModel>> FindAll() {
        string url = BaseUrlVeterinario;
        List<VeterinarioDTO> dtos = await HttpUtils.DoGet<List<VeterinarioDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<VeterinarioViewModel?> FindById(int id) {
        string url = $"{BaseUrlVeterinario}/{id}";
        VeterinarioDTO dto;
        try {
            dto = await HttpUtils.DoGet<VeterinarioDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(VeterinarioViewModel modelo) {
        // Java registra la persona y su cuenta en una sola transacción y envía la invitación.
        string url = $"{BaseUrlVeterinario}/con-cuenta";
        VeterinarioDTO dto = ToDto(modelo);
        VeterinarioDTO creado = await HttpUtils.DoPost<VeterinarioDTO, VeterinarioDTO>(url, dto);
        modelo.Id = creado.Id;
        modelo.IdCuenta = creado.Cuenta?.Id ?? modelo.IdCuenta;
    }

    public async Task Update(VeterinarioViewModel modelo) {
        string url = $"{BaseUrlVeterinario}/{modelo.Id}";
        VeterinarioDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<VeterinarioDTO, VeterinarioDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlVeterinario}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<VeterinarioViewModel?> FindByDni(string valor) {
        string url = QueryHelpers.AddQueryString(BaseUrlVeterinario, "dni", valor);
        List<VeterinarioDTO> dtos;
        try {
            dtos = await HttpUtils.DoGet<List<VeterinarioDTO>>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        if (dtos.Count == 0) {
            return null;
        }
        return ToViewModel(dtos[0]);
    }

    private List<VeterinarioViewModel> ToViewModels(List<VeterinarioDTO> dtos) {
        List<VeterinarioViewModel> modelos = [];
        foreach (VeterinarioDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private VeterinarioViewModel ToViewModel(VeterinarioDTO dto) {
        List<OpcionSeleccion> horarios = [];
        foreach (HorarioDTO horario in dto.Horarios) {
            horarios.Add(new OpcionSeleccion {
                Id = horario.Id,
                Texto = $"{horario.DiaSemana} {horario.HoraInicio} - {horario.HoraFin}",
                Seleccionado = true
            });
        }
        VeterinarioViewModel modelo = new VeterinarioViewModel {
            Id = dto.Id,
            IdCuenta = dto.Cuenta?.Id ?? 0,
            NombreUsuario = dto.Cuenta?.NombreUsuario ?? "",
            Correo = dto.Cuenta?.Correo ?? "",
            Dni = dto.Dni ?? "",
            Nombres = dto.Nombres ?? "",
            ApellidoPaterno = dto.ApellidoPaterno ?? "",
            ApellidoMaterno = dto.ApellidoMaterno ?? "",
            Telefono = dto.Telefono ?? "",
            Horarios = horarios,
            NumeroColegiatura = dto.NumeroColegiatura ?? ""
        };
        return modelo;
    }

    private VeterinarioDTO ToDto(VeterinarioViewModel viewModel) {
        List<HorarioDTO> horarios = [];
        foreach (OpcionSeleccion horario in viewModel.Horarios) {
            if (!(horario.Seleccionado)) continue;
            horarios.Add(new HorarioDTO {
                Id = horario.Id
            });
        }
        VeterinarioDTO dto = new VeterinarioDTO {
            Id = viewModel.Id,
            Cuenta = new CuentaDTO { Id = viewModel.IdCuenta, Correo = viewModel.Correo },
            Dni = viewModel.Dni,
            Nombres = viewModel.Nombres,
            ApellidoPaterno = viewModel.ApellidoPaterno,
            ApellidoMaterno = viewModel.ApellidoMaterno,
            Telefono = viewModel.Telefono,
            Horarios = horarios,
            NumeroColegiatura = viewModel.NumeroColegiatura
        };
        return dto;
    }

}
