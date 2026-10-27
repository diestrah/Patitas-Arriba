package patitasarriba.dao.impl.producto;

import patitasarriba.dao.transacciones.TransactionsManager;

import conexion.DBManager;
import patitasarriba.dao.CategoriaDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.producto.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAOImpl extends RegistroDAOImpl<Categoria> implements CategoriaDAO {

    @Override
    public List<Categoria> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_categoria()}";
            try (CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {
                List<Categoria> categorias = new ArrayList<>();
                while (rs.next()) {
                    categorias.add(mapear(rs, new Categoria()));
                }
                return categorias;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Categoria findById(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call buscar_categoria_por_id(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);

                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Categoria()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void insert(Categoria categoria) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (categoria == null) {
                throw new IllegalArgumentException("La categoria no puede ser nula");
            }

            String sql = "{call insertar_categoria(?,?,?,?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                setParametrosComunes(cmd, categoria);
                cmd.registerOutParameter("p_id", Types.INTEGER);

                cmd.execute();
                categoria.setId(cmd.getInt("p_id"));
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void update(Categoria categoria) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (categoria == null) {
                throw new IllegalArgumentException("La categoria no puede ser nula");
            }

            String sql = "{call modificar_categoria(?,?,?,?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", categoria.getId());
                setParametrosComunes(cmd, categoria);

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    // Los parametros que insertar_categoria_articulo y modificar_categoria_articulo comparten
    private void setParametrosComunes(CallableStatement cmd, Categoria categoria) throws SQLException {
        cmd.setString("p_nombre", categoria.getNombre());
        cmd.setString("p_descripcion", categoria.getDescripcion());
        cmd.setBoolean("p_activo", categoria.isActivo());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call eliminar_categoria(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public List<Categoria> findByArticuloId(int idArticulo) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_categorias_por_articulo(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setInt("p_id_articulo", idArticulo);
                try (ResultSet rs = cmd.executeQuery()) {
                    List<Categoria> categorias = new ArrayList<>();

                    while (rs.next()) {
                        categorias.add(mapear(rs, new Categoria()));
                    }
                    return categorias;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Categoria findByNombre(String nombre) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call buscar_categoria_por_nombre(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setString("p_nombre", nombre);
                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Categoria()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    protected Categoria mapear(ResultSet rs, Categoria categoria) throws SQLException {
        super.mapear(rs, categoria);
        categoria.setId(rs.getInt("id_categoria"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        return categoria;
    }
}
