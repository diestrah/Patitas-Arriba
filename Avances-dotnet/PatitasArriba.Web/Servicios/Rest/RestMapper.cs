using System.Globalization;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

internal static class RestMapper {
    internal static T EnumValue<T>(string? value) where T : struct, Enum =>
        Enum.TryParse<T>(value, true, out var parsed) && Enum.IsDefined(parsed)
            ? parsed : throw new FormatException($"Valor desconocido de {typeof(T).Name}: '{value}'.");
    internal static DateTime? DateTimeValue(string? value) => string.IsNullOrWhiteSpace(value)
        ? null : DateTime.Parse(value, CultureInfo.InvariantCulture, DateTimeStyles.RoundtripKind);
    internal static DateOnly? DateValue(string? value) => string.IsNullOrWhiteSpace(value)
        ? null : DateOnly.Parse(value, CultureInfo.InvariantCulture);
    internal static string? DateText(DateTime? value) => value?.ToString("yyyy-MM-dd'T'HH:mm:ss", CultureInfo.InvariantCulture);
    internal static string? DateText(DateOnly? value) => value?.ToString("yyyy-MM-dd", CultureInfo.InvariantCulture);
    internal static string Nombre(string? nombres, string? paterno, string? materno) =>
        string.Join(" ", new[] { nombres, paterno, materno }.Where(x => !string.IsNullOrWhiteSpace(x)));
    internal static string Nombre(ClienteDTO? d) => d == null ? "" : Nombre(d.Nombres, d.ApellidoPaterno, d.ApellidoMaterno);
    internal static string Nombre(VeterinarioDTO? d) => d == null ? "" : Nombre(d.Nombres, d.ApellidoPaterno, d.ApellidoMaterno);
    internal static string Nombre(AdministradorDTO? d) => d == null ? "" : Nombre(d.Nombres, d.ApellidoPaterno, d.ApellidoMaterno);

    internal static CategoriaViewModel ToViewModel(CategoriaDTO d) => new() {
        Id = d.Id,
        Nombre = d.Nombre ?? "",
        Descripcion = d.Descripcion ?? ""
    };
    internal static CategoriaDTO ToDto(CategoriaViewModel m) => new() {
        Id = m.Id,
        Nombre = m.Nombre,
        Descripcion = m.Descripcion
    };

    internal static DiagnosticoViewModel ToViewModel(DiagnosticoDTO d) => new() {
        Id = d.Id,
        NombreEnfermedad = d.NombreEnfermedad ?? "",
        Descripcion = d.Descripcion ?? ""
    };
    internal static DiagnosticoDTO ToDto(DiagnosticoViewModel m) => new() {
        Id = m.Id,
        NombreEnfermedad = m.NombreEnfermedad,
        Descripcion = m.Descripcion
    };

    internal static ServicioViewModel ToViewModel(ServicioDTO d) => new() {
        Id = d.Id,
        PrecioBase = d.PrecioBase,
        Nombre = d.Nombre ?? "",
        Descripcion = d.Descripcion ?? "",
        TipoServicio = EnumValue<TipoServicio>(d.Tipo),
        DuracionEstimada = d.DuracionEstimada > 0 ? d.DuracionEstimada : null
    };
    internal static ServicioDTO ToDto(ServicioViewModel m) => new() {
        Id = m.Id,
        PrecioBase = m.PrecioBase,
        Nombre = m.Nombre,
        Descripcion = m.Descripcion,
        Tipo = m.TipoServicio.ToString(),
        DuracionEstimada = m.DuracionEstimada
    };

    internal static ArticuloViewModel ToViewModel(ArticuloDTO d) => new() {
        Id = d.Id,
        PrecioBase = d.PrecioBase,
        StockActual = d.StockActual,
        StockMinimo = d.StockMinimo,
        Nombre = d.Nombre ?? "",
        Descripcion = d.Descripcion ?? "",
        Categorias = d.Categorias.Select(c => new OpcionSeleccion { Id = c.Id, Texto = c.Nombre ?? "", Seleccionado = true }).ToList()
    };
    internal static ArticuloDTO ToDto(ArticuloViewModel m) => new() {
        Id = m.Id,
        PrecioBase = m.PrecioBase,
        StockActual = m.StockActual,
        StockMinimo = m.StockMinimo,
        Nombre = m.Nombre,
        Descripcion = m.Descripcion,
        Categorias = m.Categorias.Where(c => c.Seleccionado).Select(c => new CategoriaDTO { Id = c.Id }).ToList()
    };

