using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Seguridad {
    public static class Permisos {
        // Roles del sistema (RF01)
        public const string Administrador = "ADMINISTRADOR";
        public const string Veterinario = "VETERINARIO";
        public const string Cliente = "CLIENTE";

        // Qué roles pueden usar cada opción, según el catálogo de requerimientos
        public static readonly IReadOnlyDictionary<string, string[]> RolesPorPermiso = new Dictionary<string, string[]> {
            ["Clientes"] = [Administrador, Veterinario],
            ["Mascotas"] = [Administrador, Veterinario],
            ["Citas"] = [Administrador],
            ["Agenda"] = [Veterinario],
            ["Atenciones"] = [Veterinario],
            ["Recetas"] = [Veterinario],
            ["Servicios"] = [Administrador, Veterinario],
            ["Diagnosticos"] = [Administrador, Veterinario],
            ["Articulos"] = [Administrador, Veterinario],
            ["Categorias"] = [Administrador],
            ["Personal"] = [Administrador],
            ["Horarios"] = [Administrador],
            ["CuentasUsuario"] = [Administrador],
            ["Boletas"] = [Administrador],
            ["ReporteAgenda"] = [Administrador, Veterinario],
            ["ReporteHistorial"] = [Administrador, Veterinario],
            ["MisMascotas"] = [Cliente],
            ["MisCitas"] = [Cliente]
        };

        public static readonly IReadOnlyDictionary<string, List<OpcionMenu>> Menu = new Dictionary<string, List<OpcionMenu>> {
            ["Clientes"] = [
                new OpcionMenu { Texto = "Clientes", Href = "clientes", Permiso = "Clientes" },
                new OpcionMenu { Texto = "Mascotas", Href = "mascotas", Permiso = "Mascotas" }
            ],
            ["Citas"] = [
                new OpcionMenu { Texto = "Gestión de citas", Href = "citas", Permiso = "Citas" },
                new OpcionMenu { Texto = "Mi agenda", Href = "mi-agenda", Permiso = "Agenda" }
            ],
            ["Clínica"] = [
                new OpcionMenu { Texto = "Atenciones", Href = "atenciones", Permiso = "Atenciones" },
                new OpcionMenu { Texto = "Recetas médicas", Href = "recetas", Permiso = "Recetas" }
            ],
            ["Catálogos"] = [
                new OpcionMenu { Texto = "Servicios", Href = "servicios", Permiso = "Servicios" },
                new OpcionMenu { Texto = "Diagnósticos", Href = "diagnosticos", Permiso = "Diagnosticos" }
            ],
            ["Inventario"] = [
                new OpcionMenu { Texto = "Artículos", Href = "articulos", Permiso = "Articulos" },
                new OpcionMenu { Texto = "Categorías", Href = "categorias", Permiso = "Categorias" }
            ],
            ["Personal"] = [
                new OpcionMenu { Texto = "Veterinarios", Href = "veterinarios", Permiso = "Personal" },
                new OpcionMenu { Texto = "Administradores", Href = "administradores", Permiso = "Personal" },
                new OpcionMenu { Texto = "Horarios de atención", Href = "horarios", Permiso = "Horarios" },
                new OpcionMenu { Texto = "Cuentas de usuario", Href = "cuentas-usuario", Permiso = "CuentasUsuario" }
            ],
            ["Caja"] = [
                new OpcionMenu { Texto = "Boletas", Href = "boletas", Permiso = "Boletas" }
            ],
            ["Reportes"] = [
                new OpcionMenu { Texto = "Agenda diaria", Href = "reportes/agenda-diaria", Permiso = "ReporteAgenda" },
                new OpcionMenu { Texto = "Historial clínico", Href = "reportes/historial-clinico", Permiso = "ReporteHistorial" }
            ],
            ["Mi portal"] = [
                new OpcionMenu { Texto = "Mis mascotas", Href = "mis-mascotas", Permiso = "MisMascotas" },
                new OpcionMenu { Texto = "Mis citas", Href = "mis-citas", Permiso = "MisCitas" },
                new OpcionMenu { Texto = "Reservar cita", Href = "mis-citas/nueva", Permiso = "MisCitas" }
            ]
        };

        public static bool TienePermiso(RolUsuario? rol, string permiso) {
            if (rol == null) {
                return false;
            }
            string[] rolesPermitidos = RolesPorPermiso[permiso];
            return rolesPermitidos.Contains(rol.Value.ToString());
        }

        // Menú con solo las opciones que puede usar el rol
        public static Dictionary<string, List<OpcionMenu>> MenuDelRol(RolUsuario? rol) {
            Dictionary<string, List<OpcionMenu>> menu = [];
            foreach (KeyValuePair<string, List<OpcionMenu>> grupo in Menu) {
                List<OpcionMenu> opciones = [];
                foreach (OpcionMenu opcion in grupo.Value) {
                    if (TienePermiso(rol, opcion.Permiso)) {
                        opciones.Add(opcion);
                    }
                }
                if (opciones.Count > 0) {
                    menu[grupo.Key] = opciones;
                }
            }
            return menu;
        }
    }
}
