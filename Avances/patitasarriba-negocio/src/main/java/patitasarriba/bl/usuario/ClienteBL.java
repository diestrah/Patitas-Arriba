package patitasarriba.bl.usuario;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.RegistroBL;
import patitasarriba.modelo.usuario.Cliente;

public interface ClienteBL extends RegistroBL<Cliente, Integer> {
    Cliente findByDni(String dni) throws BLException;
}
