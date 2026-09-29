package patitasarriba.dao.impl.producto;

import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.producto.Producto;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class ProductoDAOImpl<T extends Producto> extends RegistroDAOImpl<T> {
    @Override
    protected T mapear(ResultSet rs, T producto) throws SQLException {
        super.mapear(rs, producto);
        producto.setNombre(rs.getString("nombre"));
        producto.setPrecioBase(rs.getDouble("precio_base"));
        producto.setDescripcion(rs.getString("descripcion"));
        return producto;
    }
}
