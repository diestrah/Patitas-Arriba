package patitasarriba.bl.usuario;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.usuario.Veterinario;

// Acceso al sistema: registro de clientes con verificacion de correo, recuperacion de contrasena y
// alta de personal/clientes por un administrador (persona y cuenta en una sola transaccion).
// El login esta en CuentaBL.login.
public interface AccesoBL {
    // Auto-registro de un cliente: valida los datos y envia un correo con un enlace. Todavia NO crea la cuenta
    void solicitarRegistro(Cliente datos, String correo, String password) throws BLException;

    // Al abrir el enlace del correo: crea la cuenta y el cliente en una sola transaccion
    Cliente confirmarRegistro(String token) throws BLException;

    // Olvido de contrasena: si el correo pertenece a una cuenta activa envia un enlace. No dice si existe
    void solicitarRecuperacion(String correo) throws BLException;

    // Con el token del correo fija la contrasena nueva. El token es de un solo uso y vence
    void restablecerContrasena(String token, String nuevaPassword) throws BLException;

    // Alta hecha por un administrador: crea la persona y su cuenta (con una clave que nadie conoce) y envia el
    // correo para que la persona elija su contrasena
    void registrarCliente(Cliente cliente, String correo) throws BLException;

    void registrarVeterinario(Veterinario veterinario, String correo) throws BLException;

    void registrarAdministrador(Administrador administrador, String correo) throws BLException;
}
