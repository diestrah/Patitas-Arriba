using Microsoft.AspNetCore.WebUtilities;
using System.Globalization;
using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class MascotaRestService : IMascotaService {
    private string BaseUrl { get; }
    private string BaseUrlMascota { get; }

    public MascotaRestService(IConfiguration configuration) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out Uri? uri)
            || (uri.Scheme != "http" && uri.Scheme != "https")) {
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        }
        BaseUrlMascota = $"{BaseUrl}/mascotas";
    }

    public async Task<List<MascotaViewModel>> FindAll() {
        string url = BaseUrlMascota;
        List<MascotaDTO> dtos = await HttpUtils.DoGet<List<MascotaDTO>>(url);
        return ToViewModels(dtos);
    }

    public async Task<MascotaViewModel?> FindById(int id) {
        string url = $"{BaseUrlMascota}/{id}";
        MascotaDTO dto;
        try {
            dto = await HttpUtils.DoGet<MascotaDTO>(url);
        } catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) {
            return null;
        }
        return ToViewModel(dto);
    }

    public async Task Insert(MascotaViewModel modelo) {
        string url = BaseUrlMascota;
        MascotaDTO dto = ToDto(modelo);
        MascotaDTO creado = await HttpUtils.DoPost<MascotaDTO, MascotaDTO>(url, dto);
        modelo.Id = creado.Id;
    }

    public async Task Update(MascotaViewModel modelo) {
        string url = $"{BaseUrlMascota}/{modelo.Id}";
        MascotaDTO dto = ToDto(modelo);
        await HttpUtils.DoPut<MascotaDTO, MascotaDTO>(url, dto);
    }

    public async Task Delete(int id) {
        string url = $"{BaseUrlMascota}/{id}";
        await HttpUtils.DoDelete(url);
    }

    public async Task<List<MascotaViewModel>> FindByCliente(int id) {
        string url = QueryHelpers.AddQueryString(BaseUrlMascota, "cliente", id.ToString(CultureInfo.InvariantCulture));
        List<MascotaDTO> dtos = await HttpUtils.DoGet<List<MascotaDTO>>(url);
        return ToViewModels(dtos);
    }

    private List<MascotaViewModel> ToViewModels(List<MascotaDTO> dtos) {
        List<MascotaViewModel> modelos = [];
        foreach (MascotaDTO dto in dtos) {
            modelos.Add(ToViewModel(dto));
        }
        return modelos;
    }

    private MascotaViewModel ToViewModel(MascotaDTO dto) {
        MascotaViewModel modelo = new MascotaViewModel {
            Id = dto.Id,
            IdCliente = dto.Cliente?.Id ?? 0,
            NombreDueno = Nombre(dto.Cliente),
            Nombre = dto.Nombre ?? "",
            Sexo = ParseSexoMascota(dto.Sexo),
            TipoMascota = ParseTipoMascota(dto.TipoMascota),
            // Peso 0 significa "no registrado" en el backend (RN-MA-02)
            Peso = dto.Peso > 0 ? dto.Peso : null,
            FechaNacimiento = DateValue(dto.FechaNacimiento),
            Raza = dto.Raza,
            Foto = dto.Foto
        };
        return modelo;
    }

    private MascotaDTO ToDto(MascotaViewModel viewModel) {
        MascotaDTO dto = new MascotaDTO {
            Id = viewModel.Id,
            Cliente = new ClienteDTO { Id = viewModel.IdCliente },
            Nombre = viewModel.Nombre,
            Sexo = viewModel.Sexo.ToString(),
            TipoMascota = viewModel.TipoMascota.ToString(),
            Peso = viewModel.Peso ?? 0,
            FechaNacimiento = DateText(viewModel.FechaNacimiento),
            Raza = viewModel.Raza,
            // La foto se conserva al guardar.
            Foto = viewModel.Foto
        };
        return dto;
    }

    private SexoMascota ParseSexoMascota(string? valor) {
        if (Enum.TryParse(valor, true, out SexoMascota resultado) && Enum.IsDefined(resultado)) {
            return resultado;
        }
        throw new FormatException($"Valor desconocido de SexoMascota: '{valor}'.");
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


}
