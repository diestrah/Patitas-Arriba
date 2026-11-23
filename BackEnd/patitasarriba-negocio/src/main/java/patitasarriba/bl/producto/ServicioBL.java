package patitasarriba.bl.producto;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.RegistroBL;
import patitasarriba.modelo.producto.Servicio;

public interface ServicioBL extends RegistroBL<Servicio, Integer> {
    Servicio findByName(String name) throws BLException;
}
