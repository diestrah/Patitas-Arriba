package patitasarriba.bl.usuario;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.RegistroBL;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.modelo.usuario.Persona;

public interface CuentaBL extends RegistroBL<Cuenta, Integer> {
    Cuenta findByNombreUsuario(String nombreUsuario) throws BLException;
    Cuenta findByCorreo(String correo) throws BLException;
    // RF01: se ingresa con el correo y la contrasena. Valida las credenciales y devuelve la
    // persona duena de la cuenta. Su tipo (Cliente, Veterinario o Administrador) es el rol
    Persona login(String correo, String password) throws BLException;
}
