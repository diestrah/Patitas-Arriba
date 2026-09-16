package model.usuario;

import model.horario.Horario;

import java.util.List;

public abstract class PersonalAtencion extends Personal {

    public PersonalAtencion(String nombres, String apellidoPaterno, String apellidoMaterno, String telefono, String dni,
                            Cuenta cuenta, boolean estado, List<Horario> horarios) {
        super(nombres, apellidoPaterno, apellidoMaterno, telefono, dni, cuenta, estado, horarios);
    }
}
