package patitasarriba.modelo.horario;

import patitasarriba.modelo.Registro;

import java.time.LocalTime;

public class Horario extends Registro {
    private int idHorario;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;

    public Horario() {
    }

    public Horario(int idHorario, boolean activo, DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        super(activo);
        setIdHorario(idHorario);
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
        setIdHorario(horario.getIdHorario());
        setDiaSemana(horario.getDiaSemana());
        setHoraInicio(horario.getHoraInicio());
        setHoraFin(horario.getHoraFin());
    }

    // Getters y Setters
    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        if (idHorario < 0) {
            throw new IllegalArgumentException("idHorario no puede ser negativo");
        }
        this.idHorario = idHorario;
    }

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
        // Que horaInicio sea anterior a horaFin lo valida la capa de negocio
        if (horaInicio == null) {
            throw new IllegalArgumentException("horaInicio no puede ser nula");
        }
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        if (horaFin == null) {
            throw new IllegalArgumentException("horaFin no puede ser nula");
        }
        this.horaFin = horaFin;
    }

    @Override
    public String toString() {
        return "Horario{" +
                "idHorario=" + idHorario +
                ", " + super.toString() +
                ", diaSemana=" + diaSemana +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}
