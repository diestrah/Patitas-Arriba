package patitasarriba.bl;

import patitasarriba.modelo.atencion.Atencion;

import java.util.List;

public interface AtencionBL extends RegistroBL<Atencion, Integer> {
    // RF16: historial clinico de una mascota, en orden cronologico
    List<Atencion> findByMascota(Integer idMascota) throws BLException;
}
