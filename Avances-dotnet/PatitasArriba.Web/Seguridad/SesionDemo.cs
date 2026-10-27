using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Seguridad {
    // SOLO PARA EL LAB 9: simula la sesión para mostrar qué ve cada rol (RF01).
    // No valida usuario ni contraseña. Cuando se conecte con el backend se reemplazará
    // por la autenticación con cookies y la autorización por políticas.
    public class SesionDemo {
        public RolUsuario? Rol { get; private set; }
        public string NombreUsuario { get; private set; } = "";

        public bool HaIniciadoSesion => Rol != null;
        public bool EsAdministrador => Rol == RolUsuario.ADMINISTRADOR;

        // Se usan las mismas personas de los datos de ejemplo
        public void IniciarSesion(RolUsuario rol) {
            Rol = rol;
            NombreUsuario = rol switch {
                RolUsuario.ADMINISTRADOR => "Naim Diestra",
                RolUsuario.VETERINARIO => "Carla Vega Soto",
                _ => "Ana Torres Quispe"
            };
        }

        public void CerrarSesion() {
            Rol = null;
            NombreUsuario = "";
        }
    }
}
