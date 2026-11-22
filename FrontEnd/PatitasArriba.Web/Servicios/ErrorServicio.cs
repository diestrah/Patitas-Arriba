using System.Text.Json;

namespace PatitasArriba.Web.Servicios;

// Convierte la excepción de un servicio REST en un texto para mostrar en pantalla.
// Si el backend respondió, HttpUtils ya puso en Message el "error" de Java; si no hubo respuesta
// (GlassFish apagado, URL mal configurada, tiempo agotado) se muestra un mensaje genérico.
// Uso: catch (Exception ex) when (ErrorServicio.EsDeServicio(ex)) { Error = ErrorServicio.Desde(ex); }
public static class ErrorServicio {
    public const string SinConexion = "No se pudo conectar con el servidor. Inténtalo de nuevo en unos minutos.";
    public const string ErrorInterno = "Ocurrió un problema en el servidor. Inténtalo de nuevo en unos minutos.";

    public static bool EsDeServicio(Exception ex) =>
        ex is HttpRequestException or TaskCanceledException or JsonException;

    public static string Desde(Exception ex) {
        if (ex is not HttpRequestException { StatusCode: not null } http) return SinConexion;
        // "Error inesperado: ..." (ErrorInesperadoMapper de Java) trae el detalle técnico de la excepción,
        // muchas veces en inglés (p. ej. "Failed to initialize pool: Communications link failure").
        // "HTTP 404: Not Found" es una respuesta sin el JSON de la API. Ninguno se muestra al usuario.
        if (http.Message.StartsWith("Error inesperado", StringComparison.Ordinal)
            || http.Message.StartsWith("HTTP ", StringComparison.Ordinal)) {
            return ErrorInterno;
        }
        // Reglas de negocio y "no se pudo..." del backend: ya vienen en español y para el usuario
        return http.Message;
    }
}
