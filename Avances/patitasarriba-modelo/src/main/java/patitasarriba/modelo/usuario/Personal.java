package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;
import patitasarriba.modelo.horario.HorarioPersonal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Personal extends Persona {
    private List<HorarioPersonal> horariosPersonal;

    public Personal() {
        this.horariosPersonal = new ArrayList<>();
    }

    public Personal(int id, boolean activo, String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni, Cuenta cuenta, List<HorarioPersonal> horariosPersonales) {
        super(id, activo, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta);
        setHorariosPersonal(horariosPersonales);
    }

    // Constructor de copia
    public Personal(final Personal personal) {
        if (personal == null) {
            throw new IllegalArgumentException("personal no puede ser nulo");
        }
        super(personal);
        setHorariosPersonal(new ArrayList<>(personal.getHorariosPersonal()));
    }

    // Getters y Setters
    public List<HorarioPersonal> getHorariosPersonal() {
        return Collections.unmodifiableList(horariosPersonal);
    }

    public void setHorariosPersonal(List<HorarioPersonal> horariosPersonales) {
        if (horariosPersonales == null) {
            throw new IllegalArgumentException("horariosPersonales no puede ser nulo");
        }
        this.horariosPersonal = new ArrayList<>(horariosPersonales);
    }

    public void agregarHorarioPersonal(int id, boolean activo, Horario horario) {
        horariosPersonal.add(new HorarioPersonal(id, activo, horario));
    }

    public void eliminarHorarioPersonal(HorarioPersonal horarioPersonal) {
        horariosPersonal.remove(horarioPersonal);
    }

    public String toString() {
        return "Personal{" +
                super.toString() +
                ", horariosPersonales=" + horariosPersonal +
                '}';
    }
}
