package patitasarriba.bl;

import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.modelo.usuario.Persona;

public interface CuentaBL extends RegistroBL<Cuenta, Integer> {
    Cuenta findByNombreUsuario(String nombreUsuario) throws BLException;
    // RF01: valida las credenciales y devuelve la persona duena de la cuenta.
    // Su tipo (Cliente, Veterinario o Administrador) es el rol
    Persona login(String nombreUsuario, String password) throws BLException;
}
