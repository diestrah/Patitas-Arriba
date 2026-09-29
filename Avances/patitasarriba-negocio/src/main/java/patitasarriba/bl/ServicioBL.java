package patitasarriba.bl;

import patitasarriba.modelo.producto.Servicio;

public interface ServicioBL extends RegistroBL<Servicio, Integer> {
    Servicio findByName(String name) throws BLException;
}
