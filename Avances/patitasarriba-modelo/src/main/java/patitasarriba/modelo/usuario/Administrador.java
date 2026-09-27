package patitasarriba.modelo.usuario;

import patitasarriba.modelo.horario.Horario;

import java.util.List;

public class Administrador extends Personal {


    public Administrador(int idPersona, String nombres, String apellidoPaterno, String apellidoMaterno,
                         String telefono, String dni,
                         Cuenta cuenta, boolean estado, List<Horario> horarios) {
        super(idPersona, nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, estado, horarios);
    }

    // Constructor de copia
    public Administrador(Administrador administrador) {
        super(administrador);
    }

    @Override
    public String toString() {
        return "Administrador{" +
                "idPersona='" + getIdPersona() + '\'' +
                "nombres='" + getNombres() + '\'' +
                ", apellidoPaterno='" + getApellidoPaterno() + '\'' +
                ", estado=" + isEstado() +
                '}';
    }
}
