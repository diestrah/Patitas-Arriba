package model.usuario;

import model.horario.Horario;
import model.mascota.TipoMascota;

import java.util.List;

public class Estilista extends PersonalAtencion {
    private String certificacionGrooming;
    private TipoMascota especialidadAnimal;


    public Estilista(String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                     Cuenta cuenta, boolean estado, List<Horario> horarios,
                     String certificacionGrooming, TipoMascota especialidadAnimal) {
        super(nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, estado, horarios);
        this.certificacionGrooming = certificacionGrooming;
        this.especialidadAnimal = especialidadAnimal;
    }

    // Getters y Setters
    public String getCertificacionGrooming() {
        return certificacionGrooming;
    }

    public void setCertificacionGrooming(String certificacionGrooming) {
        this.certificacionGrooming = certificacionGrooming;
    }

    public TipoMascota getEspecialidadAnimal() {
        return especialidadAnimal;
    }

    public void setEspecialidadAnimal(TipoMascota especialidadAnimal) {
        this.especialidadAnimal = especialidadAnimal;
    }

    @Override
    public String toString() {
        return "Estilista{" +
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", certificacionGrooming='" + certificacionGrooming + '\'' +
                ", especialidadAnimal=" + especialidadAnimal +
                '}';
    }
}
