package patitasarriba.dao;

import patitasarriba.modelo.producto.Categoria;

import java.sql.SQLException;
import java.util.List;

public interface CategoriaDAO extends DAO<Categoria, Integer> {
    List<Categoria> findByArticuloId(int idArticulo) throws SQLException;
}