    internal static HorarioViewModel ToViewModel(HorarioDTO d) => new() {
        Id = d.Id,
        DiaSemana = EnumValue<DiaSemana>(d.DiaSemana),
        HoraInicio = TimeOnly.Parse(d.HoraInicio ?? throw new FormatException("Falta horaInicio"), CultureInfo.InvariantCulture),
        HoraFin = TimeOnly.Parse(d.HoraFin ?? throw new FormatException("Falta horaFin"), CultureInfo.InvariantCulture)
    };
    internal static HorarioDTO ToDto(HorarioViewModel m) => new() {
        Id = m.Id,
        DiaSemana = m.DiaSemana.ToString(),
        HoraInicio = m.HoraInicio.ToString("HH:mm:ss", CultureInfo.InvariantCulture),
        HoraFin = m.HoraFin.ToString("HH:mm:ss", CultureInfo.InvariantCulture)
    };

    internal static ClienteViewModel ToViewModel(ClienteDTO d) => new() {
        Id = d.Id,
        IdCuenta = d.Cuenta?.Id ?? 0,
        NombreUsuario = d.Cuenta?.NombreUsuario ?? "",
        Correo = d.Cuenta?.Correo ?? "",
        Dni = d.Dni ?? "",
        Nombres = d.Nombres ?? "",
        ApellidoPaterno = d.ApellidoPaterno ?? "",
        ApellidoMaterno = d.ApellidoMaterno ?? "",
        Telefono = d.Telefono ?? ""
    };
    internal static ClienteDTO ToDto(ClienteViewModel m) => new() {
        Id = m.Id,
        Cuenta = new CuentaDTO { Id = m.IdCuenta, Correo = m.Correo },
        Dni = m.Dni,
        Nombres = m.Nombres,
        ApellidoPaterno = m.ApellidoPaterno,
        ApellidoMaterno = m.ApellidoMaterno,
        Telefono = m.Telefono
    };

    internal static AdministradorViewModel ToViewModel(AdministradorDTO d) => new() {
        Id = d.Id,
        IdCuenta = d.Cuenta?.Id ?? 0,
        NombreUsuario = d.Cuenta?.NombreUsuario ?? "",
        Correo = d.Cuenta?.Correo ?? "",
        Dni = d.Dni ?? "",
        Nombres = d.Nombres ?? "",
        ApellidoPaterno = d.ApellidoPaterno ?? "",
        ApellidoMaterno = d.ApellidoMaterno ?? "",
        Telefono = d.Telefono ?? "",
        Horarios = d.Horarios.Select(h => new OpcionSeleccion { Id = h.Id, Texto = $"{h.DiaSemana} {h.HoraInicio} - {h.HoraFin}", Seleccionado = true }).ToList()
    };
    internal static AdministradorDTO ToDto(AdministradorViewModel m) => new() {
        Id = m.Id,
        Cuenta = new CuentaDTO { Id = m.IdCuenta, Correo = m.Correo },
        Dni = m.Dni,
        Nombres = m.Nombres,
        ApellidoPaterno = m.ApellidoPaterno,
        ApellidoMaterno = m.ApellidoMaterno,
        Telefono = m.Telefono,
        Horarios = m.Horarios.Where(h => h.Seleccionado).Select(h => new HorarioDTO { Id = h.Id }).ToList()
    };

