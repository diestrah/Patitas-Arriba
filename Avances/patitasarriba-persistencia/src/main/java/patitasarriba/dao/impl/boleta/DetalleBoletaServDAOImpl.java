package patitasarriba.dao.impl.boleta;

import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.boleta.DetalleBoleta;
import patitasarriba.modelo.producto.Servicio;
import conexion.DBManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class DetalleBoletaServDAOImpl extends RegistroDAOImpl<DetalleBoleta>
        implements DetalleBoletaServDAO {

    @Override
    public void insertDetalles(int idBoleta, List<DetalleBoleta> detalles) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_detalle_boleta_serv(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (DetalleBoleta detalle : detalles) {
                cmd.setInt("p_id_boleta", idBoleta);
                cmd.setInt("p_id_servicio", detalle.getProducto().getId());
                cmd.setBoolean("p_activo", true);
                cmd.setInt("p_cantidad", detalle.getCantidad());
                cmd.setDouble("p_subtotal", detalle.getSubTotal());
                cmd.registerOutParameter("p_id", Types.INTEGER);

                if (cmd.executeUpdate() == 0) {
                    throw new SQLException("No se pudo insertar el detalle de boleta (servicio)");
                }

                detalle.setId(cmd.getInt("p_id"));
            }
        }
    }

    @Override
    public void deleteDetallesPorBoleta(int idBoleta) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_detalles_boleta_serv_por_boleta(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_boleta", idBoleta);
            cmd.executeUpdate();
        }
    }

    @Override
    public List<DetalleBoleta> findByBoletaId(int idBoleta) throws SQLException {
        String sql = "{call listar_detalles_boleta_serv_por_boleta(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id_boleta", idBoleta);
            try (ResultSet rs = cmd.executeQuery()) {
                List<DetalleBoleta> detalles = new ArrayList<>();
                while (rs.next()) {
                    DetalleBoleta detalle = mapear(rs, new DetalleBoleta());
                    detalles.add(detalle);
                }
                return detalles;
            }
        }
    }

    @Override
    protected DetalleBoleta mapear(ResultSet rs, DetalleBoleta detalle) throws SQLException {
        detalle.setId(rs.getInt("ID_DETALLE_SERV"));
        detalle.setActivo(rs.getBoolean("ACTIVO"));

        Servicio servicio = new Servicio();
        servicio.setId(rs.getInt("ID_SERVICIO"));
        // Se carga solo el id; la capa de negocio o un DAO de Servicio
        // puede enriquecer el objeto si es necesario
        detalle.setProducto(servicio);

        detalle.setCantidad(rs.getInt("CANTIDAD"));
        detalle.setSubTotal(rs.getDouble("SUBTOTAL"));

        return detalle;
    }
}
