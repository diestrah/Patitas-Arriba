using PatitasArriba.Web.Datos;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Seguridad {
    // MIENTRAS NO SE ENLAZA CON EL BACKEND: simula la sesión para mostrar qué ve cada rol (RF01).
    // No valida usuario ni contraseña. Al enlazar se reemplazará por POST /login, que devuelve
    // la cuenta, la persona y su rol (RN-CU-04).
    public class SesionDemo {
        public RolUsuario? Rol { get; private set; }

        // Nombre de la persona que inició sesión (se muestra en el menú)
        public string NombreUsuario { get; private set; } = "";

        // Id del cliente, veterinario o administrador que inició sesión, y de su cuenta.
        // Sirven para filtrar "mis citas", "mi agenda", y para no dejar eliminar la cuenta propia.
        public int IdPersona { get; private set; }
        public int IdCuenta { get; private set; }

        public bool HaIniciadoSesion => Rol != null;
        public bool EsAdministrador => Rol == RolUsuario.ADMINISTRADOR;

        // Cada rol entra con una persona que existe en los datos de ejemplo
        public void IniciarSesion(RolUsuario rol) {
            PersonaViewModel persona = rol switch {
                RolUsuario.ADMINISTRADOR => DatosEjemplo.Administradores().First(a => a.Id == 1),
                RolUsuario.VETERINARIO => DatosEjemplo.Veterinarios().First(v => v.Id == 1),
                _ => DatosEjemplo.Clientes().First(c => c.Id == 1)
            };
            Rol = rol;
            IdPersona = persona.Id;
            IdCuenta = persona.IdCuenta;
            NombreUsuario = persona.NombreCompleto;
        }

        public void CerrarSesion() {
            Rol = null;
            IdPersona = 0;
            IdCuenta = 0;
            NombreUsuario = "";
        }
    }
}
