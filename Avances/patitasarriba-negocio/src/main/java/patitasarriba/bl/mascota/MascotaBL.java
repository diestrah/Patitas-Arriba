package patitasarriba.bl.mascota;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.RegistroBL;
import patitasarriba.modelo.mascota.Mascota;

import java.util.List;

public interface MascotaBL extends RegistroBL<Mascota, Integer> {
    // RF03: mascotas activas de un cliente
    List<Mascota> findByCliente(Integer idCliente) throws BLException;
}
