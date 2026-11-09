package patitasarriba.bl;

import patitasarriba.modelo.cita.Cita;

import java.time.LocalDate;
import java.util.List;

public interface CitaBL extends RegistroBL<Cita, Integer> {
    // RF10: citas de las mascotas de un cliente, ordenadas por fecha
    List<Cita> findByCliente(Integer idCliente) throws BLException;

    // RF10, RF11, RF13: citas de un dia, ordenadas por hora. Con idVeterinario (no nulo) solo las que
    // incluyen algun servicio de ese veterinario: su agenda del dia y sus horas ocupadas
    List<Cita> findByFecha(LocalDate fecha, Integer idVeterinario) throws BLException;
}
