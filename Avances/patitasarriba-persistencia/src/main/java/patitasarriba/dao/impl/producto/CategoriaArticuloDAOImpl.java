package patitasarriba.dao.impl.producto;

import conexion.DBManager;
import patitasarriba.dao.CategoriaArticuloDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.producto.CategoriaArticulo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoriaArticuloDAOImpl extends RegistroDAOImpl<CategoriaArticulo> implements CategoriaArticuloDAO {

    @Override
    public List<CategoriaArticulo> findAll() throws SQLException {
        String sql = "{call listar_categoria_articulo()}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {
            List<CategoriaArticulo> categorias = new ArrayList<>();
            while (rs.next()) {
                categorias.add(mapear(rs, new CategoriaArticulo()));
            }
            return categorias;
        }
    }

    @Override
    public CategoriaArticulo findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_categoria_articulo_por_id(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new CategoriaArticulo()) : null;
            }
        }
    }

    @Override
    public void insert(CategoriaArticulo categoria) throws SQLException {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria no puede ser nula");
        }

        String sql = "{call insertar_categoria_articulo(?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, categoria);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            categoria.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(CategoriaArticulo categoria) throws SQLException {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoria no puede ser nula");
        }

        String sql = "{call modificar_categoria_articulo(?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", categoria.getId());
            setParametrosComunes(cmd, categoria);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo modificar la categoria de articulo");
            }
        }
    }

    // Los parametros que insertar_categoria_articulo y modificar_categoria_articulo comparten
    private void setParametrosComunes(CallableStatement cmd, CategoriaArticulo categoria) throws SQLException {
        cmd.setString("p_nombre", categoria.getNombre());
        cmd.setString("p_descripcion", categoria.getDescripcion());
        cmd.setBoolean("p_activo", categoria.isActivo());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_categoria_articulo(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar la categoria de articulo");
            }
        }
    }

    @Override
    protected CategoriaArticulo mapear(ResultSet rs, CategoriaArticulo categoria) throws SQLException {
        super.mapear(rs, categoria);
        categoria.setId(rs.getInt("id_categoria_articulo"));
        categoria.setNombre(rs.getString("nombre"));
        categoria.setDescripcion(rs.getString("descripcion"));
        return categoria;
    }
}
