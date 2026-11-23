using System.Net;
using System.Security.Claims;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Mvc;
using PatitasArriba.Web.Servicios;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Seguridad;

// Sesión con cookie y permisos por rol, como en el ejemplo del profesor (sesión de clase 21).
// La cookie guarda los datos de POST /login como claims; cada página declara su permiso con
// @attribute [Authorize(Policy = "...")] y Routes.razor (AuthorizeRouteView) la bloquea si el rol no lo tiene.
public static class SeguridadExtensions {
    // Claims propios, además de NameIdentifier (id de la cuenta), Role y Email
    public const string ClaimIdPersona = "id_persona";
    public const string ClaimNombres = "nombres";
    public const string ClaimApellidoPaterno = "apellido_paterno";

    public static void AddSeguridad(this IServiceCollection services) {
        services.AddAuthentication(CookieAuthenticationDefaults.AuthenticationScheme)
            .AddCookie(opciones => {
                opciones.LoginPath = "/login";
                opciones.AccessDeniedPath = "/acceso-denegado";
                opciones.ExpireTimeSpan = TimeSpan.FromHours(8);
            });

        // Una política por permiso, con los roles de Permisos.RolesPorPermiso
        services.AddAuthorization(opciones => {
            foreach (KeyValuePair<string, string[]> permiso in Permisos.RolesPorPermiso) {
                opciones.AddPolicy(permiso.Key, politica => politica.RequireRole(permiso.Value));
            }
        });

        services.AddCascadingAuthenticationState();
        services.AddHttpContextAccessor();
    }

    public static void MapSesionEndpoints(this IEndpointRouteBuilder app) {
        app.MapPost("/cuenta/login", IniciarSesion);
        // Con solo HttpContext de parámetro se tomaría como RequestDelegate y se perdería la redirección
        app.MapPost("/cuenta/logout", (Delegate)CerrarSesion);
    }

    // POST /login en el backend: valida la contraseña y deduce el rol de la cuenta.
    // El mensaje de error es siempre el mismo, sin decir si falló el correo o la contraseña (RN-CU-05)
    private static async Task<IResult> IniciarSesion(HttpContext context, ICuentaService cuentaService,
            [FromForm] string correo, [FromForm] string password) {
        SesionViewModel sesion;
        try {
            sesion = await cuentaService.Login(correo.Trim(), password);
        } catch (HttpRequestException ex) when (ex.StatusCode is HttpStatusCode.Unauthorized or HttpStatusCode.BadRequest) {
            return Results.Redirect("/login?error=credenciales");
        } catch (Exception ex) when (ErrorServicio.EsDeServicio(ex)) {
            return Results.Redirect("/login?error=conexion");
        }

        List<Claim> claims = [
            new Claim(ClaimTypes.NameIdentifier, sesion.Id.ToString()),
            new Claim(ClaimTypes.Name, sesion.NombreUsuario),
            new Claim(ClaimTypes.Email, sesion.Correo),
            new Claim(ClaimTypes.Role, sesion.Rol.ToString()),
            new Claim(ClaimIdPersona, sesion.IdPersona.ToString()),
            new Claim(ClaimNombres, sesion.Nombres),
            new Claim(ClaimApellidoPaterno, sesion.ApellidoPaterno ?? "")
        ];
        ClaimsIdentity identidad = new(claims, CookieAuthenticationDefaults.AuthenticationScheme);
        await context.SignInAsync(new ClaimsPrincipal(identidad));
        return Results.Redirect("/");
    }

    private static async Task<IResult> CerrarSesion(HttpContext context) {
        await context.SignOutAsync();
        return Results.Redirect("/login");
    }
}
