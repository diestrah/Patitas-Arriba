package patitasarriba.bl;

import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.InsumoUtilizado;

public interface AtencionBL extends RegistroBL<Atencion, Integer> {
    // Busca un insumo utilizado por su propio id, sin traer la atencion completa. Lo usa la capa
    // REST para resolver DetalleBoletaArticuloDTO.insumoUtilizado sin tener que cargar la atencion
    // y recorrer su lista de insumos.
    InsumoUtilizado findInsumoById(Integer id) throws BLException;
}
