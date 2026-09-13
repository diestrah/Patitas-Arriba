package model.usuario;

import model.horario.Horario;

import java.util.List;

public class Administrador extends Personal {


    public Administrador(String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                         Cuenta cuenta, boolean estado, List<Horario> horarios) {
        super(nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, estado, horarios);
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", estado=" + isEstado() +
                '}';
    }
}
