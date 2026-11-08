package patitasarriba.rest.mapper;

import patitasarriba.bl.AtencionBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.impl.atencion.AtencionBLImpl;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.InsumoUtilizado;

// No existe un InsumoUtilizadoBL propio: para resolver un InsumoUtilizado YA EXISTENTE (el que se
// va a cobrar en DetalleBoletaArticuloDTO.insumoUtilizado), se busca su Atencion y se recorren
// sus insumos. Mismo tipo de limitacion que DetalleCitaResolver.
final class InsumoUtilizadoResolver {
    private static final AtencionBL atencionBL = new AtencionBLImpl();

    private InsumoUtilizadoResolver() {
    }

    static InsumoUtilizado buscarPorId(int idAtencion, int idInsumoUtilizado) throws BLException {
        Atencion atencion;
        try {
            atencion = atencionBL.findById(idAtencion);
        } catch (NoEncontradoException e) {
            throw new ValidacionException("No existe la atencion con id " + idAtencion);
        }
        for (InsumoUtilizado insumo : atencion.getInsumosUtilizados()) {
            if (insumo.getId() == idInsumoUtilizado) {
                return insumo;
            }
        }
        throw new ValidacionException("La atencion " + idAtencion + " no tiene el insumo con id " + idInsumoUtilizado);
    }
}