    internal static VeterinarioViewModel ToViewModel(VeterinarioDTO d) => new() {
        Id = d.Id,
        IdCuenta = d.Cuenta?.Id ?? 0,
        NombreUsuario = d.Cuenta?.NombreUsuario ?? "",
        Correo = d.Cuenta?.Correo ?? "",
        Dni = d.Dni ?? "",
        Nombres = d.Nombres ?? "",
        ApellidoPaterno = d.ApellidoPaterno ?? "",
        ApellidoMaterno = d.ApellidoMaterno ?? "",
        Telefono = d.Telefono ?? "",
        Horarios = d.Horarios.Select(h => new OpcionSeleccion { Id = h.Id, Texto = $"{h.DiaSemana} {h.HoraInicio} - {h.HoraFin}", Seleccionado = true }).ToList(),
        NumeroColegiatura = d.NumeroColegiatura ?? ""
    };
    internal static VeterinarioDTO ToDto(VeterinarioViewModel m) => new() {
        Id = m.Id,
        Cuenta = new CuentaDTO { Id = m.IdCuenta, Correo = m.Correo },
        Dni = m.Dni,
        Nombres = m.Nombres,
        ApellidoPaterno = m.ApellidoPaterno,
        ApellidoMaterno = m.ApellidoMaterno,
        Telefono = m.Telefono,
        Horarios = m.Horarios.Where(h => h.Seleccionado).Select(h => new HorarioDTO { Id = h.Id }).ToList(),
        NumeroColegiatura = m.NumeroColegiatura
    };

    internal static CuentaUsuarioViewModel ToViewModel(CuentaDTO d) => new() {
        Id = d.Id,
        NombreUsuario = d.NombreUsuario ?? "",
        Correo = d.Correo ?? ""
    };
    internal static CuentaDTO ToDto(CuentaUsuarioViewModel m) => new() {
        Id = m.Id,
        NombreUsuario = m.NombreUsuario,
        Correo = m.Correo,
        Password = m.Password
    };

    // La contraseña se envía tal cual; los demás campos sin espacios sobrantes (el backend valida el formato)
    internal static RegistroDTO ToDto(RegistroViewModel m) => new() {
        Dni = m.Dni.Trim(),
        Nombres = m.Nombres.Trim(),
        ApellidoPaterno = m.ApellidoPaterno.Trim(),
        ApellidoMaterno = m.ApellidoMaterno.Trim(),
        Telefono = m.Telefono.Trim(),
        Correo = m.Correo.Trim(),
        Password = m.Password
    };

    internal static MascotaViewModel ToViewModel(MascotaDTO d) => new() {
        Id = d.Id,
        IdCliente = d.Cliente?.Id ?? 0,
        NombreDueno = Nombre(d.Cliente),
        Nombre = d.Nombre ?? "",
        Sexo = EnumValue<SexoMascota>(d.Sexo),
        TipoMascota = EnumValue<TipoMascota>(d.TipoMascota),
        // Peso 0 significa "no registrado" en el backend (RN-MA-02)
        Peso = d.Peso > 0 ? d.Peso : null,
        FechaNacimiento = DateValue(d.FechaNacimiento),
        Raza = d.Raza,
        Foto = d.Foto
    };
    internal static MascotaDTO ToDto(MascotaViewModel m) => new() {
        Id = m.Id,
        Cliente = new ClienteDTO { Id = m.IdCliente },
        Nombre = m.Nombre,
        Sexo = m.Sexo.ToString(),
        TipoMascota = m.TipoMascota.ToString(),
        Peso = m.Peso ?? 0,
        FechaNacimiento = DateText(m.FechaNacimiento),
        Raza = m.Raza,
        // Se envía siempre: si no viajara, Java guardaría la mascota sin foto
        Foto = m.Foto
    };

    internal static DetalleCitaViewModel ToViewModel(DetalleCitaDTO d) => new() {
        Id = d.Id,
        IdServicio = d.Servicio?.Id ?? 0,
        NombreServicio = d.Servicio?.Nombre ?? "",
        IdVeterinario = d.Veterinario?.Id ?? 0,
        NombreVeterinario = Nombre(d.Veterinario),
        Observaciones = d.Observaciones
    };
    internal static DetalleCitaDTO ToDto(DetalleCitaViewModel m) => new() {
        Id = m.Id,
        Servicio = new ServicioDTO { Id = m.IdServicio },
        Veterinario = new VeterinarioDTO { Id = m.IdVeterinario },
        Observaciones = m.Observaciones
    };

