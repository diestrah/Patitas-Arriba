package patitasarriba.dao.impl.producto;

import conexion.DBManager;
import patitasarriba.dao.ArticuloDAO;
import patitasarriba.modelo.producto.Articulo;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAOImpl extends ProductoDAOImpl<Articulo> implements ArticuloDAO {

    @Override
    public List<Articulo> findAll() throws SQLException {
        String sql = "{call listar_articulo()}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {
            List<Articulo> articulos = new ArrayList<>();
            while (rs.next()) {
                articulos.add(mapear(rs, new Articulo()));
            }
            return articulos;
        }
    }

    @Override
    public Articulo findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_articulo_por_id(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Articulo()) : null;
            }
        }
    }

    @Override
    public void insert(Articulo articulo) throws SQLException {
        if (articulo == null) {
            throw new IllegalArgumentException("El articulo no puede ser nulo");
        }

        String sql = "{call insertar_articulo(?,?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, articulo);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            articulo.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(Articulo articulo) throws SQLException {
        if (articulo == null) {
            throw new IllegalArgumentException("El articulo no puede ser nulo");
        }

        String sql = "{call modificar_articulo(?,?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", articulo.getId());
            setParametrosComunes(cmd, articulo);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo modificar el articulo");
            }
        }
    }

    // Los parametros que insertar_articulo y modificar_articulo comparten
    private void setParametrosComunes(CallableStatement cmd, Articulo articulo) throws SQLException {
        cmd.setString("p_nombre", articulo.getNombre());
        cmd.setBigDecimal("p_precio_base", BigDecimal.valueOf(articulo.getPrecioBase()));
        cmd.setString("p_descripcion", articulo.getDescripcion());
        cmd.setBoolean("p_activo", articulo.isActivo());
        cmd.setInt("p_stock_actual", articulo.getStockActual());
        cmd.setInt("p_stock_minimo", articulo.getStockMinimo());
        cmd.setString("p_marca", articulo.getMarca());
        cmd.setInt("p_id_categoria_articulo", articulo.getCategoria().getId());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_articulo(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar el articulo");
            }
        }
    }

    @Override
    protected Articulo mapear(ResultSet rs, Articulo articulo) throws SQLException {
        super.mapear(rs, articulo);
        articulo.setId(rs.getInt("id_articulo"));
        articulo.setStockActual(rs.getInt("stock_actual"));
        articulo.setStockMinimo(rs.getInt("stock_minimo"));
        articulo.setMarca(rs.getString("marca"));

        // FK obligatoria: se resuelve el objeto completo
        int idCategoria = rs.getInt("id_categoria_articulo");
        articulo.setCategoria(new CategoriaArticuloDAOImpl().findById(idCategoria));

        return articulo;
    }
}
