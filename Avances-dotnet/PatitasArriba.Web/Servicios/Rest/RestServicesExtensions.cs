namespace PatitasArriba.Web.Servicios.Rest;

public static class RestServicesExtensions {
    public static IServiceCollection AddPatitasRestServices(this IServiceCollection services) {
        services.AddScoped<IAccesoService, AccesoRestService>();
        services.AddScoped<IAdministradorService, AdministradorRestService>();
        services.AddScoped<IArticuloService, ArticuloRestService>();
        services.AddScoped<IAtencionService, AtencionRestService>();
        services.AddScoped<IBoletaService, BoletaRestService>();
        services.AddScoped<ICategoriaService, CategoriaRestService>();
        services.AddScoped<ICitaService, CitaRestService>();
        services.AddScoped<IClienteService, ClienteRestService>();
        services.AddScoped<ICuentaService, CuentaRestService>();
        services.AddScoped<IDiagnosticoService, DiagnosticoRestService>();
        services.AddScoped<IHorarioService, HorarioRestService>();
        services.AddScoped<IMascotaService, MascotaRestService>();
        services.AddScoped<IRecetaService, RecetaRestService>();
        services.AddScoped<IServicioService, ServicioRestService>();
        services.AddScoped<IVeterinarioService, VeterinarioRestService>();
        return services;
    }
}
