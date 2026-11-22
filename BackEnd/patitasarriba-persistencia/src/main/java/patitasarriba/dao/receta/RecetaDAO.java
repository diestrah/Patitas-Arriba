package patitasarriba.dao.receta;

import patitasarriba.dao.DAO;
import patitasarriba.modelo.receta.Receta;

import java.sql.SQLException;
import java.util.List;

public interface RecetaDAO extends DAO<Receta, Integer> {
    // Recetas activas de una atencion
    List<Receta> listarPorAtencion(int idAtencion) throws SQLException;
}
