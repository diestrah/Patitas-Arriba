package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;

import java.util.List;

public class Veterinario extends Personal {
    private String numeroColegiatura;


    public Veterinario(int idPersona, String nombres, String apellidoPaterno, String apellidoMaterno,
                       String telefono, String dni,
                       Cuenta cuenta, boolean estado, List<Horario> horarios,
                       String numeroColegiatura) {
        super(idPersona, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, estado, horarios);
        setNumeroColegiatura(numeroColegiatura);
    }

    // Constructor de copia
    public Veterinario(Veterinario veterinario) {
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
                "IdPersona='" + getIdPersona() + '\'' +
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", numeroColegiatura='" + numeroColegiatura + '\'' +
                '}';
    }
}
