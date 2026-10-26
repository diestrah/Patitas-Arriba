package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Personal extends Persona {
    private List<Horario> horarios;

    public Personal() {
        this.horarios = new ArrayList<>();
    }

    public Personal(int idPersona, boolean activo, Cuenta cuenta, String dni, String nombres,
                    String apellidoPaterno, String apellidoMaterno, String telefono, List<Horario> horarios) {
        super(idPersona, activo, cuenta, dni, nombres, apellidoPaterno, apellidoMaterno, telefono);
        setHorarios(horarios);
    }

    // Constructor de copia
    public Personal(final Personal personal) {
        if (personal == null) {
            throw new IllegalArgumentException("personal no puede ser nulo");
        }
        super(personal);
        setHorarios(personal.getHorarios());
    }

    // Getters y Setters
    public List<Horario> getHorarios() {
        return Collections.unmodifiableList(horarios);
    }

    public void setHorarios(List<Horario> horarios) {
        if (horarios == null) {
            throw new IllegalArgumentException("horarios no puede ser nulo");
        }
        for (Horario horario : horarios) {
            if (horario == null) {
                throw new IllegalArgumentException("horarios no puede contener elementos nulos");
            }
        }
        this.horarios = new ArrayList<>(horarios);
    }

    public void agregarHorario(Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("horario no puede ser nulo");
        }
        horarios.add(horario);
    }

    public void eliminarHorario(Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("horario no puede ser nulo");
        }
        horarios.removeIf(h -> h.getIdHorario() == horario.getIdHorario());
    }

    @Override
    public String toString() {
        return super.toString() +
                ", horarios=" + horarios;
    }
}
