package patitasarriba.dao.impl.receta;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.receta.DetalleReceta;
import patitasarriba.modelo.receta.Receta;
import patitasarriba.dao.RecetaDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecetaDAOImpl extends RegistroDAOImpl<Receta> implements RecetaDAO {
    @Override
    public List<Receta> findAll() throws SQLException {
        String sql = "{call listar_recetas()}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {

            List<Receta> recetas = new ArrayList<>();
            while (rs.next()) {
                recetas.add(mapear(rs, new Receta()));
            }
            return recetas;
        }
    }

    @Override
    public Receta findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_receta_por_id(?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)){

            cmd.setInt("p_id", id);
            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Receta()) : null;
            }
        }
    }

    @Override
    public void insert(Receta receta) throws SQLException {
        if (receta == null) {
            throw new IllegalArgumentException("La receta no puede ser nula");
        }
        Connection conn = TransactionsManager.getConnection();

        String sql = "{insertar_receta(?, ?, ?, ?, ?}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            if (receta.getAtencionMedica() != null){
                cmd.setInt("p_id_atencion_medica", receta.getAtencionMedica().getId());
            }

            cmd.setDate("p_fecha_emision", Date.valueOf(receta.getFechaEmision()));
            cmd.setString("p_indicaciones_generales", receta.getIndicacionesGenerales());
            cmd.setInt("p_estado", receta.isActivo() ? 1 : 0);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo insertar la receta");
            }

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

        String sql = "{call modificar_receta(?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)){
            if (receta.getAtencionMedica() != null) {
                cmd.setInt("p_id_atencion_medica", receta.getAtencionMedica().getId());
            }
            cmd.setDate("p_fecha_emision", Date.valueOf(receta.getFechaEmision()));
            cmd.setString("p_indicaciones_generales", receta.getIndicacionesGenerales());
            cmd.setInt("p_estado", receta.isActivo() ? 1 : 0);
            cmd.setInt("p_id", receta.getId());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo actualizar la receta");
            }

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
            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar la receta");
            }
        }
    }

    protected Receta mapear(ResultSet rs, Receta receta) throws SQLException {
        super.mapear(rs, receta);
        mapearAtencionMedica(rs, receta);
        receta.setId(rs.getInt("id_receta"));
        receta.setFechaEmision(rs.getDate("fecha_emision").toLocalDate());
        receta.setIndicacionesGenerales(rs.getString("indicaciones_generales"));
        
        mapearDetalles(rs, receta);
        return receta;
    }

    private void mapearAtencionMedica(ResultSet rs, Receta receta) throws SQLException {
        int idAtencionMedica = rs.getInt("id_atencion_medica");
        if (!rs.wasNull()) {
            receta.setAtencionMedica(new RecetaDAOImpl().findById(idAtencionMedica).getAtencionMedica());
        }
        else{
            receta.setAtencionMedica(null);
        }
    }

    private void mapearDetalles(ResultSet rs, Receta receta) throws SQLException{
        DetalleRecetaDAO detalleRecetaDAO = new DetalleRecetaDAOImpl();
        List<DetalleReceta> detalles = detalleRecetaDAO.buscarPorIdReceta(rs.getInt("id_receta"));
        receta.setDetalles(detalles);
    }

}
