package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Personal extends Persona {
    private boolean estado;
    private List<Horario> horarios;

    public Personal(int idPersona, String nombres, String apellidoPaterno, String apellidoMaterno,
                    String telefono, String dni,
                    Cuenta cuenta, boolean estado, List<Horario> horarios) {
        super(idPersona, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta);
        setEstado(estado);
        setHorarios(horarios);
    }

    // Constructor de copia
    protected Personal(Personal personal) {
        super(personal);
        setEstado(personal.isEstado());
        setHorarios(personal.getHorarios());
    }

    // Getters y Setters
    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

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
                "idPersona='" + getIdPersona() + '\'' +
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", estado=" + estado + '\'' +
                ", horarios='" + horarios + '\'' +
                '}';
    }
}
