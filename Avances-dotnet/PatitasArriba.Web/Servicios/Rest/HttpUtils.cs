using System.Text.Json;

namespace PatitasArriba.Web.Servicios.Rest;

public static class HttpUtils {
    private static readonly HttpClient http = new();

    private static async Task CheckResponse(HttpResponseMessage response) {
        if (response.IsSuccessStatusCode) return;
        var message = $"HTTP {(int)response.StatusCode}: {response.ReasonPhrase}";
        var body = await response.Content.ReadAsStringAsync();
        try {
            using var json = JsonDocument.Parse(body);
            if (json.RootElement.TryGetProperty("error", out var error)
                && error.ValueKind == JsonValueKind.String) message = error.GetString() ?? message;
        } catch (JsonException) { /* Una página HTML de error no es el contrato JSON de la API. */ }
        throw new HttpRequestException(message, null, response.StatusCode);
    }
    private static async Task<T> Read<T>(HttpResponseMessage response) {
        await CheckResponse(response);
        return await response.Content.ReadFromJsonAsync<T>()
            ?? throw new JsonException("El servicio devolvió JSON nulo cuando se esperaba un resultado.");
    }
    public static async Task<TResponse> DoGet<TResponse>(string url) {
        using var response = await http.GetAsync(url);
        return await Read<TResponse>(response);
    }
    public static async Task<TResponse> DoPost<TRequest, TResponse>(string url, TRequest payload) {
        using var response = await http.PostAsJsonAsync(url, payload);
        return await Read<TResponse>(response);
    }
    public static async Task<TResponse> DoPut<TRequest, TResponse>(string url, TRequest payload) {
        using var response = await http.PutAsJsonAsync(url, payload);
        return await Read<TResponse>(response);
    }
    public static async Task DoPut<TRequest>(string url, TRequest payload) {
        using var response = await http.PutAsJsonAsync(url, payload);
        await CheckResponse(response);
    }
    public static async Task DoDelete(string url) {
        using var response = await http.DeleteAsync(url);
        await CheckResponse(response);
    }
}
