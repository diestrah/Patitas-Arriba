package patitasarriba.dao.impl.receta;

import patitasarriba.modelo.receta.DetalleReceta;

import java.sql.SQLException;
import java.util.List;

public interface DetalleRecetaDAO {
    void insertarDetalles(int idReceta, List<DetalleReceta> detallesReceta) throws SQLException;
    void eliminarDetalles(int idReceta) throws SQLException;
    List<DetalleReceta> buscarPorIdReceta(int idReceta) throws SQLException;
}
