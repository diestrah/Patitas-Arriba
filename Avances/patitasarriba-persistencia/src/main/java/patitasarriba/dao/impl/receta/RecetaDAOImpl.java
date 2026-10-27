package patitasarriba.dao.impl.receta;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.receta.DetalleReceta;
import patitasarriba.modelo.receta.Receta;
import patitasarriba.dao.RecetaDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecetaDAOImpl extends RegistroDAOImpl<Receta> implements RecetaDAO {
    @Override
    public List<Receta> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_recetas()}";

            try (CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {

                List<Receta> recetas = new ArrayList<>();
                while (rs.next()) {
                    recetas.add(mapear(rs, new Receta()));
                }
                return recetas;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Receta findById(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call buscar_receta_por_id(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)){

                cmd.setInt("p_id", id);
                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Receta()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void insert(Receta receta) throws SQLException {
        if (receta == null) {
            throw new IllegalArgumentException("La receta no puede ser nula");
        }
        Connection conn = TransactionsManager.getConnection();

        if (receta.getAtencion() == null) {
            throw new IllegalArgumentException("La receta debe tener una atención médica");
        }

        String sql = "{call insertar_receta(?,?,?,?,?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_atencion", receta.getAtencion().getId());
            cmd.setDate("p_fecha_emision", Date.valueOf(receta.getFechaEmision()));
            cmd.setString("p_indicaciones_generales", receta.getIndicacionesGenerales());
            cmd.setBoolean("p_activo", receta.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);
            cmd.execute();
            receta.setId(cmd.getInt("p_id"));

            DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
            detalleRecetaDAO.insertarDetalles(receta.getId(), receta.getDetalles());
        }
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

        String sql = "{call modificar_receta(?,?,?,?,?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)){
            cmd.setInt("p_id_atencion", receta.getAtencion().getId());
            cmd.setDate("p_fecha_emision", Date.valueOf(receta.getFechaEmision()));
            cmd.setString("p_indicaciones_generales", receta.getIndicacionesGenerales());
            cmd.setBoolean("p_activo", receta.isActivo());
            cmd.setInt("p_id", receta.getId());

            cmd.execute();

            DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
            detalleRecetaDAO.eliminarDetalles(receta.getId());
            detalleRecetaDAO.insertarDetalles(receta.getId(), receta.getDetalles());
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_receta(?)}";

        DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
        detalleRecetaDAO.eliminarDetalles(id);

        try (CallableStatement cmd = conn.prepareCall(sql)){
            cmd.setInt("p_id", id);
            cmd.execute();
        }
    }

    protected Receta mapear(ResultSet rs, Receta receta) throws SQLException {
        super.mapear(rs, receta);
        mapearAtencion(rs, receta);
        receta.setId(rs.getInt("id_receta"));
        receta.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        receta.setIndicacionesGenerales(rs.getString("indicaciones_generales"));

        mapearDetalles(rs, receta);
        return receta;
    }

    private void mapearAtencion(ResultSet rs, Receta receta) throws SQLException {
        Atencion atencion = new patitasarriba.dao.impl.atencion.AtencionDAOImpl()
        .findById(rs.getInt("id_atencion"));
        if (atencion == null) throw new SQLException("No existe la atencion activa de la receta");
        receta.setAtencion(atencion);
    }
    private void mapearDetalles(ResultSet rs, Receta receta) throws SQLException{
        DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
        List<DetalleReceta> detalles = detalleRecetaDAO.buscarPorIdReceta(rs.getInt("id_receta"));
        receta.setDetalles(detalles);
    }


}
