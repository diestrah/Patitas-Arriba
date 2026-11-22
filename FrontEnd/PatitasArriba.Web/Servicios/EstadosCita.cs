using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

// Aprobar, rechazar, cancelar o completar una cita. El backend no tiene un endpoint solo para el estado:
// se envía PUT /citas/{id} con la cita completa y el nuevo estado; CitaBLImpl valida la transición (RN-CI-08 a RN-CI-11).
public static class EstadosCita {
    // Al aprobar o rechazar se registra el administrador que decide (RN-CI-09).
    // Si el backend rechaza el cambio, la cita vuelve a como estaba y la excepción sigue su curso.
    public static async Task CambiarEstado(this ICitaService citaService, CitaViewModel cita, EstadoCita estado,
            int? idAdministrador = null, string? nombreAdministrador = null) {
        EstadoCita estadoAnterior = cita.Estado;
        int? idAnterior = cita.IdAdministrador;
        string? nombreAnterior = cita.NombreAdministrador;

        cita.Estado = estado;
        if ((estado == EstadoCita.APROBADO || estado == EstadoCita.RECHAZADO) && idAdministrador != null) {
            cita.IdAdministrador = idAdministrador;
            cita.NombreAdministrador = nombreAdministrador;
        }
        try {
            await citaService.Update(cita);
        } catch {
            cita.Estado = estadoAnterior;
            cita.IdAdministrador = idAnterior;
            cita.NombreAdministrador = nombreAnterior;
            throw;
        }
    }
}
