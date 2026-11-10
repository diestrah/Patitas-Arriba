package patitasarriba.bl;

import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;

import java.time.LocalDate;
import java.util.List;

public interface CitaBL extends RegistroBL<Cita, Integer> {
    // Busca un detalle de cita por su propio id, sin traer la cita completa. Lo usa la capa REST
    // para resolver referencias a un DetalleCita ya existente (ej. AtencionDTO.detalleCita,
    // DetalleBoletaServicioDTO.detalleCita) sin recorrer todas las citas a fuerza bruta.
    DetalleCita findDetalleById(Integer id) throws BLException;

    // RF10: citas de las mascotas de un cliente, ordenadas por fecha
    List<Cita> findByCliente(Integer idCliente) throws BLException;

    // RF11, RF13: citas de un dia, ordenadas por hora. Con idVeterinario (no nulo) solo las que
    // incluyen algun servicio de ese veterinario (su agenda diaria)
    List<Cita> findByFecha(LocalDate fecha, Integer idVeterinario) throws BLException;
}
