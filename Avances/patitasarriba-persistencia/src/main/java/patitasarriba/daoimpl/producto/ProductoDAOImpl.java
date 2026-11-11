package patitasarriba.daoimpl.producto;

import patitasarriba.daoimpl.BaseDAOImpl;
import patitasarriba.modelo.producto.Producto;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ProductoDAOImpl<T extends Producto> extends BaseDAOImpl<T> {

    protected void mapearProducto(ResultSet rs, T producto) throws SQLException {
        producto.setActivo(rs.getBoolean("activo"));
        producto.setNombre(rs.getString("nombre"));
        producto.setPrecioBase(rs.getDouble("precio_base"));
        producto.setDescripcion(rs.getString("descripcion"));
    }
}
