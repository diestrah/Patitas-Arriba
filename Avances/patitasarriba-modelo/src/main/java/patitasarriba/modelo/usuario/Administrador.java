package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;
import patitasarriba.modelo.horario.HorarioPersonal;

import java.util.List;

public class Administrador extends Personal {

    public Administrador() {
    }

    public Administrador(int id, boolean activo, String nombres, String apellidoPaterno, String apellidoMaterno,
                         String telefono, String dni,
                         Cuenta cuenta, List<HorarioPersonal> horariosPersonal) {
        super(id, activo, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, horariosPersonal);
    }

    // Constructor de copia
    public Administrador(final Administrador administrador) {
        if(administrador == null){
            throw new IllegalArgumentException("Administrador no puede ser nulo");
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
