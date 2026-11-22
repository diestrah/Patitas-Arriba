using System.Security.Claims;
using Microsoft.AspNetCore.Components.Authorization;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Seguridad {
    // Datos del usuario que inició sesión, leídos de los claims de la cookie (ver SeguridadExtensions).
    // El rol no se elige: lo deduce el backend a partir de la cuenta (RN-CU-04). Como vive en la cookie,
    // recargar la página no pierde la sesión.
    public class SesionDemo {
        private readonly AuthenticationStateProvider proveedor;

        public SesionDemo(AuthenticationStateProvider proveedor) {
            this.proveedor = proveedor;
        }

        // En Blazor Server el estado de autenticación ya viene resuelto de la cookie al abrir el circuito
        private ClaimsPrincipal Usuario {
            get {
                Task<AuthenticationState> estado = proveedor.GetAuthenticationStateAsync();
                return estado.IsCompletedSuccessfully ? estado.Result.User : new ClaimsPrincipal(new ClaimsIdentity());
            }
        }

        private string Claim(string tipo) => Usuario.FindFirst(tipo)?.Value ?? "";

        private int ClaimEntero(string tipo) => int.TryParse(Claim(tipo), out int valor) ? valor : 0;

        public RolUsuario? Rol => Usuario.Identity?.IsAuthenticated == true
            && Enum.TryParse(Claim(ClaimTypes.Role), out RolUsuario rol) ? rol : null;

        // Solo los nombres (para el saludo)
        public string Nombres => Claim(SeguridadExtensions.ClaimNombres);

        // Nombre completo de la persona que inició sesión
        public string NombreUsuario => Unir(Nombres, Claim(SeguridadExtensions.ClaimApellidoPaterno));

        // Primer nombre + apellido (para la barra superior)
        public string NombreCorto => Unir(Nombres.Split(' ', StringSplitOptions.RemoveEmptyEntries).FirstOrDefault(),
            Claim(SeguridadExtensions.ClaimApellidoPaterno));

        // Id del cliente, veterinario o administrador que inició sesión, y de su cuenta.
        // Sirven para filtrar "mis citas", "mi agenda", y para no dejar eliminar la cuenta propia.
        public int IdPersona => ClaimEntero(SeguridadExtensions.ClaimIdPersona);
        public int IdCuenta => ClaimEntero(ClaimTypes.NameIdentifier);

        // Correo de la cuenta con la que se inició sesión
        public string Correo => Claim(ClaimTypes.Email);

        public bool HaIniciadoSesion => Rol != null;
        public bool EsAdministrador => Rol == RolUsuario.ADMINISTRADOR;

        public string Iniciales => NombreCorto.Length == 0 ? "?"
            : string.Concat(NombreCorto.Split(' ', StringSplitOptions.RemoveEmptyEntries).Take(2).Select(p => char.ToUpper(p[0])));

        private static string Unir(params string?[] partes) =>
            string.Join(" ", partes.Where(p => !string.IsNullOrWhiteSpace(p)));
    }
}
