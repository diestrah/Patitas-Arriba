package patitasarriba.modelo.horario;

import java.time.LocalTime;

public class Horario {
    private int idHorario;
    private DiaSemana diaSemana;
    private LocalTime horaInicio;
    private LocalTime horaFin;


    public Horario(int idHorario, DiaSemana diaSemana, LocalTime horaInicio, LocalTime horaFin) {
        setIdHorario(idHorario);
        setDiaSemana(diaSemana);
        setHoraInicio(horaInicio);
        setHoraFin(horaFin);
    }

    // Constructor de copia
    public Horario(Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("horario no puede ser nulo");
        }
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
                "idHorario=" + idHorario +
                ", diaSemana=" + diaSemana +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                '}';
    }
}
