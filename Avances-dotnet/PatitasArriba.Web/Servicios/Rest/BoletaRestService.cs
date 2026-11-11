using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class BoletaRestService : IBoletaService {
    private string BaseUrl { get; }
    private string BaseUrlBoleta { get; }

    public BoletaRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlBoleta = $"{BaseUrl}/boletas";
    }

    public async Task<List<BoletaViewModel>> FindAll() {
        string url = BaseUrlBoleta;
        List<BoletaDTO> dtos = await HttpUtils.DoGet<List<BoletaDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<BoletaViewModel?> FindById(int id) {
        string url = $"{BaseUrlBoleta}/{id}";
        BoletaDTO dto;
        try {
            dto = await HttpUtils.DoGet<BoletaDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(BoletaViewModel modelo) {
        string url = BaseUrlBoleta;
        BoletaDTO dto = ToDto(modelo);
        // Java genera el número de boleta.
        dto.SerieCorrelativo = null;
        BoletaDTO creado = await HttpUtils.DoPost<BoletaDTO, BoletaDTO>(url, dto);
        modelo.Id = creado.Id;
        if (!string.IsNullOrEmpty(creado.SerieCorrelativo)) {
            modelo.SerieCorrelativo = creado.SerieCorrelativo;
        }
    }

    public async Task Update(BoletaViewModel modelo) {
        string url = $"{BaseUrlBoleta}/{modelo.Id}";
        BoletaDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<BoletaDTO, BoletaDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlBoleta}/{id}";
        await HttpUtils.DoDelete(url);
    }

    private List<BoletaViewModel> ToViewModels(List<BoletaDTO> dtos) {
        List<BoletaViewModel> modelos = [];
        foreach (BoletaDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private BoletaViewModel ToViewModel(BoletaDTO dto) {
        List<LineaBoletaViewModel> lineas = [];
        foreach (DetalleBoletaServicioDTO detalle in dto.DetallesServicio) {
            lineas.Add(new LineaBoletaViewModel {
                Tipo = "Servicio", IdDetalleCita = detalle.DetalleCita?.Id,
                Descripcion = detalle.DetalleCita?.Servicio?.Nombre ?? "",
                Cantidad = detalle.Cantidad, PrecioUnitario = detalle.PrecioUnitario
            });
        }
        foreach (DetalleBoletaArticuloDTO detalle in dto.DetallesArticulo) {
            lineas.Add(new LineaBoletaViewModel {
                Tipo = detalle.InsumoUtilizado == null ? "Artículo" : "Insumo",
                IdArticulo = detalle.Articulo?.Id, IdInsumoUtilizado = detalle.InsumoUtilizado?.Id,
                Descripcion = detalle.Articulo?.Nombre ?? "",
                Cantidad = detalle.Cantidad, PrecioUnitario = detalle.PrecioUnitario
            });
        }
        BoletaViewModel modelo = new BoletaViewModel {
            Id = dto.Id,
            IsActivo = dto.Activo,
            SerieCorrelativo = dto.SerieCorrelativo ?? "",
            Fecha = DateTimeValue(dto.Fecha) ?? throw new FormatException("Falta fecha de boleta"),
            IdCliente = dto.Cliente?.Id ?? 0,
            NombreCliente = Nombre(dto.Cliente),
            DniCliente = dto.Cliente?.Dni ?? "",
            MetodoPago = ParseMetodoPago(dto.MetodoPago),
            Lineas = lineas
        };
        return modelo;
    }

    private BoletaDTO ToDto(BoletaViewModel viewModel) {
        List<DetalleBoletaServicioDTO> detallesServicio = [];
        foreach (LineaBoletaViewModel linea in viewModel.Lineas) {
            if (linea.Tipo != "Servicio") continue;
            detallesServicio.Add(new DetalleBoletaServicioDTO {
                DetalleCita = new DetalleCitaDTO { Id = linea.IdDetalleCita ?? 0 },
                Cantidad = linea.Cantidad
            });
        }
        List<DetalleBoletaArticuloDTO> detallesArticulo = [];
        foreach (LineaBoletaViewModel linea in viewModel.Lineas) {
            if (linea.Tipo == "Servicio") continue;
            detallesArticulo.Add(new DetalleBoletaArticuloDTO {
                Articulo = new ArticuloDTO { Id = linea.IdArticulo ?? 0 },
                InsumoUtilizado = linea.IdInsumoUtilizado.HasValue ? new InsumoUtilizadoRefDTO { Id = linea.IdInsumoUtilizado.Value } : null,
                Cantidad = linea.Cantidad
            });
        }
        BoletaDTO dto = new BoletaDTO {
            Id = viewModel.Id,
            Activo = viewModel.IsActivo,
            SerieCorrelativo = viewModel.SerieCorrelativo,
            Fecha = DateText(viewModel.Fecha),
            Cliente = new ClienteDTO { Id = viewModel.IdCliente },
            MetodoPago = viewModel.MetodoPago.ToString(),
            DetallesServicio = detallesServicio,
            DetallesArticulo = detallesArticulo
        };
        return dto;
    }

    private MetodoPago ParseMetodoPago(string? valor) {
        if (Enum.TryParse(valor, true, out MetodoPago resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de MetodoPago: '{valor}'.");
    }

    private DateTime? DateTimeValue(string? valor) {
        if (string.IsNullOrWhiteSpace(valor)) return null;
        return DateTime.Parse(valor, CultureInfo.InvariantCulture, DateTimeStyles.RoundtripKind);
    }

    private string? DateText(DateTime? valor) {
        return valor?.ToString("yyyy-MM-dd'T'HH:mm:ss", CultureInfo.InvariantCulture);
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


}
