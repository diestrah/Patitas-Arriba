using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class ArticuloRestService : IArticuloService {
    private string BaseUrl { get; }
    private string BaseUrlArticulo { get; }

    public ArticuloRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlArticulo = $"{BaseUrl}/articulos";
    }

    public async Task<List<ArticuloViewModel>> FindAll() {
        string url = BaseUrlArticulo;
        List<ArticuloDTO> dtos = await HttpUtils.DoGet<List<ArticuloDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<ArticuloViewModel?> FindById(int id) {
        string url = $"{BaseUrlArticulo}/{id}";
        ArticuloDTO dto;
        try {
            dto = await HttpUtils.DoGet<ArticuloDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(ArticuloViewModel modelo) {
        string url = BaseUrlArticulo;
        ArticuloDTO dto = ToDto(modelo);
        ArticuloDTO creado = await HttpUtils.DoPost<ArticuloDTO, ArticuloDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(ArticuloViewModel modelo) {
        string url = $"{BaseUrlArticulo}/{modelo.Id}";
        ArticuloDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<ArticuloDTO, ArticuloDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlArticulo}/{id}";
        await HttpUtils.DoDelete(url);
    }

    private List<ArticuloViewModel> ToViewModels(List<ArticuloDTO> dtos) {
        List<ArticuloViewModel> modelos = [];
        foreach (ArticuloDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private ArticuloViewModel ToViewModel(ArticuloDTO dto) {
        List<OpcionSeleccion> categorias = [];
        foreach (CategoriaDTO categoria in dto.Categorias) {
            categorias.Add(new OpcionSeleccion {
                Id = categoria.Id,
                Texto = categoria.Nombre ?? "",
                Seleccionado = true
            });
        }
        ArticuloViewModel modelo = new ArticuloViewModel {
            Id = dto.Id,
            PrecioBase = dto.PrecioBase,
            StockActual = dto.StockActual,
            StockMinimo = dto.StockMinimo,
            Nombre = dto.Nombre ?? "",
            Descripcion = dto.Descripcion ?? "",
            Categorias = categorias
        };
        return modelo;
    }

    private ArticuloDTO ToDto(ArticuloViewModel viewModel) {
        List<CategoriaDTO> categorias = [];
        foreach (OpcionSeleccion categoria in viewModel.Categorias) {
            if (!(categoria.Seleccionado)) continue;
            categorias.Add(new CategoriaDTO {
                Id = categoria.Id
            });
        }
        ArticuloDTO dto = new ArticuloDTO {
            Id = viewModel.Id,
            PrecioBase = viewModel.PrecioBase,
            StockActual = viewModel.StockActual,
            StockMinimo = viewModel.StockMinimo,
            Nombre = viewModel.Nombre,
            Descripcion = viewModel.Descripcion,
            Categorias = categorias
        };
        return dto;
    }

}
