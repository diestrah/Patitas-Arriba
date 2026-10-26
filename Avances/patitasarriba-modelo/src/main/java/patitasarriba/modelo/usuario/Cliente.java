package patitasarriba.modelo.usuario;

public class Cliente extends Persona {

    public Cliente() {
    }

    public Cliente(int idPersona, boolean activo, Cuenta cuenta, String dni, String nombres,
                   String apellidoPaterno, String apellidoMaterno, String telefono) {
        super(idPersona, activo, cuenta, dni, nombres, apellidoPaterno, apellidoMaterno, telefono);
    }

    // Constructor de copia
    public Cliente(final Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("cliente no puede ser nulo");
        }
        super(cliente);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                super.toString() +
                '}';
    }
}