    internal static CitaViewModel ToViewModel(CitaDTO d) => new() {
        Id = d.Id,
        FechaHora = DateTimeValue(d.FechaHora),
        Estado = EnumValue<EstadoCita>(d.Estado),
        IdMascota = d.Mascota?.Id ?? 0,
        NombreMascota = d.Mascota?.Nombre ?? "",
        FotoMascota = d.Mascota?.Foto,
        IdCliente = d.Mascota?.Cliente?.Id ?? 0,
        NombreCliente = Nombre(d.Mascota?.Cliente),
        IdAdministrador = d.Administrador?.Id,
        NombreAdministrador = Nombre(d.Administrador),
        EsEmergencia = d.EsEmergencia,
        Detalles = d.Detalles.Select(ToViewModel).ToList()
    };
    internal static CitaDTO ToDto(CitaViewModel m) => new() {
        Id = m.Id,
        FechaHora = DateText(m.FechaHora),
        Estado = m.Estado.ToString(),
        Mascota = new MascotaDTO { Id = m.IdMascota },
        Administrador = m.IdAdministrador.HasValue ? new AdministradorDTO { Id = m.IdAdministrador.Value } : null,
        EsEmergencia = m.EsEmergencia,
        Detalles = m.Detalles.Select(ToDto).ToList()
    };

    internal static AtencionViewModel ToViewModel(AtencionDTO d) => new() {
        Id = d.Id,
        IdDetalleCita = d.DetalleCita?.Id ?? 0,
        NombreServicio = d.DetalleCita?.Servicio?.Nombre ?? "",
        TipoServicio = EnumValue<TipoServicio>(d.DetalleCita?.Servicio?.Tipo),
        IdVeterinario = d.DetalleCita?.Veterinario?.Id ?? 0,
        NombreVeterinario = Nombre(d.DetalleCita?.Veterinario),
        FechaHora = DateTimeValue(d.FechaHora),
        MotivoConsulta = d.MotivoConsulta,
        Observaciones = d.Observaciones,
        Diagnosticos = d.Diagnosticos.Select(x => new AtencionDiagnosticoViewModel { IdDiagnostico = x.Diagnostico?.Id ?? 0, NombreEnfermedad = x.Diagnostico?.NombreEnfermedad ?? "", NivelGravedad = EnumValue<NivelGravedad>(x.NivelGravedad), DetalleDiagnostico = x.DetalleDiagnostico }).ToList(),
        Insumos = d.InsumosUtilizados.Select(x => new InsumoUtilizadoViewModel { Id = x.Id, IdArticulo = x.Articulo?.Id ?? 0, NombreArticulo = x.Articulo?.Nombre ?? "", PrecioUnitario = x.Articulo?.PrecioBase ?? 0, CantidadUtilizada = x.CantidadUtilizada, EsCobrable = x.EsCobrable }).ToList()
    };
    internal static AtencionDTO ToDto(AtencionViewModel m) => new() {
        Id = m.Id,
        DetalleCita = new DetalleCitaDTO { Id = m.IdDetalleCita },
        FechaHora = DateText(m.FechaHora),
        MotivoConsulta = m.MotivoConsulta,
        Observaciones = m.Observaciones,
        Diagnosticos = m.Diagnosticos.Select(x => new AtencionDiagnosticoDTO { Diagnostico = new DiagnosticoDTO { Id = x.IdDiagnostico }, NivelGravedad = x.NivelGravedad.ToString(), DetalleDiagnostico = x.DetalleDiagnostico }).ToList(),
        InsumosUtilizados = m.Insumos.Select(x => new InsumoUtilizadoDTO { Id = x.Id, Articulo = new ArticuloDTO { Id = x.IdArticulo }, CantidadUtilizada = x.CantidadUtilizada, EsCobrable = x.EsCobrable }).ToList()
    };

