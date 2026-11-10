package patitasarriba.dao.impl.receta;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.receta.DetalleReceta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DetalleRecetaDAOImpl extends RegistroDAOImpl<DetalleReceta> implements DetalleRecetaDAO {
    @Override
    public void insertarDetalles(int idReceta, List<DetalleReceta> detallesReceta) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_detalle_receta (?, ?, ?, ?, ?, ?, ?,?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (DetalleReceta detalleReceta : detallesReceta) {
                cmd.setInt("p_id_receta", idReceta);
                cmd.setInt("p_id_articulo", detalleReceta.getArticulo().getId());
                cmd.setString("p_dosis", detalleReceta.getDosis());
                cmd.setString("p_frecuencia", detalleReceta.getFrecuencia());
                cmd.setInt("p_duracion_dias", detalleReceta.getDuracionDias());
                cmd.setInt("p_cantidad_total", detalleReceta.getCantidadTotal());
                cmd.setBoolean("p_activo", detalleReceta.isActivo());
                cmd.registerOutParameter("p_id", Types.INTEGER);

                cmd.execute();

                detalleReceta.setId(cmd.getInt("p_id"));
            }

        }
    }

    @Override
    public void eliminarDetalles(int idReceta) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_detalles_por_receta(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_receta", idReceta);
            cmd.executeUpdate();
        }
    }

    @Override
    public List<DetalleReceta> buscarPorIdReceta(int idReceta) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_detalles_por_receta(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setInt("p_id_receta", idReceta);
                try (ResultSet rs = cmd.executeQuery()) {
                    List<DetalleReceta> detalles = new ArrayList<>();
                    while (rs.next()) {
                        detalles.add(mapear(rs, new DetalleReceta()));
                    }
                    return detalles;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    protected DetalleReceta mapear(ResultSet rs, DetalleReceta detalle) throws SQLException {
        super.mapear(rs, detalle);
        detalle.setId(rs.getInt("id_detalle_receta"));
        detalle.setDosis(rs.getString("dosis"));
        detalle.setFrecuencia(rs.getString("frecuencia"));
        detalle.setDuracionDias(rs.getInt("duracion_dias"));
        detalle.setCantidadTotal(rs.getInt("cantidad_total"));
        var articulo = new patitasarriba.dao.impl.producto.ArticuloDAOImpl().findById(rs.getInt("id_articulo"));
        if (articulo == null) throw new SQLException("No existe el articulo del detalle de receta");
        detalle.setArticulo(articulo);
        var receta = new patitasarriba.modelo.receta.Receta();
        receta.setId(rs.getInt("id_receta"));
        detalle.setReceta(receta);
        return detalle;
    }
}
