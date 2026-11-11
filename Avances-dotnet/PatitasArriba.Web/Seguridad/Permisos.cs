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
            ["AgendaDiaria"] = [Administrador],
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
                new OpcionMenu { Texto = "Clientes", Href = "clientes", Permiso = "Clientes", Icono = "users" },
                new OpcionMenu { Texto = "Mascotas", Href = "mascotas", Permiso = "Mascotas", Icono = "paw" }
            ],
            ["Citas"] = [
                new OpcionMenu { Texto = "Agenda diaria", Href = "agenda", Permiso = "AgendaDiaria", Icono = "calendar" },
                new OpcionMenu { Texto = "Gestión de citas", Href = "citas", Permiso = "Citas", Icono = "calendar" },
                new OpcionMenu { Texto = "Mi agenda", Href = "mi-agenda", Permiso = "Agenda", Icono = "clock" }
            ],
            ["Clínica"] = [
                new OpcionMenu { Texto = "Atenciones", Href = "atenciones", Permiso = "Atenciones", Icono = "stethoscope" },
                new OpcionMenu { Texto = "Recetas médicas", Href = "recetas", Permiso = "Recetas", Icono = "file-text" }
            ],
            ["Catálogos"] = [
                new OpcionMenu { Texto = "Servicios", Href = "servicios", Permiso = "Servicios", Icono = "tag" },
                new OpcionMenu { Texto = "Diagnósticos", Href = "diagnosticos", Permiso = "Diagnosticos", Icono = "clipboard" }
            ],
            ["Inventario"] = [
                new OpcionMenu { Texto = "Artículos", Href = "articulos", Permiso = "Articulos", Icono = "box" },
                new OpcionMenu { Texto = "Categorías", Href = "categorias", Permiso = "Categorias", Icono = "list" }
            ],
            ["Personal"] = [
                new OpcionMenu { Texto = "Veterinarios", Href = "veterinarios", Permiso = "Personal", Icono = "stethoscope" },
                new OpcionMenu { Texto = "Administradores", Href = "administradores", Permiso = "Personal", Icono = "user" },
                new OpcionMenu { Texto = "Horarios de atención", Href = "horarios", Permiso = "Horarios", Icono = "clock" },
                new OpcionMenu { Texto = "Cuentas de usuario", Href = "cuentas-usuario", Permiso = "CuentasUsuario", Icono = "key" }
            ],
            ["Caja"] = [
                new OpcionMenu { Texto = "Boletas", Href = "boletas", Permiso = "Boletas", Icono = "receipt" }
            ],
            ["Reportes"] = [
                new OpcionMenu { Texto = "Reporte de agenda diaria", Href = "reportes/agenda-diaria", Permiso = "ReporteAgenda", Icono = "bar-chart" },
                new OpcionMenu { Texto = "Historial clínico", Href = "reportes/historial-clinico", Permiso = "ReporteHistorial", Icono = "clipboard" }
            ],
            ["Mi portal"] = [
                new OpcionMenu { Texto = "Mis mascotas", Href = "mis-mascotas", Permiso = "MisMascotas", Icono = "paw" },
                new OpcionMenu { Texto = "Mis citas", Href = "mis-citas", Permiso = "MisCitas", Icono = "calendar" }
            ]
        };

        // Ícono de cada grupo del menú superior
        public static string IconoDeGrupo(string grupo) {
            return grupo switch {
                "Clientes" => "users",
                "Citas" => "calendar",
                "Clínica" => "stethoscope",
                "Catálogos" => "tag",
                "Inventario" => "box",
                "Personal" => "user",
                "Caja" => "receipt",
                "Reportes" => "bar-chart",
                "Mi portal" => "paw",
                _ => "list"
            };
        }

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
