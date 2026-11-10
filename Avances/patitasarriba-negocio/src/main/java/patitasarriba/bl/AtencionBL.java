package patitasarriba.bl;

import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.InsumoUtilizado;

import java.util.List;

public interface AtencionBL extends RegistroBL<Atencion, Integer> {
    // Busca un insumo utilizado por su propio id, sin traer la atencion completa. Lo usa la capa
    // REST para resolver DetalleBoletaArticuloDTO.insumoUtilizado sin tener que cargar la atencion
    // y recorrer su lista de insumos.
    InsumoUtilizado findInsumoById(Integer id) throws BLException;

    // RF16: historial clinico de una mascota, en orden cronologico
    List<Atencion> findByMascota(Integer idMascota) throws BLException;
}
