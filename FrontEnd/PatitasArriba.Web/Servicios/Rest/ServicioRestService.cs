using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class ServicioRestService : IServicioService {
    private string BaseUrl { get; }
    private string BaseUrlServicio { get; }

    public ServicioRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlServicio = $"{BaseUrl}/servicios";
    }

    public async Task<List<ServicioViewModel>> FindAll() {
        string url = BaseUrlServicio;
        List<ServicioDTO> dtos = await HttpUtils.DoGet<List<ServicioDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<ServicioViewModel?> FindById(int id) {
        string url = $"{BaseUrlServicio}/{id}";
        ServicioDTO dto;
        try {
            dto = await HttpUtils.DoGet<ServicioDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(ServicioViewModel modelo) {
        string url = BaseUrlServicio;
        ServicioDTO dto = ToDto(modelo);
        ServicioDTO creado = await HttpUtils.DoPost<ServicioDTO, ServicioDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(ServicioViewModel modelo) {
        string url = $"{BaseUrlServicio}/{modelo.Id}";
        ServicioDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<ServicioDTO, ServicioDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlServicio}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<ServicioViewModel> FindByName(string valor) {
        string url = QueryHelpers.AddQueryString(BaseUrlServicio, "nombre", valor);
        List<ServicioDTO> dtos;
        try {
            dtos = await HttpUtils.DoGet<List<ServicioDTO>>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            throw;
        }
        if (dtos.Count == 0) {
            throw new HttpRequestException("No se encontró el registro solicitado.", null, HttpStatusCode.NotFound);
        }
        return ToViewModel(dtos[0]);
    }

    private List<ServicioViewModel> ToViewModels(List<ServicioDTO> dtos) {
        List<ServicioViewModel> modelos = [];
        foreach (ServicioDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private ServicioViewModel ToViewModel(ServicioDTO dto) {
        ServicioViewModel modelo = new ServicioViewModel {
            Id = dto.Id,
            PrecioBase = dto.PrecioBase,
            Nombre = dto.Nombre ?? "",
            Descripcion = dto.Descripcion ?? "",
            TipoServicio = ParseTipoServicio(dto.Tipo),
            DuracionEstimada = dto.DuracionEstimada > 0 ? dto.DuracionEstimada : null
        };
        return modelo;
    }

    private ServicioDTO ToDto(ServicioViewModel viewModel) {
        ServicioDTO dto = new ServicioDTO {
            Id = viewModel.Id,
            PrecioBase = viewModel.PrecioBase,
            Nombre = viewModel.Nombre,
            Descripcion = viewModel.Descripcion,
            Tipo = viewModel.TipoServicio.ToString(),
            DuracionEstimada = viewModel.DuracionEstimada
        };
        return dto;
    }

    private TipoServicio ParseTipoServicio(string? valor) {
        if (Enum.TryParse(valor, true, out TipoServicio resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de TipoServicio: '{valor}'.");
    }
}
