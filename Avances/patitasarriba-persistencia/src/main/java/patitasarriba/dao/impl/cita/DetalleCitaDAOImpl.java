package patitasarriba.dao.impl.cita;

import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.producto.Servicio;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

// Sin interfaz publica: solo CitaDAOImpl la usa, para orquestar el maestro-detalle.
// Por eso sus metodos reciben la Connection ya abierta, en vez de pedir una nueva:
// siempre se ejecuta dentro de la transaccion que ya inicio CitaDAOImpl.
class DetalleCitaDAOImpl extends RegistroDAOImpl<DetalleCita> {

    void insert(Connection conn, DetalleCita detalle, int idCita) throws SQLException {
        String sql = "{call insertar_detalle_cita(?,?,?,?,?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cita", idCita);
            cmd.setInt("p_id_servicio", detalle.getServicio().getId());
            cmd.setString("p_observaciones", detalle.getObservaciones());
            cmd.setBoolean("p_activo", detalle.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            detalle.setId(cmd.getInt("p_id"));
        }
    }

    // Borra todos los detalles de una cita (hijos antes que padre, dentro de
    // la misma transaccion) — la FK se mantiene en NO ACTION a proposito.
    void deleteDetalles(Connection conn, int idCita) throws SQLException {
        String sql = "{call eliminar_detalles_por_cita(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cita", idCita);
            cmd.executeUpdate();
        }
    }

    List<DetalleCita> listarPorCita(Connection conn, int idCita) throws SQLException {
        String sql = "{call listar_detalle_cita_por_cita(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cita", idCita);
            try (ResultSet rs = cmd.executeQuery()) {
                List<DetalleCita> detalles = new ArrayList<>();
                while (rs.next()) {
                    detalles.add(mapear(rs, new DetalleCita()));
                }
                return detalles;
            }
        }
    }

    @Override
    protected DetalleCita mapear(ResultSet rs, DetalleCita detalle) throws SQLException {
        super.mapear(rs, detalle);
        detalle.setId(rs.getInt("id_detalle_cita"));
        detalle.setObservaciones(rs.getString("observaciones"));

        // Servicio no es parte de este modulo: se deja con solo el id
        // hasta que exista ServicioDAOImpl
        Servicio servicio = new Servicio();
        servicio.setId(rs.getInt("id_servicio"));
        detalle.setServicio(servicio);

        return detalle;
    }
}
