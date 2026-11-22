package patitasarriba.bl.usuario;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.RegistroBL;
import patitasarriba.modelo.usuario.Administrador;

public interface AdministradorBL extends RegistroBL<Administrador, Integer> {
    Administrador findByDni(String dni) throws BLException;
}
