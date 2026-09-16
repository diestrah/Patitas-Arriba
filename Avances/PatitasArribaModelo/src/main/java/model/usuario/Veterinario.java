package model.usuario;

import model.horario.Horario;

import java.util.List;

public class Veterinario extends PersonalAtencion {
    private String numeroColegiatura;


    public Veterinario(String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                       Cuenta cuenta, boolean estado, List<Horario> horarios,
                       String numeroColegiatura) {
        super(nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, estado, horarios);
        this.numeroColegiatura = numeroColegiatura;
    }

    // Getters y Setters
    public String getNumeroColegiatura() {
        return numeroColegiatura;
    }

    public void setNumeroColegiatura(String numeroColegiatura) {
        this.numeroColegiatura = numeroColegiatura;
    }

    @Override
    public String toString() {
        return "Veterinario{" +
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", numeroColegiatura='" + numeroColegiatura + '\'' +
                '}';
    }
}
