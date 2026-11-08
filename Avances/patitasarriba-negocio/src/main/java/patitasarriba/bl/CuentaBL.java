package patitasarriba.bl;

import patitasarriba.modelo.usuario.Cuenta;

public interface CuentaBL extends RegistroBL<Cuenta, Integer> {
    Cuenta findByNombreUsuario(String nombreUsuario) throws BLException;
    Cuenta login(String nombreUsuario, String password) throws BLException;
}