    internal static RecetaViewModel ToViewModel(RecetaDTO d) => new() {
        Id = d.Id,
        IdAtencion = d.Atencion?.Id ?? 0,
        IdVeterinario = d.Atencion?.DetalleCita?.Veterinario?.Id ?? 0,
        NombreVeterinario = Nombre(d.Atencion?.DetalleCita?.Veterinario),
        NumeroColegiatura = d.Atencion?.DetalleCita?.Veterinario?.NumeroColegiatura ?? "",
        Diagnostico = string.Join(", ", d.Atencion?.Diagnosticos.Select(x => x.Diagnostico?.NombreEnfermedad) ?? []),
        FechaEmision = DateValue(d.FechaEmision),
        IndicacionesGenerales = d.IndicacionesGenerales,
        Medicamentos = d.Detalles.Select(x => new DetalleRecetaViewModel { IdArticulo = x.Articulo?.Id ?? 0, NombreArticulo = x.Articulo?.Nombre ?? "", Dosis = x.Dosis ?? "", Frecuencia = x.Frecuencia ?? "", DuracionDias = x.DuracionDias, CantidadTotal = x.CantidadTotal }).ToList()
    };
    internal static RecetaDTO ToDto(RecetaViewModel m) => new() {
        Id = m.Id,
        Atencion = new AtencionDTO { Id = m.IdAtencion },
        FechaEmision = DateText(m.FechaEmision),
        IndicacionesGenerales = m.IndicacionesGenerales,
        Detalles = m.Medicamentos.Select(x => new DetalleRecetaDTO { Articulo = new ArticuloDTO { Id = x.IdArticulo }, Dosis = x.Dosis, Frecuencia = x.Frecuencia, DuracionDias = x.DuracionDias, CantidadTotal = x.CantidadTotal }).ToList()
    };

    internal static BoletaViewModel ToViewModel(BoletaDTO d) => new() {
        Id = d.Id,
        IsActivo = d.Activo,
        SerieCorrelativo = d.SerieCorrelativo ?? "",
        Fecha = DateTimeValue(d.Fecha) ?? throw new FormatException("Falta fecha de boleta"),
        IdCliente = d.Cliente?.Id ?? 0,
        NombreCliente = Nombre(d.Cliente),
        DniCliente = d.Cliente?.Dni ?? "",
        MetodoPago = EnumValue<MetodoPago>(d.MetodoPago),
        Lineas = d.DetallesServicio.Select(x => new LineaBoletaViewModel { Tipo = "Servicio", IdDetalleCita = x.DetalleCita?.Id, Descripcion = x.DetalleCita?.Servicio?.Nombre ?? "", Cantidad = x.Cantidad, PrecioUnitario = x.PrecioUnitario }).Concat(d.DetallesArticulo.Select(x => new LineaBoletaViewModel { Tipo = x.InsumoUtilizado == null ? "Artículo" : "Insumo", IdArticulo = x.Articulo?.Id, IdInsumoUtilizado = x.InsumoUtilizado?.Id, Descripcion = x.Articulo?.Nombre ?? "", Cantidad = x.Cantidad, PrecioUnitario = x.PrecioUnitario })).ToList()
    };
    internal static BoletaDTO ToDto(BoletaViewModel m) => new() {
        Id = m.Id,
        Activo = m.IsActivo,
        SerieCorrelativo = m.SerieCorrelativo,
        Fecha = DateText(m.Fecha),
        Cliente = new ClienteDTO { Id = m.IdCliente },
        MetodoPago = m.MetodoPago.ToString(),
        DetallesServicio = m.Lineas.Where(x => x.Tipo == "Servicio").Select(x => new DetalleBoletaServicioDTO { DetalleCita = new DetalleCitaDTO { Id = x.IdDetalleCita ?? 0 }, Cantidad = x.Cantidad }).ToList(),
        DetallesArticulo = m.Lineas.Where(x => x.Tipo != "Servicio").Select(x => new DetalleBoletaArticuloDTO { Articulo = new ArticuloDTO { Id = x.IdArticulo ?? 0 }, InsumoUtilizado = x.IdInsumoUtilizado.HasValue ? new InsumoUtilizadoRefDTO { Id = x.IdInsumoUtilizado.Value } : null, Cantidad = x.Cantidad }).ToList()
    };
}
