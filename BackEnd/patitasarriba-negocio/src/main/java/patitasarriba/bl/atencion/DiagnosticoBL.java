package patitasarriba.bl.atencion;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.RegistroBL;
import patitasarriba.modelo.atencion.Diagnostico;

public interface DiagnosticoBL extends RegistroBL<Diagnostico, Integer> {
    Diagnostico findByNombreEnfermedad(String nombreEnfermedad) throws BLException;
}
