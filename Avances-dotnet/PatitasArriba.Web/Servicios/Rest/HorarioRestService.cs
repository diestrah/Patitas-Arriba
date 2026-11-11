using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class HorarioRestService : IHorarioService {
    private string BaseUrl { get; }
    private string BaseUrlHorario { get; }

    public HorarioRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlHorario = $"{BaseUrl}/horarios";
    }

    public async Task<List<HorarioViewModel>> FindAll() {
        string url = BaseUrlHorario;
        List<HorarioDTO> dtos = await HttpUtils.DoGet<List<HorarioDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<HorarioViewModel?> FindById(int id) {
        string url = $"{BaseUrlHorario}/{id}";
        HorarioDTO dto;
        try {
            dto = await HttpUtils.DoGet<HorarioDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(HorarioViewModel modelo) {
        string url = BaseUrlHorario;
        HorarioDTO dto = ToDto(modelo);
        HorarioDTO creado = await HttpUtils.DoPost<HorarioDTO, HorarioDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(HorarioViewModel modelo) {
        string url = $"{BaseUrlHorario}/{modelo.Id}";
        HorarioDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<HorarioDTO, HorarioDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlHorario}/{id}";
        await HttpUtils.DoDelete(url);
    }

    private List<HorarioViewModel> ToViewModels(List<HorarioDTO> dtos) {
        List<HorarioViewModel> modelos = [];
        foreach (HorarioDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private HorarioViewModel ToViewModel(HorarioDTO dto) {
        HorarioViewModel modelo = new HorarioViewModel {
            Id = dto.Id,
            DiaSemana = ParseDiaSemana(dto.DiaSemana),
            HoraInicio = TimeOnly.Parse(dto.HoraInicio ?? throw new FormatException("Falta horaInicio"), CultureInfo.InvariantCulture),
            HoraFin = TimeOnly.Parse(dto.HoraFin ?? throw new FormatException("Falta horaFin"), CultureInfo.InvariantCulture)
        };
        return modelo;
    }

    private HorarioDTO ToDto(HorarioViewModel viewModel) {
        HorarioDTO dto = new HorarioDTO {
            Id = viewModel.Id,
            DiaSemana = viewModel.DiaSemana.ToString(),
            HoraInicio = viewModel.HoraInicio.ToString("HH:mm:ss", CultureInfo.InvariantCulture),
            HoraFin = viewModel.HoraFin.ToString("HH:mm:ss", CultureInfo.InvariantCulture)
        };
        return dto;
    }

    private DiaSemana ParseDiaSemana(string? valor) {
        if (Enum.TryParse(valor, true, out DiaSemana resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de DiaSemana: '{valor}'.");
    }
}
