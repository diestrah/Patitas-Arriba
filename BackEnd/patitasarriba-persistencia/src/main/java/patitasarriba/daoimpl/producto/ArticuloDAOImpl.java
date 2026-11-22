package patitasarriba.daoimpl.producto;

import patitasarriba.dao.producto.ArticuloDAO;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Categoria;
import patitasarriba.dao.transacciones.TransactionsManager;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ArticuloDAOImpl extends ProductoDAOImpl<Articulo> implements ArticuloDAO {

    @Override
    public void insert(Articulo articulo) throws SQLException {
        if (articulo == null) {
            throw new IllegalArgumentException("El articulo no puede ser nulo");
        }

        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        Connection conn = TransactionsManager.getConnection();
        try {
            ejecutarComandoInsertar(conn, articulo);
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

        ejecutarComandoActualizar(conn, articulo);
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

            ejecutarComandoEliminar(conn, id);
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    @Override
    public List<Articulo> findByCategoriaId(int idCategoria) throws SQLException {
        return ejecutarComando(conn -> {
                String sql = "{call listar_articulos_por_categoria(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_categoria", idCategoria);
                    try (ResultSet rs = cmd.executeQuery()) {
                        List<Articulo> articulos = new ArrayList<>();
                        while (rs.next()) {
                            articulos.add(mapear(rs));
                        }
                        return articulos;
                    }
                }
            });
    }

    @Override
    protected Articulo mapear(ResultSet rs) throws SQLException {
        Articulo articulo = new Articulo();
        mapearProducto(rs, articulo);
        articulo.setId(rs.getInt("id_articulo"));
        articulo.setStockActual(rs.getInt("stock_actual"));
        articulo.setStockMinimo(rs.getInt("stock_minimo"));

        // Relacion muchos a muchos: cargar las categorias activas.
        articulo.setCategorias(new CategoriaDAOImpl().findByArticuloId(articulo.getId()));

        return articulo;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call listar_articulo()}");
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call buscar_articulo_por_id(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Articulo modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call insertar_articulo(?,?,?,?,?,?,?)}");
        try {
            setParametrosComunes(cmd, modelo);
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Articulo modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call modificar_articulo(?,?,?,?,?,?,?)}");
        try {
            cmd.setInt("p_id", modelo.getId());
            setParametrosComunes(cmd, modelo);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call eliminar_articulo(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Articulo modelo) throws SQLException {
        int id = cmd.getInt("p_id");
        if (cmd.wasNull() || id <= 0) throw new SQLException("No se obtuvo un ID válido para Articulo.");
        modelo.setId(id);
    }
}
