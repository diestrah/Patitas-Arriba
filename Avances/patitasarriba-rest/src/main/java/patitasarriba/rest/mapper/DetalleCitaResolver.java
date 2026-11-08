package patitasarriba.rest.mapper;

import patitasarriba.bl.BLException;
import patitasarriba.bl.CitaBL;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.cita.CitaBLImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;

// No existe un DetalleCitaBL/DetalleCitaResource independiente (ver REST_SERVICIOS.md §5.13/§5.15):
// para resolver un DetalleCita YA EXISTENTE por su id (lo que necesitan AtencionMapper y
// DetalleBoletaServicioMapper), se recorren todas las citas buscando el detalle. Es el mismo tipo
// de costo que tenia ServicioBLImpl.validarSinCitas antes de optimizarlo (commit "Optimización de
// validarSinCitas"): si esto se vuelve un problema de rendimiento real, la solucion es agregar un
// finder puntual a CitaBL/CitaDAO, no resolverlo aqui con mas fuerza bruta.
final class DetalleCitaResolver {
    private static final CitaBL citaBL = new CitaBLImpl();

    private DetalleCitaResolver() {
    }

    static DetalleCita buscarPorId(int idDetalleCita) throws BLException {
        for (Cita cita : citaBL.findAll()) {
            for (DetalleCita detalle : cita.getDetalles()) {
                if (detalle.getId() == idDetalleCita) {
                    return detalle;
                }
            }
        }
        throw new ValidacionException("No existe el detalle de cita con id " + idDetalleCita);
    }
}
