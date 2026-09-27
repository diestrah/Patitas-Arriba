package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Personal extends Persona {
    private List<Horario> horarios;

    public Personal() {horarios = new ArrayList<>();}

    public Personal(int id, boolean activo, String nombres, String apellidoPaterno, String apellidoMaterno,
                    String telefono, String dni,
                    Cuenta cuenta,List<Horario> horarios) {
        super(id, activo, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta);
        setHorarios(horarios);
    }

    // Constructor de copia
    public Personal(final Personal personal) {
        if (personal == null) {
            throw new IllegalArgumentException("Personal no puede ser nula");
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
        this.horarios = new ArrayList<>(horarios);
    }

    public void agregarHorario(Horario horario) {
        this.horarios.add(horario);
    }

    public void eliminarHorario(Horario horario) {
        this.horarios.remove(horario);
    }

    @Override
    public String toString() {
        return "Personal{" +
                super.toString() + '\'' +
                ", horarios='" + horarios + '\'' +
                '}';
    }
}
