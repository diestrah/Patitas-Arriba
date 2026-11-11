using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class AtencionRestService : IAtencionService {
    private string BaseUrl { get; }
    private string BaseUrlAtencion { get; }

    public AtencionRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlAtencion = $"{BaseUrl}/atenciones";
    }

    public async Task<List<AtencionViewModel>> FindAll() {
        string url = BaseUrlAtencion;
        List<AtencionDTO> dtos = await HttpUtils.DoGet<List<AtencionDTO>>(url);
        List<CitaDTO> citas = await HttpUtils.DoGet<List<CitaDTO>>($"{BaseUrl}/citas");
        return ToViewModels(dtos, citas);
    }

    public async Task<AtencionViewModel?> FindById(int id) {
        string url = $"{BaseUrlAtencion}/{id}";
        AtencionDTO dto;
        try {
            dto = await HttpUtils.DoGet<AtencionDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        List<CitaDTO> citas = await HttpUtils.DoGet<List<CitaDTO>>($"{BaseUrl}/citas");
        return ToViewModel(dto, citas);
    }

    public async Task Insert(AtencionViewModel modelo) {
        string url = BaseUrlAtencion;
        AtencionDTO dto = ToDto(modelo);
        AtencionDTO creado = await HttpUtils.DoPost<AtencionDTO, AtencionDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(AtencionViewModel modelo) {
        string url = $"{BaseUrlAtencion}/{modelo.Id}";
        AtencionDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<AtencionDTO, AtencionDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlAtencion}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<List<AtencionViewModel>> FindByMascota(int id) {
        string url = QueryHelpers.AddQueryString(BaseUrlAtencion, "mascota", id.ToString(CultureInfo.InvariantCulture));
        List<AtencionDTO> dtos = await HttpUtils.DoGet<List<AtencionDTO>>(url);
        List<CitaDTO> citas = await HttpUtils.DoGet<List<CitaDTO>>($"{BaseUrl}/citas");
        return ToViewModels(dtos, citas);
    }

    // Solo GET /atenciones, sin completar los nombres desde /citas como hace FindAll
    public async Task<Dictionary<int, int>> AtencionPorDetalleCita() {
        Dictionary<int, int> resultado = [];
        foreach (AtencionDTO dto in await HttpUtils.DoGet<List<AtencionDTO>>(BaseUrlAtencion)) {
            if (dto.DetalleCita != null) resultado[dto.DetalleCita.Id] = dto.Id;
        }
        return resultado;
    }

    private List<AtencionViewModel> ToViewModels(List<AtencionDTO> dtos, List<CitaDTO> citas) {
        List<AtencionViewModel> modelos = [];
        foreach (AtencionDTO dto in dtos) {
            modelos.Add(ToViewModel(dto, citas));
        }
        return modelos;
    }

    private AtencionViewModel ToViewModel(AtencionDTO dto, List<CitaDTO> citas) {
        List<AtencionDiagnosticoViewModel> diagnosticos = [];
        foreach (AtencionDiagnosticoDTO diagnostico in dto.Diagnosticos) {
            diagnosticos.Add(new AtencionDiagnosticoViewModel {
                IdDiagnostico = diagnostico.Diagnostico?.Id ?? 0,
                NombreEnfermedad = diagnostico.Diagnostico?.NombreEnfermedad ?? "",
                NivelGravedad = ParseNivelGravedad(diagnostico.NivelGravedad),
                DetalleDiagnostico = diagnostico.DetalleDiagnostico
            });
        }
        List<InsumoUtilizadoViewModel> insumos = [];
        foreach (InsumoUtilizadoDTO insumo in dto.InsumosUtilizados) {
            insumos.Add(new InsumoUtilizadoViewModel {
                Id = insumo.Id,
                IdArticulo = insumo.Articulo?.Id ?? 0,
                NombreArticulo = insumo.Articulo?.Nombre ?? "",
                PrecioUnitario = insumo.Articulo?.PrecioBase ?? 0,
                CantidadUtilizada = insumo.CantidadUtilizada,
                EsCobrable = insumo.EsCobrable
            });
        }
        AtencionViewModel modelo = new AtencionViewModel {
            Id = dto.Id,
            IdDetalleCita = dto.DetalleCita?.Id ?? 0,
            NombreServicio = dto.DetalleCita?.Servicio?.Nombre ?? "",
            TipoServicio = ParseTipoServicio(dto.DetalleCita?.Servicio?.Tipo),
            IdVeterinario = dto.DetalleCita?.Veterinario?.Id ?? 0,
            NombreVeterinario = Nombre(dto.DetalleCita?.Veterinario),
            FechaHora = DateTimeValue(dto.FechaHora),
            MotivoConsulta = dto.MotivoConsulta,
            Observaciones = dto.Observaciones,
            Diagnosticos = diagnosticos,
            Insumos = insumos
        };
        // El DTO no incluye la mascota: se obtiene de la cita del detalle atendido.
        foreach (CitaDTO cita in citas) {
            foreach (DetalleCitaDTO detalle in cita.Detalles) {
                if (detalle.Id != modelo.IdDetalleCita) continue;
                modelo.IdMascota = cita.Mascota?.Id ?? 0;
                modelo.NombreMascota = cita.Mascota?.Nombre ?? "";
                modelo.NombreCliente = Nombre(cita.Mascota?.Cliente);
                return modelo;
            }
        }
        return modelo;
    }

    private AtencionDTO ToDto(AtencionViewModel viewModel) {
        List<AtencionDiagnosticoDTO> diagnosticos = [];
        foreach (AtencionDiagnosticoViewModel diagnostico in viewModel.Diagnosticos) {
            diagnosticos.Add(new AtencionDiagnosticoDTO {
                Diagnostico = new DiagnosticoDTO { Id = diagnostico.IdDiagnostico },
                NivelGravedad = diagnostico.NivelGravedad.ToString(),
                DetalleDiagnostico = diagnostico.DetalleDiagnostico
            });
        }
        List<InsumoUtilizadoDTO> insumosUtilizados = [];
        foreach (InsumoUtilizadoViewModel insumo in viewModel.Insumos) {
            insumosUtilizados.Add(new InsumoUtilizadoDTO {
                Id = insumo.Id,
                Articulo = new ArticuloDTO { Id = insumo.IdArticulo },
                CantidadUtilizada = insumo.CantidadUtilizada,
                EsCobrable = insumo.EsCobrable
            });
        }
        AtencionDTO dto = new AtencionDTO {
            Id = viewModel.Id,
            DetalleCita = new DetalleCitaDTO { Id = viewModel.IdDetalleCita },
            FechaHora = DateText(viewModel.FechaHora),
            MotivoConsulta = viewModel.MotivoConsulta,
            Observaciones = viewModel.Observaciones,
            Diagnosticos = diagnosticos,
            InsumosUtilizados = insumosUtilizados
        };
        return dto;
    }

    private NivelGravedad ParseNivelGravedad(string? valor) {
        if (Enum.TryParse(valor, true, out NivelGravedad resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de NivelGravedad: '{valor}'.");
    }

    private TipoServicio ParseTipoServicio(string? valor) {
        if (Enum.TryParse(valor, true, out TipoServicio resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de TipoServicio: '{valor}'.");
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

    private string Nombre(VeterinarioDTO? persona) {
        if (persona == null) return "";
        return Nombre(persona.Nombres, persona.ApellidoPaterno, persona.ApellidoMaterno);
    }

}
