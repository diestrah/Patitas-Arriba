package patitasarriba.bl;

// Correo o contrasena incorrectos (o cuenta/persona inactiva) al iniciar sesion. La capa REST la
// traduce a 401; una BLException comun en el login es un error tecnico (500).
public class CredencialesInvalidasException extends BLException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
