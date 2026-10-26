package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;

import java.util.List;

public class Veterinario extends Personal {
    private String numeroColegiatura;

    public Veterinario() {
    }

    public Veterinario(int idPersona, boolean activo, Cuenta cuenta, String dni, String nombres,
                       String apellidoPaterno, String apellidoMaterno, String telefono, List<Horario> horarios,
                       String numeroColegiatura) {
        super(idPersona, activo, cuenta, dni, nombres, apellidoPaterno, apellidoMaterno, telefono, horarios);
        setNumeroColegiatura(numeroColegiatura);
    }

    // Constructor de copia
    public Veterinario(final Veterinario veterinario) {
        if (veterinario == null) {
            throw new IllegalArgumentException("veterinario no puede ser nulo");
        }
        super(veterinario);
        setNumeroColegiatura(veterinario.getNumeroColegiatura());
    }

    // Getters y Setters
    public String getNumeroColegiatura() {
        return numeroColegiatura;
    }

    public void setNumeroColegiatura(String numeroColegiatura) {
        if (numeroColegiatura == null || numeroColegiatura.isBlank()) {
            throw new IllegalArgumentException("numeroColegiatura no puede ser nulo o vacío");
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
