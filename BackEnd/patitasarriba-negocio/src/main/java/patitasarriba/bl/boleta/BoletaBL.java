package patitasarriba.bl.boleta;

import patitasarriba.bl.RegistroBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.modelo.boleta.Boleta;

public interface BoletaBL extends RegistroBL<Boleta, Integer> {
    // Lo que el cliente todavia no paga (RN-BO-07, RN-BO-08), como una boleta sin guardar
    Boleta pendientesPorCliente(Integer idCliente) throws BLException;
}
