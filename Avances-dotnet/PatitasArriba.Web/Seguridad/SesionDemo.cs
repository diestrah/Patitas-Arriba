using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Seguridad {
    // Sesión del usuario, con los datos que devuelve POST /login. El rol no se elige: lo deduce el backend
    // a partir de la cuenta (RN-CU-04). Vive en el circuito de Blazor: recargar la página la pierde.
    public class SesionDemo {
        public RolUsuario? Rol { get; private set; }

        // Nombre completo de la persona que inició sesión
        public string NombreUsuario { get; private set; } = "";

        // Solo los nombres (para el saludo) y nombre + apellido (para la barra superior)
        public string Nombres { get; private set; } = "";
        public string NombreCorto { get; private set; } = "";

        // Id del cliente, veterinario o administrador que inició sesión, y de su cuenta.
        // Sirven para filtrar "mis citas", "mi agenda", y para no dejar eliminar la cuenta propia.
        public int IdPersona { get; private set; }
        public int IdCuenta { get; private set; }

        public bool HaIniciadoSesion => Rol != null;
        public bool EsAdministrador => Rol == RolUsuario.ADMINISTRADOR;

        public string Iniciales => NombreCorto.Length == 0 ? "?"
            : string.Concat(NombreCorto.Split(' ', StringSplitOptions.RemoveEmptyEntries).Take(2).Select(p => char.ToUpper(p[0])));

        // Correo de la cuenta con la que se inició sesión
        public string Correo { get; private set; } = "";

        // Guarda la sesión que devolvió POST /login (ICuentaService.Login). El backend ya validó la contraseña,
        // que la cuenta y su persona estén activas, y dedujo el rol.
        public void Iniciar(SesionViewModel sesion) {
            Rol = sesion.Rol;
            IdPersona = sesion.IdPersona;
            IdCuenta = sesion.Id;
            Correo = sesion.Correo;
            Nombres = sesion.Nombres;
            string primerNombre = sesion.Nombres.Split(' ', StringSplitOptions.RemoveEmptyEntries).FirstOrDefault() ?? "";
            NombreCorto = string.Join(" ", new[] { primerNombre, sesion.ApellidoPaterno }
                .Where(p => !string.IsNullOrWhiteSpace(p)));
            NombreUsuario = string.Join(" ", new[] { sesion.Nombres, sesion.ApellidoPaterno }
                .Where(p => !string.IsNullOrWhiteSpace(p)));
        }

        public void CerrarSesion() {
            Rol = null;
            IdPersona = 0;
            IdCuenta = 0;
            Correo = "";
            NombreUsuario = "";
            Nombres = "";
            NombreCorto = "";
        }
    }
}
