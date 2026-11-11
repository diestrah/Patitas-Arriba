package patitasarriba.daoimpl.receta;

import patitasarriba.daoimpl.BaseDAOImpl;

import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.daoimpl.atencion.AtencionDAOImpl;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.receta.DetalleReceta;
import patitasarriba.modelo.receta.Receta;
import patitasarriba.dao.receta.RecetaDAO;

import java.sql.*;
import java.util.List;

public class RecetaDAOImpl extends BaseDAOImpl<Receta> implements RecetaDAO {

    @Override
    public void insert(Receta receta) throws SQLException {
        if (receta == null) {
            throw new IllegalArgumentException("La receta no puede ser nula");
        }
        Connection conn = TransactionsManager.getConnection();

        if (receta.getAtencion() == null) {
            throw new IllegalArgumentException("La receta debe tener una atención médica");
        }

        ejecutarComandoInsertar(conn, receta);
        DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
        detalleRecetaDAO.insertarDetalles(receta.getId(), receta.getDetalles());
    }

    @Override
    public void update(Receta receta) throws SQLException {
        if (receta == null) {
            throw new IllegalArgumentException("La receta no puede ser nula");
        }

        Connection conn = TransactionsManager.getConnection();

        if (receta.getAtencion() == null) {
            throw new IllegalArgumentException("La receta debe tener una atención médica");
        }

        ejecutarComandoActualizar(conn, receta);
        DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
        detalleRecetaDAO.eliminarDetalles(receta.getId());
        detalleRecetaDAO.insertarDetalles(receta.getId(), receta.getDetalles());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        Connection conn = TransactionsManager.getConnection();

        DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
        detalleRecetaDAO.eliminarDetalles(id);

        ejecutarComandoEliminar(conn, id);
    }

    protected Receta mapear(ResultSet rs) throws SQLException {
        Receta receta = new Receta();
        receta.setActivo(rs.getBoolean("activo"));
        mapearAtencion(rs, receta);
        receta.setId(rs.getInt("id_receta"));
        receta.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        receta.setIndicacionesGenerales(rs.getString("indicaciones_generales"));

        mapearDetalles(rs, receta);
        return receta;
    }

    private void mapearAtencion(ResultSet rs, Receta receta) throws SQLException {
        Atencion atencion = new AtencionDAOImpl()
        .findById(rs.getInt("id_atencion"));
        if (atencion == null) throw new SQLException("No existe la atencion activa de la receta");
        receta.setAtencion(atencion);
    }
    private void mapearDetalles(ResultSet rs, Receta receta) throws SQLException{
        DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
        List<DetalleReceta> detalles = detalleRecetaDAO.buscarPorIdReceta(rs.getInt("id_receta"));
        receta.setDetalles(detalles);
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call listar_recetas()}");
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call buscar_receta_por_id(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Receta modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call insertar_receta(?,?,?,?,?)}");
        try {
            cmd.setInt("p_id_atencion", modelo.getAtencion().getId());
            cmd.setDate("p_fecha_emision", Date.valueOf(modelo.getFechaEmision()));
            cmd.setString("p_indicaciones_generales", modelo.getIndicacionesGenerales());
            cmd.setBoolean("p_activo", modelo.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Receta modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call modificar_receta(?,?,?,?,?)}");
        try {
            cmd.setInt("p_id_atencion", modelo.getAtencion().getId());
            cmd.setDate("p_fecha_emision", Date.valueOf(modelo.getFechaEmision()));
            cmd.setString("p_indicaciones_generales", modelo.getIndicacionesGenerales());
            cmd.setBoolean("p_activo", modelo.isActivo());
            cmd.setInt("p_id", modelo.getId());
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call eliminar_receta(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Receta modelo) throws SQLException {
        int id = cmd.getInt("p_id");
        if (cmd.wasNull() || id <= 0) throw new SQLException("No se obtuvo un ID válido para Receta.");
        modelo.setId(id);
    }
}
