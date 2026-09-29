package patitasarriba.bl;

import patitasarriba.modelo.usuario.Cliente;

public interface ClienteBL extends RegistroBL<Cliente, Integer> {
    Cliente findByDni(String dni) throws BLException;
}
