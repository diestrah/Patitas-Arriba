package patitasarriba.dao;

import patitasarriba.modelo.producto.Articulo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface ArticuloDAO extends DAO<Articulo, Integer> {
    // Articulos activos asignados a una categoria
    List<Articulo> findByCategoriaId(int idCategoria) throws SQLException;

    // Suma (cantidad > 0) o resta (cantidad < 0) stock de forma atomica dentro de la transaccion
    // de conn. Devuelve false si no hay stock suficiente o el articulo no existe (RN-AR-04).
    boolean ajustarStock(Connection conn, int idArticulo, int cantidad) throws SQLException;
    // Incluye registros dados de baja: para cargar relaciones del historial
    Articulo findByIdHistorico(Integer id) throws SQLException;
}
