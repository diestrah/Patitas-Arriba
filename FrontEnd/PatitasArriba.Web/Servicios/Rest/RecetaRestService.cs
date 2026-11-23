using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class RecetaRestService : IRecetaService {
    private string BaseUrl { get; }
    private string BaseUrlReceta { get; }

    public RecetaRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlReceta = $"{BaseUrl}/recetas";
    }

    public async Task<List<RecetaViewModel>> FindAll() {
        string url = BaseUrlReceta;
        List<RecetaDTO> dtos = await HttpUtils.DoGet<List<RecetaDTO>>(url);
        List<CitaDTO> citas = await HttpUtils.DoGet<List<CitaDTO>>($"{BaseUrl}/citas");
        return ToViewModels(dtos, citas);
    }

    public async Task<RecetaViewModel?> FindById(int id) {
        string url = $"{BaseUrlReceta}/{id}";
        RecetaDTO dto;
        try {
            dto = await HttpUtils.DoGet<RecetaDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        List<CitaDTO> citas = await HttpUtils.DoGet<List<CitaDTO>>($"{BaseUrl}/citas");
        return ToViewModel(dto, citas);
    }

    public async Task Insert(RecetaViewModel modelo) {
        string url = BaseUrlReceta;
        RecetaDTO dto = ToDto(modelo);
        RecetaDTO creado = await HttpUtils.DoPost<RecetaDTO, RecetaDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(RecetaViewModel modelo) {
        string url = $"{BaseUrlReceta}/{modelo.Id}";
        RecetaDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<RecetaDTO, RecetaDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlReceta}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<List<RecetaViewModel>> FindByAtencion(int id) {
        string url = QueryHelpers.AddQueryString(BaseUrlReceta, "atencion", id.ToString(CultureInfo.InvariantCulture));
        List<RecetaDTO> dtos = await HttpUtils.DoGet<List<RecetaDTO>>(url);
        List<CitaDTO> citas = await HttpUtils.DoGet<List<CitaDTO>>($"{BaseUrl}/citas");
        return ToViewModels(dtos, citas);
    }

    private List<RecetaViewModel> ToViewModels(List<RecetaDTO> dtos, List<CitaDTO> citas) {
        List<RecetaViewModel> modelos = [];
        foreach (RecetaDTO dto in dtos) {
            modelos.Add(ToViewModel(dto, citas));
        }
        return modelos;
    }

    private RecetaViewModel ToViewModel(RecetaDTO dto, List<CitaDTO> citas) {
        List<DetalleRecetaViewModel> medicamentos = [];
        foreach (DetalleRecetaDTO detalle in dto.Detalles) {
            medicamentos.Add(new DetalleRecetaViewModel {
                IdArticulo = detalle.Articulo?.Id ?? 0,
                NombreArticulo = detalle.Articulo?.Nombre ?? "",
                Dosis = detalle.Dosis ?? "",
                Frecuencia = detalle.Frecuencia ?? "",
                DuracionDias = detalle.DuracionDias,
                CantidadTotal = detalle.CantidadTotal
            });
        }
        List<string> nombresDiagnosticos = [];
        if (dto.Atencion != null) {
            foreach (AtencionDiagnosticoDTO diagnostico in dto.Atencion.Diagnosticos) {
                nombresDiagnosticos.Add(diagnostico.Diagnostico?.NombreEnfermedad ?? "");
            }
        }
        RecetaViewModel modelo = new RecetaViewModel {
            Id = dto.Id,
            IdAtencion = dto.Atencion?.Id ?? 0,
            IdVeterinario = dto.Atencion?.DetalleCita?.Veterinario?.Id ?? 0,
            NombreVeterinario = Nombre(dto.Atencion?.DetalleCita?.Veterinario),
            NumeroColegiatura = dto.Atencion?.DetalleCita?.Veterinario?.NumeroColegiatura ?? "",
            Diagnostico = string.Join(", ", nombresDiagnosticos),
            FechaEmision = DateValue(dto.FechaEmision),
            IndicacionesGenerales = dto.IndicacionesGenerales,
            Medicamentos = medicamentos
        };
        // El DTO no incluye la mascota: se obtiene de la cita del detalle atendido.
        foreach (CitaDTO cita in citas) {
            foreach (DetalleCitaDTO detalle in cita.Detalles) {
                if (detalle.Id != dto.Atencion?.DetalleCita?.Id) continue;
                modelo.NombreMascota = cita.Mascota?.Nombre ?? "";
                modelo.NombreCliente = Nombre(cita.Mascota?.Cliente);
                if (cita.Mascota != null) {
                    modelo.TipoMascota = ParseTipoMascota(cita.Mascota.TipoMascota);
                }
                return modelo;
            }
        }
        return modelo;
    }

    private RecetaDTO ToDto(RecetaViewModel viewModel) {
        List<DetalleRecetaDTO> detalles = [];
        foreach (DetalleRecetaViewModel medicamento in viewModel.Medicamentos) {
            detalles.Add(new DetalleRecetaDTO {
                Articulo = new ArticuloDTO { Id = medicamento.IdArticulo },
                Dosis = medicamento.Dosis,
                Frecuencia = medicamento.Frecuencia,
                DuracionDias = medicamento.DuracionDias,
                CantidadTotal = medicamento.CantidadTotal
            });
        }
        RecetaDTO dto = new RecetaDTO {
            Id = viewModel.Id,
            Atencion = new AtencionDTO { Id = viewModel.IdAtencion },
            FechaEmision = DateText(viewModel.FechaEmision),
            IndicacionesGenerales = viewModel.IndicacionesGenerales,
            Detalles = detalles
        };
        return dto;
    }

    private TipoMascota ParseTipoMascota(string? valor) {
        if (Enum.TryParse(valor, true, out TipoMascota resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de TipoMascota: '{valor}'.");
    }

    private DateOnly? DateValue(string? valor) {
        if (string.IsNullOrWhiteSpace(valor)) return null;
        return DateOnly.Parse(valor, CultureInfo.InvariantCulture);
    }

    private string? DateText(DateOnly? valor) {
        return valor?.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
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

}
