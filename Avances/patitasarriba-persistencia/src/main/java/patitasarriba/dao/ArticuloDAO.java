package patitasarriba.dao;

import patitasarriba.modelo.producto.Articulo;

import java.sql.Connection;
import java.sql.SQLException;

public interface ArticuloDAO extends DAO<Articulo, Integer> {
    // Variante para usar dentro de una transaccion ya abierta (ej. BoletaBLImpl.descontarStock),
    // evita abrir una segunda conexion que se autobloquee esperando el candado de la primera.
    void update(Connection conn, Articulo articulo) throws SQLException;
}
