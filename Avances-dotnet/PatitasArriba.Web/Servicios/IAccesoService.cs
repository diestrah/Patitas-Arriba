using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

// Rutas públicas (sin sesión) de /acceso: registro con verificación de correo y recuperación de contraseña.
// Cada método devuelve el mensaje del backend para mostrarlo tal cual. Ante un error lanza
// HttpRequestException con el StatusCode y el mensaje "error" de Java (p. ej. token vencido -> 400).
public interface IAccesoService {
    // POST /acceso/registro: todavía no crea la cuenta, envía el correo de confirmación (202)
    Task<string> SolicitarRegistro(RegistroViewModel registro);

    // POST /acceso/registro/confirmar: crea la cuenta y el cliente con el token del enlace (201)
    Task<string> ConfirmarRegistro(string token);

    // POST /acceso/recuperacion: responde igual exista o no el correo (202)
    Task<string> SolicitarRecuperacion(string correo);

    // POST /acceso/recuperacion/restablecer: también activa las cuentas creadas por un administrador (200)
    Task<string> RestablecerContrasena(string token, string password);
}
