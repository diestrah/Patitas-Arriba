using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class DiagnosticoRestService : IDiagnosticoService {
    private string BaseUrl { get; }
    private string BaseUrlDiagnostico { get; }

    public DiagnosticoRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlDiagnostico = $"{BaseUrl}/diagnosticos";
    }

    public async Task<List<DiagnosticoViewModel>> FindAll() {
        string url = BaseUrlDiagnostico;
        List<DiagnosticoDTO> dtos = await HttpUtils.DoGet<List<DiagnosticoDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<DiagnosticoViewModel?> FindById(int id) {
        string url = $"{BaseUrlDiagnostico}/{id}";
        DiagnosticoDTO dto;
        try {
            dto = await HttpUtils.DoGet<DiagnosticoDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(DiagnosticoViewModel modelo) {
        string url = BaseUrlDiagnostico;
        DiagnosticoDTO dto = ToDto(modelo);
        DiagnosticoDTO creado = await HttpUtils.DoPost<DiagnosticoDTO, DiagnosticoDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(DiagnosticoViewModel modelo) {
        string url = $"{BaseUrlDiagnostico}/{modelo.Id}";
        DiagnosticoDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<DiagnosticoDTO, DiagnosticoDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlDiagnostico}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<DiagnosticoViewModel> FindByNombreEnfermedad(string valor) {
        string url = QueryHelpers.AddQueryString(BaseUrlDiagnostico, "nombreEnfermedad", valor);
        List<DiagnosticoDTO> dtos;
        try {
            dtos = await HttpUtils.DoGet<List<DiagnosticoDTO>>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            throw;
        }
        if (dtos.Count == 0) {
            throw new HttpRequestException("No se encontró el registro solicitado.", null, HttpStatusCode.NotFound);
        }
        return ToViewModel(dtos[0]);
    }

    private List<DiagnosticoViewModel> ToViewModels(List<DiagnosticoDTO> dtos) {
        List<DiagnosticoViewModel> modelos = [];
        foreach (DiagnosticoDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private DiagnosticoViewModel ToViewModel(DiagnosticoDTO dto) {
        DiagnosticoViewModel modelo = new DiagnosticoViewModel {
            Id = dto.Id,
            NombreEnfermedad = dto.NombreEnfermedad ?? "",
            Descripcion = dto.Descripcion ?? ""
        };
        return modelo;
    }

    private DiagnosticoDTO ToDto(DiagnosticoViewModel viewModel) {
        DiagnosticoDTO dto = new DiagnosticoDTO {
            Id = viewModel.Id,
            NombreEnfermedad = viewModel.NombreEnfermedad,
            Descripcion = viewModel.Descripcion
        };
        return dto;
    }

}
