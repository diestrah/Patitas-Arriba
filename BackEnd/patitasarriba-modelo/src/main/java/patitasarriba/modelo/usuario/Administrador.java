package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;

import java.util.List;

public class Administrador extends Personal {

    public Administrador() {
    }

    public Administrador(int idPersona, boolean activo, Cuenta cuenta, String dni, String nombres,
                         String apellidoPaterno, String apellidoMaterno, String telefono, List<Horario> horarios) {
        super(idPersona, activo, cuenta, dni, nombres, apellidoPaterno, apellidoMaterno, telefono, horarios);
    }

    // Constructor de copia
    public Administrador(final Administrador administrador) {
        if (administrador == null) {
            throw new IllegalArgumentException("administrador no puede ser nulo");
        }
        super(administrador);
    }

    @Override
    public String toString() {
        return "Administrador{" +
                super.toString() +
                '}';
    }
}
