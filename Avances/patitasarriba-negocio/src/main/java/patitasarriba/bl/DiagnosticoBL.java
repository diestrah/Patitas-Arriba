package patitasarriba.bl;

import patitasarriba.modelo.atencion.Diagnostico;

public interface DiagnosticoBL extends RegistroBL<Diagnostico, Integer> {
    Diagnostico findByNombreEnfermedad(String nombreEnfermedad) throws BLException;
}
