using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios.Rest;

public class AccesoRestService : IAccesoService {
    private readonly string accesoUrl;

    public AccesoRestService(IConfiguration configuration) {
        accesoUrl = $"{RestConfig.BaseUrl(configuration)}/acceso";
    }

    public Task<string> SolicitarRegistro(RegistroViewModel registro) =>
        Enviar("registro", RestMapper.ToDto(registro));

    public Task<string> ConfirmarRegistro(string token) =>
        Enviar("registro/confirmar", new { token });

    public Task<string> SolicitarRecuperacion(string correo) =>
        Enviar("recuperacion", new { correo = correo.Trim() });

    public Task<string> RestablecerContrasena(string token, string password) =>
        Enviar("recuperacion/restablecer", new { token, password });

    private async Task<string> Enviar<TRequest>(string ruta, TRequest payload) {
        var respuesta = await HttpUtils.DoPost<TRequest, MensajeDTO>($"{accesoUrl}/{ruta}", payload);
        return respuesta.Mensaje ?? "";
    }
}
