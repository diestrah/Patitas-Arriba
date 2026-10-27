package patitasarriba.dao.impl.producto;

import conexion.DBManager;
import patitasarriba.dao.ArticuloDAO;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Categoria;
import patitasarriba.dao.transacciones.TransactionsManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAOImpl extends ProductoDAOImpl<Articulo> implements ArticuloDAO {

    @Override
    public List<Articulo> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_articulo()}";
            try (CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {
                List<Articulo> articulos = new ArrayList<>();
                while (rs.next()) {
                    articulos.add(mapear(rs, new Articulo()));
                }
                return articulos;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Articulo findById(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call buscar_articulo_por_id(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);

                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Articulo()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void insert(Articulo articulo) throws SQLException {
        if (articulo == null) {
            throw new IllegalArgumentException("El articulo no puede ser nulo");
        }

        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        Connection conn = TransactionsManager.getConnection();
        String sql = "{call insertar_articulo(?,?,?,?,?,?,?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, articulo);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            articulo.setId(cmd.getInt("p_id"));
            guardarCategorias(conn, articulo);
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    @Override
    public void update(Articulo articulo) throws SQLException {
        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        try {
            update(TransactionsManager.getConnection(), articulo);
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    // Variante para usar dentro de una transaccion ya abierta (ej. BoletaBLImpl.descontarStock):
    // usa la Connection recibida en vez de abrir una nueva, para no autobloquearse esperando
    // el candado que ya sostiene esa misma transaccion (ej. por una FK de detalle_boleta_art).
    @Override
    public void update(Connection conn, Articulo articulo) throws SQLException {
        if (articulo == null) {
            throw new IllegalArgumentException("El articulo no puede ser nulo");
        }

        String sql = "{call modificar_articulo(?,?,?,?,?,?,?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", articulo.getId());
            setParametrosComunes(cmd, articulo);

            cmd.execute();
        }
        guardarCategorias(conn, articulo);
    }

    // Los parametros que insertar_articulo y modificar_articulo comparten
    private void setParametrosComunes(CallableStatement cmd, Articulo articulo) throws SQLException {
        cmd.setString("p_nombre", articulo.getNombre());
        cmd.setBigDecimal("p_precio_base", BigDecimal.valueOf(articulo.getPrecioBase()));
        cmd.setString("p_descripcion", articulo.getDescripcion());
        cmd.setBoolean("p_activo", articulo.isActivo());
        cmd.setInt("p_stock_actual", articulo.getStockActual());
        cmd.setInt("p_stock_minimo", articulo.getStockMinimo());
    }

    private void guardarCategorias(Connection conn, Articulo articulo) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call eliminar_categorias_por_articulo(?)}")) {
            cmd.setInt("p_id_articulo", articulo.getId());
            cmd.execute();
        }
        try (CallableStatement cmd = conn.prepareCall("{call insertar_articulo_categoria(?,?)}")) {
            for (Categoria categoria : articulo.getCategorias()) {
                if (!categoria.isActivo()) continue;
                cmd.setInt("p_id_articulo", articulo.getId());
                cmd.setInt("p_id_categoria", categoria.getId());
                cmd.execute();
            }
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        Connection conn = TransactionsManager.getConnection();
        try {
            try (CallableStatement cmd = conn.prepareCall("{call eliminar_categorias_por_articulo(?)}")) {
                cmd.setInt("p_id_articulo", id);
                cmd.execute();
            }

            String sql = "{call eliminar_articulo(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);

                cmd.execute();
            }
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    @Override
    public List<Articulo> findByCategoriaId(int idCategoria) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_articulos_por_categoria(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id_categoria", idCategoria);
                try (ResultSet rs = cmd.executeQuery()) {
                    List<Articulo> articulos = new ArrayList<>();
                    while (rs.next()) {
                        articulos.add(mapear(rs, new Articulo()));
                    }
                    return articulos;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    protected Articulo mapear(ResultSet rs, Articulo articulo) throws SQLException {
        super.mapear(rs, articulo);
        articulo.setId(rs.getInt("id_articulo"));
        articulo.setStockActual(rs.getInt("stock_actual"));
        articulo.setStockMinimo(rs.getInt("stock_minimo"));

        // Relacion muchos a muchos: cargar las categorias activas.
        articulo.setCategorias(new CategoriaDAOImpl().findByArticuloId(articulo.getId()));

        return articulo;
    }
}
