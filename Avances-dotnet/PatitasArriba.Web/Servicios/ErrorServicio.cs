using System.Text.Json;

namespace PatitasArriba.Web.Servicios;

// Convierte la excepción de un servicio REST en un texto para mostrar en pantalla.
// Si el backend respondió, HttpUtils ya puso en Message el "error" de Java; si no hubo respuesta
// (GlassFish apagado, URL mal configurada, tiempo agotado) se muestra un mensaje genérico.
// Uso: catch (Exception ex) when (ErrorServicio.EsDeServicio(ex)) { Error = ErrorServicio.Desde(ex); }
public static class ErrorServicio {
    public const string SinConexion = "No se pudo conectar con el servidor. Inténtalo de nuevo en unos minutos.";

    public static bool EsDeServicio(Exception ex) =>
        ex is HttpRequestException or TaskCanceledException or JsonException;

    public static string Desde(Exception ex) =>
        ex is HttpRequestException { StatusCode: not null } http ? http.Message : SinConexion;
}
