using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // Consultas sobre las citas de un cliente que usan el inicio, Mis mascotas y Mis citas del portal.
    // Trabajan con la lista de citas del cliente (al enlazar: GET /citas?cliente={id}).
    public static class PortalCliente {
        // Citas activas (pendientes o aprobadas) que todavía no ocurren, la más cercana primero
        public static List<CitaViewModel> Proximas(List<CitaViewModel> citas) {
            return citas
                .Where(c => c.EsModificable && c.FechaHora > DateTime.Now)
                .OrderBy(c => c.FechaHora)
                .ToList();
        }

        // Todo lo demás: ya ocurridas, completadas, rechazadas o canceladas; la más reciente primero
        public static List<CitaViewModel> Anteriores(List<CitaViewModel> citas) {
            return citas
                .Where(c => !(c.EsModificable && c.FechaHora > DateTime.Now))
                .OrderByDescending(c => c.FechaHora)
                .ToList();
        }

        public static CitaViewModel? UltimaCompletada(List<CitaViewModel> citas, int idMascota) {
            return citas
                .Where(c => c.IdMascota == idMascota && c.Estado == EstadoCita.COMPLETADO)
                .OrderByDescending(c => c.FechaHora)
                .FirstOrDefault();
        }

        public static CitaViewModel? ProximaDe(List<CitaViewModel> citas, int idMascota) {
            return Proximas(citas).FirstOrDefault(c => c.IdMascota == idMascota);
        }

        // El cliente no cambia el nombre ni el tipo de una mascota con citas aprobadas o completadas:
        // protege el historial clínico. Lo aplica el front; el backend no distingue quién registra.
        public static bool NombreYTipoBloqueados(List<CitaViewModel> citas, int idMascota) {
            return citas.Any(c => c.IdMascota == idMascota
                               && (c.Estado == EstadoCita.APROBADO || c.Estado == EstadoCita.COMPLETADO));
        }

        // Edad en texto corto: "8 meses", "1 año", "3 años"
        public static string Edad(DateOnly? nacimiento) {
            if (nacimiento == null) {
                return "";
            }
            DateOnly hoy = DateOnly.FromDateTime(DateTime.Today);
            int meses = (hoy.Year - nacimiento.Value.Year) * 12 + hoy.Month - nacimiento.Value.Month;
            if (meses < 12) {
                return $"{Math.Max(meses, 0)} meses";
            }
            int anios = meses / 12;
            return anios == 1 ? "1 año" : $"{anios} años";
        }
    }
}
