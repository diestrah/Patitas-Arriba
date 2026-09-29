package patitasarriba.bl;

import patitasarriba.modelo.usuario.Administrador;

public interface AdministradorBL extends RegistroBL<Administrador, Integer> {
    Administrador findByDni(String dni) throws BLException;
}
