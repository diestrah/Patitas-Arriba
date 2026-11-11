using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class CitaRestService : ICitaService {
    private string BaseUrl { get; }
    private string BaseUrlCita { get; }

    public CitaRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlCita = $"{BaseUrl}/citas";
    }

    public async Task<List<CitaViewModel>> FindAll() {
        string url = BaseUrlCita;
        List<CitaDTO> dtos = await HttpUtils.DoGet<List<CitaDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<CitaViewModel?> FindById(int id) {
        string url = $"{BaseUrlCita}/{id}";
        CitaDTO dto;
        try {
            dto = await HttpUtils.DoGet<CitaDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(CitaViewModel modelo) {
        string url = BaseUrlCita;
        CitaDTO dto = ToDto(modelo);
        CitaDTO creado = await HttpUtils.DoPost<CitaDTO, CitaDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(CitaViewModel modelo) {
        string url = $"{BaseUrlCita}/{modelo.Id}";
        CitaDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<CitaDTO, CitaDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlCita}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<List<CitaViewModel>> FindByCliente(int id) {
        string url = QueryHelpers.AddQueryString(BaseUrlCita, "cliente", id.ToString(CultureInfo.InvariantCulture));
        List<CitaDTO> dtos = await HttpUtils.DoGet<List<CitaDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<List<CitaViewModel>> FindByFecha(DateOnly fecha, int? idVeterinario = null) {
        string url = QueryHelpers.AddQueryString(BaseUrlCita, "fecha", fecha.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture));
        if (idVeterinario.HasValue) {
            url = QueryHelpers.AddQueryString(url, "veterinario", idVeterinario.Value.ToString(CultureInfo.InvariantCulture));
        }
        List<CitaDTO> dtos = await HttpUtils.DoGet<List<CitaDTO>>(url);
        return ToViewModels(dtos);
    }

    private List<CitaViewModel> ToViewModels(List<CitaDTO> dtos) {
        List<CitaViewModel> modelos = [];
        foreach (CitaDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private CitaViewModel ToViewModel(CitaDTO dto) {
        List<DetalleCitaViewModel> detalles = [];
        foreach (DetalleCitaDTO detalle in dto.Detalles) {
            detalles.Add(ToViewModel(detalle));
        }
        CitaViewModel modelo = new CitaViewModel {
            Id = dto.Id,
            FechaHora = DateTimeValue(dto.FechaHora),
            Estado = ParseEstadoCita(dto.Estado),
            IdMascota = dto.Mascota?.Id ?? 0,
            NombreMascota = dto.Mascota?.Nombre ?? "",
            FotoMascota = dto.Mascota?.Foto,
            IdCliente = dto.Mascota?.Cliente?.Id ?? 0,
            NombreCliente = Nombre(dto.Mascota?.Cliente),
            IdAdministrador = dto.Administrador?.Id,
            NombreAdministrador = Nombre(dto.Administrador),
            EsEmergencia = dto.EsEmergencia,
            Detalles = detalles
        };
        return modelo;
    }

    private CitaDTO ToDto(CitaViewModel viewModel) {
        List<DetalleCitaDTO> detalles = [];
        foreach (DetalleCitaViewModel detalle in viewModel.Detalles) {
            detalles.Add(ToDto(detalle));
        }
        CitaDTO dto = new CitaDTO {
            Id = viewModel.Id,
            FechaHora = DateText(viewModel.FechaHora),
            Estado = viewModel.Estado.ToString(),
            Mascota = new MascotaDTO { Id = viewModel.IdMascota },
            Administrador = viewModel.IdAdministrador.HasValue ? new AdministradorDTO { Id = viewModel.IdAdministrador.Value } : null,
            EsEmergencia = viewModel.EsEmergencia,
            Detalles = detalles
        };
        return dto;
    }

    private DetalleCitaViewModel ToViewModel(DetalleCitaDTO dto) {
        DetalleCitaViewModel modelo = new DetalleCitaViewModel {
            Id = dto.Id,
            IdServicio = dto.Servicio?.Id ?? 0,
            NombreServicio = dto.Servicio?.Nombre ?? "",
            IdVeterinario = dto.Veterinario?.Id ?? 0,
            NombreVeterinario = Nombre(dto.Veterinario),
            Observaciones = dto.Observaciones
        };
        return modelo;
    }

    private DetalleCitaDTO ToDto(DetalleCitaViewModel viewModel) {
        DetalleCitaDTO dto = new DetalleCitaDTO {
            Id = viewModel.Id,
            Servicio = new ServicioDTO { Id = viewModel.IdServicio },
            Veterinario = new VeterinarioDTO { Id = viewModel.IdVeterinario },
            Observaciones = viewModel.Observaciones
        };
        return dto;
    }

    private EstadoCita ParseEstadoCita(string? valor) {
        if (Enum.TryParse(valor, true, out EstadoCita resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de EstadoCita: '{valor}'.");
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

    private string Nombre(AdministradorDTO? persona) {
        if (persona == null) return "";
        return Nombre(persona.Nombres, persona.ApellidoPaterno, persona.ApellidoMaterno);
    }
}
