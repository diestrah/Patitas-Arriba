package patitasarriba.dao.impl.cita;

import patitasarriba.dao.transacciones.TransactionsManager;
import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.impl.producto.ServicioDAOImpl;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetalleCitaDAOImpl extends RegistroDAOImpl<DetalleCita> {
    void insert(Connection conn, DetalleCita detalle, int idCita) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call insertar_detalle_cita(?,?,?,?,?,?)}")) {
            cmd.setInt("p_id_cita", idCita);
            parametros(cmd, detalle);
            cmd.registerOutParameter("p_id", Types.INTEGER);
            cmd.execute();
            detalle.setId(cmd.getInt("p_id"));
        }
    }

    private void parametros(CallableStatement cmd, DetalleCita detalle) throws SQLException {
        cmd.setInt("p_id_servicio", detalle.getServicio().getId());
        cmd.setInt("p_id_veterinario", detalle.getVeterinario().getId());
        cmd.setString("p_observaciones", detalle.getObservaciones());
        cmd.setBoolean("p_activo", detalle.isActivo());
    }

    void actualizarPorCita(Connection conn, Cita cita) throws SQLException {
        Set<Integer> existentes = new HashSet<>();
        for (DetalleCita detalle : listarPorCita(conn, cita)) existentes.add(detalle.getId());
        Set<Integer> recibidos = new HashSet<>();
        // Validar antes de desactivar; conservar los IDs referenciados por atenciones y boletas.
        for (DetalleCita detalle : cita.getDetalles()) {
            if (detalle.getId() != 0 && (!existentes.contains(detalle.getId()) || !recibidos.add(detalle.getId()))) {
                throw new SQLException("Detalle ajeno a la cita o duplicado: " + detalle.getId());
            }
        }
        deleteDetalles(conn, cita.getId());
        for (DetalleCita detalle : cita.getDetalles()) {
            if (detalle.getId() == 0) {
                insert(conn, detalle, cita.getId());
            } else {
                try (CallableStatement cmd = conn.prepareCall("{call modificar_detalle_cita(?,?,?,?,?)}")) {
                    cmd.setInt("p_id", detalle.getId());
                    parametros(cmd, detalle);
                    cmd.execute();
                }
            }
        }
    }

    void deleteDetalles(Connection conn, int idCita) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call eliminar_detalles_por_cita(?)}")) {
            cmd.setInt("p_id_cita", idCita);
            cmd.execute();
        }
    }

    List<DetalleCita> listarPorCita(Connection conn, Cita cita) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call listar_detalle_cita_por_cita(?)}")) {
            cmd.setInt("p_id_cita", cita.getId());
            try (ResultSet rs = cmd.executeQuery()) {
                List<DetalleCita> detalles = new ArrayList<>();
                while (rs.next()) detalles.add(mapear(rs, new DetalleCita(), cita));
                return detalles;
            }
        }
    }

    public DetalleCita buscarDetallePorId(int id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            try (CallableStatement cmd = conn.prepareCall("{call buscar_detalle_cita_por_id(?)}")) {
                cmd.setInt("p_id", id);
                try (ResultSet rs = cmd.executeQuery()) {
                    if (!rs.next()) return null;
                    Cita cita = new CitaDAOImpl().buscarCabecera(conn, rs.getInt("id_cita"));
                    if (cita == null) throw new SQLException("No existe la cita activa del detalle " + id);
                    return mapear(rs, new DetalleCita(), cita);
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    private DetalleCita mapear(ResultSet rs, DetalleCita detalle, Cita cita) throws SQLException {
        super.mapear(rs, detalle);
        detalle.setId(rs.getInt("id_detalle_cita"));
        detalle.setObservaciones(rs.getString("observaciones"));
        var servicio = new ServicioDAOImpl().findById(rs.getInt("id_servicio"));
        var veterinario = new VeterinarioDAOImpl().findById(rs.getInt("id_veterinario"));
        if (servicio == null || veterinario == null) {
            throw new SQLException("Servicio o veterinario inexistente/inactivo en el detalle " + detalle.getId());
        }
        detalle.setServicio(servicio);
        detalle.setVeterinario(veterinario);
        detalle.setCita(cita);
        return detalle;
    }
}
