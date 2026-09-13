package model.usuario;

import model.horario.Horario;

import java.util.List;

public class Veterinario extends Personal {
    private String numeroColegiatura;
    private EspecialidadMedica especialidadMedica;


    public Veterinario(String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                       Cuenta cuenta, boolean estado, List<Horario> horarios,
                       String numeroColegiatura, EspecialidadMedica especialidadMedica) {
        super(nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, estado, horarios);
        this.numeroColegiatura = numeroColegiatura;
        this.especialidadMedica = especialidadMedica;
    }

    // Getters y Setters
    public String getNumeroColegiatura() {
        return numeroColegiatura;
    }

    public void setNumeroColegiatura(String numeroColegiatura) {
        this.numeroColegiatura = numeroColegiatura;
    }

    public EspecialidadMedica getEspecialidadMedica() {
        return especialidadMedica;
    }

    public void setEspecialidadMedica(EspecialidadMedica especialidadMedica) {
        this.especialidadMedica = especialidadMedica;
    }

    @Override
    public String toString() {
        return "Veterinario{" +
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", numeroColegiatura='" + numeroColegiatura + '\'' +
                ", especialidadMedica=" + especialidadMedica +
                '}';
    }
}
