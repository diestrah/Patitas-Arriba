package model.usuario;

import model.horario.Horario;

import java.util.ArrayList;
import java.util.List;

public abstract class Personal extends Persona {
    private boolean estado;
    private List<Horario> horarios;

    public Personal(String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                    Cuenta cuenta, boolean estado, List<Horario> horarios) {
        super(nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta);
        this.estado = estado;
        this.horarios = horarios;
    }

    // Getters y Setters
    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public List<Horario> getHorarios() {
        return horarios;
    }

    public void setHorarios(List<Horario> horarios) {
        this.horarios = horarios;
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
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", estado=" + estado +
                ", horarios=" + horarios +
                '}';
    }
}
