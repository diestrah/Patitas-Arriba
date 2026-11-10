package patitasarriba.bl;

import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;

public interface CitaBL extends RegistroBL<Cita, Integer> {
    // Busca un detalle de cita por su propio id, sin traer la cita completa. Lo usa la capa REST
    // para resolver referencias a un DetalleCita ya existente (ej. AtencionDTO.detalleCita,
    // DetalleBoletaServicioDTO.detalleCita) sin recorrer todas las citas a fuerza bruta.
    DetalleCita findDetalleById(Integer id) throws BLException;
}
