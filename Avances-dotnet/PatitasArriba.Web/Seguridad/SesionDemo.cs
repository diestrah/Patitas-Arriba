using PatitasArriba.Web.Datos;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Seguridad {
    // MIENTRAS NO SE ENLAZA CON EL BACKEND: simula la sesión con las cuentas de los datos de ejemplo.
    // El rol no se elige: se deduce de la cuenta, como hará POST /login (RN-CU-04).
    // Cualquier contraseña no vacía sirve; al enlazar, la valida el backend.
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

        // Busca la cuenta por su correo y entra con la persona dueña de esa cuenta.
        // Devuelve false si no existe (el mensaje es siempre el mismo, RN-CU-05).
        public bool IniciarSesion(string correo, string password) {
            string texto = correo.Trim();
            CuentaUsuarioViewModel? cuenta = DatosEjemplo.CuentasUsuario().FirstOrDefault(c =>
                c.Correo.Equals(texto, StringComparison.OrdinalIgnoreCase));
            if (cuenta == null || string.IsNullOrWhiteSpace(password)) {
                return false;
            }
            PersonaViewModel? persona = cuenta.Rol switch {
                RolUsuario.ADMINISTRADOR => DatosEjemplo.Administradores().FirstOrDefault(a => a.IdCuenta == cuenta.Id),
                RolUsuario.VETERINARIO => DatosEjemplo.Veterinarios().FirstOrDefault(v => v.IdCuenta == cuenta.Id),
                _ => DatosEjemplo.Clientes().FirstOrDefault(c => c.IdCuenta == cuenta.Id)
            };
            if (persona == null) {
                return false;
            }
            Rol = cuenta.Rol;
            IdPersona = persona.Id;
            IdCuenta = cuenta.Id;
            NombreUsuario = persona.NombreCompleto;
            Nombres = persona.Nombres;
            NombreCorto = $"{persona.Nombres.Split(' ')[0]} {persona.ApellidoPaterno}";
            return true;
        }

        public void CerrarSesion() {
            Rol = null;
            IdPersona = 0;
            IdCuenta = 0;
            NombreUsuario = "";
            Nombres = "";
            NombreCorto = "";
        }
    }
}
