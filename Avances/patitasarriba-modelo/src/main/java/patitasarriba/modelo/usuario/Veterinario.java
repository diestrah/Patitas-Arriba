package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;
import patitasarriba.modelo.horario.HorarioPersonal;

import java.util.List;

public class Veterinario extends Personal {
    private String numeroColegiatura;

    public Veterinario() {
    }

    public Veterinario(int id, boolean activo, String nombres, String apellidoPaterno, String apellidoMaterno,
                       String telefono, String dni,
                       Cuenta cuenta, List<HorarioPersonal> horariosPersonal,
                       String numeroColegiatura) {
        super(id, activo, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, horariosPersonal);
        setNumeroColegiatura(numeroColegiatura);
    }

    // Constructor de copia
    public Veterinario(final Veterinario veterinario) {
        if (veterinario == null) {
            throw new IllegalArgumentException("Veterinario no puede ser nulo");
        }
        super(veterinario);
        setNumeroColegiatura(veterinario.getNumeroColegiatura());
    }

    // Getters y Setters
    public String getNumeroColegiatura() {
        return numeroColegiatura;
    }

    public void setNumeroColegiatura(String numeroColegiatura) {
        if (numeroColegiatura == null || numeroColegiatura.isEmpty()) {
            throw new IllegalArgumentException("numero de colegiatura no puede ser nulo");
        }
        this.numeroColegiatura = numeroColegiatura;
    }

    @Override
    public String toString() {
        return "Veterinario{" +
                super.toString() +
                ", numeroColegiatura='" + numeroColegiatura + '\'' +
                '}';
    }
}
