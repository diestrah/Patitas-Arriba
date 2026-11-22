using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class CategoriaRestService : ICategoriaService {
    private string BaseUrl { get; }
    private string BaseUrlCategoria { get; }

    public CategoriaRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlCategoria = $"{BaseUrl}/categorias";
    }

    public async Task<List<CategoriaViewModel>> FindAll() {
        string url = BaseUrlCategoria;
        List<CategoriaDTO> dtos = await HttpUtils.DoGet<List<CategoriaDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<CategoriaViewModel?> FindById(int id) {
        string url = $"{BaseUrlCategoria}/{id}";
        CategoriaDTO dto;
        try {
            dto = await HttpUtils.DoGet<CategoriaDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(CategoriaViewModel modelo) {
        string url = BaseUrlCategoria;
        CategoriaDTO dto = ToDto(modelo);
        CategoriaDTO creado = await HttpUtils.DoPost<CategoriaDTO, CategoriaDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(CategoriaViewModel modelo) {
        string url = $"{BaseUrlCategoria}/{modelo.Id}";
        CategoriaDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<CategoriaDTO, CategoriaDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlCategoria}/{id}";
        await HttpUtils.DoDelete(url);
    }

    private List<CategoriaViewModel> ToViewModels(List<CategoriaDTO> dtos) {
        List<CategoriaViewModel> modelos = [];
        foreach (CategoriaDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private CategoriaViewModel ToViewModel(CategoriaDTO dto) {
        CategoriaViewModel modelo = new CategoriaViewModel {
            Id = dto.Id,
            Nombre = dto.Nombre ?? "",
            Descripcion = dto.Descripcion ?? ""
        };
        return modelo;
    }

    private CategoriaDTO ToDto(CategoriaViewModel viewModel) {
        CategoriaDTO dto = new CategoriaDTO {
            Id = viewModel.Id,
            Nombre = viewModel.Nombre,
            Descripcion = viewModel.Descripcion
        };
        return dto;
    }

}
