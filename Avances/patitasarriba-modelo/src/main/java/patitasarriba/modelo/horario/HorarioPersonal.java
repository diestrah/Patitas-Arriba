package patitasarriba.modelo.horario;

import patitasarriba.modelo.Registro;
import patitasarriba.modelo.usuario.Personal;

public class HorarioPersonal extends Registro {
    private Horario horario;

    public HorarioPersonal() {
    }

    public HorarioPersonal(int id, boolean activo, Horario horario) {
        super(id, activo);
        setHorario(horario);
    }

    // Constructor de copia
    public HorarioPersonal(final HorarioPersonal horarioPersonal) {
        if (horarioPersonal == null) {
            throw new IllegalArgumentException("horarioPersonal no puede ser nulo");
        }
        super(horarioPersonal);
        setHorario(horarioPersonal.getHorario());
    }

    // Getters y Setters
    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        if (horario == null) {
            throw new IllegalArgumentException("horario no puede ser nulo");
        }
        this.horario = horario;
    }

    @Override
    public String toString() {
        return "HorarioPersonal{" +
                super.toString() +
                ", horario=" + horario +
                '}';
    }
}
