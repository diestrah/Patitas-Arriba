package patitasarriba.daoimpl.producto;

import patitasarriba.dao.producto.CategoriaDAO;
import patitasarriba.daoimpl.BaseDAOImpl;
import patitasarriba.modelo.producto.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl extends BaseDAOImpl<Categoria> implements CategoriaDAO {

    // Los parametros que insertar_categoria_articulo y modificar_categoria_articulo comparten
    private void setParametrosComunes(CallableStatement cmd, Categoria categoria) throws SQLException {
        cmd.setString("p_nombre", categoria.getNombre());
        cmd.setString("p_descripcion", categoria.getDescripcion());
        cmd.setBoolean("p_activo", categoria.isActivo());
    }

    @Override
    public List<Categoria> findByArticuloId(int idArticulo) throws SQLException {
        return ejecutarComando(conn -> {
                String sql = "{call listar_categorias_por_articulo(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {

                    cmd.setInt("p_id_articulo", idArticulo);
                    try (ResultSet rs = cmd.executeQuery()) {
                        List<Categoria> categorias = new ArrayList<>();

                        while (rs.next()) {
                            categorias.add(mapear(rs));
                        }
                        return categorias;
                    }
                }
            });
    }

    @Override
    public Categoria findByNombre(String nombre) throws SQLException {
        return ejecutarComando(conn -> {
                String sql = "{call buscar_categoria_por_nombre(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_nombre", nombre);
                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    protected Categoria mapear(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria();
        categoria.setActivo(rs.getBoolean("activo"));
        categoria.setId(rs.getInt("id_categoria"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        return categoria;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        String sql = "{call listar_categoria()}";
        CallableStatement cmd = conn.prepareCall(sql);
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_categoria_por_id(?)}";
        CallableStatement cmd = conn.prepareCall(sql);

        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try {
                cmd.close();
            } catch (SQLException cierre) {
                ex.addSuppressed(cierre);
            }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Categoria modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("La categoria no puede ser nula");
        }

        String sql = "{call insertar_categoria(?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            setParametrosComunes(cmd, modelo);
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try {
                cmd.close();
            } catch (SQLException cierre) {
                ex.addSuppressed(cierre);
            }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Categoria modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("La categoria no puede ser nula");
        }

        String sql = "{call modificar_categoria(?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id", modelo.getId());
            setParametrosComunes(cmd, modelo);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try {
                cmd.close();
            } catch (SQLException cierre) {
                ex.addSuppressed(cierre);
            }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_categoria(?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try {
                cmd.close();
            } catch (SQLException cierre) {
                ex.addSuppressed(cierre);
            }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Categoria modelo) throws SQLException {
        modelo.setId(cmd.getInt("p_id"));
    }
}
