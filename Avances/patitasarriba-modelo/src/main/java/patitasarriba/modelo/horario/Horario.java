package patitasarriba.modelo.horario;

import patitasarriba.modelo.Registro;

import java.time.LocalTime;

public class Horario extends Registro {
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Horario() {
    }

    public Horario(int id, boolean activo, DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        super(id, activo);
        setDiaSemana(diaSemana);
        setHoraInicio(horaInicio);
        setHoraFin(horaFin);
    }

    // Constructor de copia
    public Horario(final Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("horario no puede ser nulo");
        }
        super(horario);
        setDiaSemana(horario.getDiaSemana());
        setHoraInicio(horario.getHoraInicio());
        setHoraFin(horario.getHoraFin());
    }

    // Getters y Setters

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        if (diaSemana == null) {
            throw new IllegalArgumentException("diaSemana no puede ser nulo");
        }
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        if (horaInicio == null) {
            throw new IllegalArgumentException("horaInicio no puede ser nulo");
        }
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        if (horaFin == null) {
            throw new IllegalArgumentException("horaFin no puede ser nulo");
        }
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return "Horario{" +
                super.toString() +
                ", diaSemana=" + diaSemana +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}
