package patitasarriba.dao.impl.boleta;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.impl.cita.DetalleCitaDAOImpl;
import patitasarriba.dao.impl.atencion.InsumoUtilizadoDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.boleta.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class DetalleBoletaServDAOImpl extends RegistroDAOImpl<DetalleBoletaServicio> implements DetalleBoletaServDAO {
    @Override
    public void insertDetalles(int idBoleta, List<DetalleBoleta> detalles) throws SQLException {
        Connection conn = TransactionsManager.getConnection();
        for (DetalleBoleta base : detalles) {
            if (!(base instanceof DetalleBoletaServicio linea)) throw new SQLException("Tipo de detalle incorrecto");
            boolean nuevo = linea.getId() == 0;
            String sql = nuevo ? "{call insertar_detalle_boleta_serv(?,?,?,?,?,?)}"
            : "{call modificar_detalle_boleta_serv(?,?,?,?,?,?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id_boleta", idBoleta);
                cmd.setInt("p_id_detalle_cita", linea.getDetalleCita().getId());
                cmd.setInt("p_cantidad", linea.getCantidad());
                cmd.setBigDecimal("p_precio_unitario", BigDecimal.valueOf(linea.getPrecioUnitario()));
                cmd.setBoolean("p_activo", linea.isActivo());
                if (nuevo) cmd.registerOutParameter("p_id", Types.INTEGER);
                else cmd.setInt("p_id", linea.getId());
                cmd.execute();
                if (nuevo) linea.setId(cmd.getInt("p_id"));
            }
        }
    }

    @Override
    public void deleteDetallesPorBoleta(int idBoleta) throws SQLException {
        Connection conn = TransactionsManager.getConnection();
        try (CallableStatement cmd = conn.prepareCall("{call eliminar_detalles_boleta_serv_por_boleta(?)}")) {
            cmd.setInt("p_id_boleta", idBoleta);
            cmd.execute();
        }
    }

    @Override
    public List<DetalleBoleta> findByBoletaId(int idBoleta) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            try (CallableStatement cmd = conn.prepareCall("{call listar_detalles_boleta_serv_por_boleta(?)}")) {
                cmd.setInt("p_id_boleta", idBoleta);
                try (ResultSet rs = cmd.executeQuery()) {
                    List<DetalleBoleta> detalles = new ArrayList<>();
                    while (rs.next()) detalles.add(mapear(rs, new DetalleBoletaServicio()));
                    return detalles;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    protected DetalleBoletaServicio mapear(ResultSet rs, DetalleBoletaServicio detalle) throws SQLException {
        super.mapear(rs, detalle);
        detalle.setId(rs.getInt("id_detalle_boleta_serv"));
        Boleta boleta = new Boleta();
        boleta.setId(rs.getInt("id_boleta"));
        detalle.setBoleta(boleta);
        detalle.setCantidad(rs.getInt("cantidad"));
        detalle.setPrecioUnitario(rs.getDouble("precio_unitario"));
        detalle.setSubtotal(rs.getDouble("subtotal"));
        var detalleCita = new DetalleCitaDAOImpl().buscarDetallePorId(rs.getInt("id_detalle_cita"));
        if (detalleCita == null) throw new SQLException("No existe el detalle de cita facturado");
        detalle.setDetalleCita(detalleCita);
        return detalle;
    }
}
