namespace PatitasArriba.Web.Servicios.Rest;

internal static class RestConfig {
    // Lee RestApiBaseUrl (incluye contexto y /api/v1) y la valida; sin barra final
    public static string BaseUrl(IConfiguration configuration) {
        var baseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(baseUrl, UriKind.Absolute, out var uri)
            || (uri.Scheme != "http" && uri.Scheme != "https"))
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        return baseUrl;
    }
}
