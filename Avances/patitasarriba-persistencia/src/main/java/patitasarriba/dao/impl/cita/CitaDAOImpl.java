package patitasarriba.dao.impl.cita;

import conexion.DBManager;
import patitasarriba.dao.CitaDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.dao.impl.mascota.MascotaDAOImpl;
import patitasarriba.dao.impl.usuario.AdministradorDAOImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.EstadoCita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAOImpl extends RegistroDAOImpl<Cita> implements CitaDAO {
    private final DetalleCitaDAOImpl detalleDAO = new DetalleCitaDAOImpl();

    @Override
    public List<Cita> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            try (CallableStatement cmd = conn.prepareCall("{call listar_cita()}");
                ResultSet rs = cmd.executeQuery()) {
                List<Cita> citas = new ArrayList<>();
                while (rs.next()) {
                    Cita cita = mapear(rs, new Cita());
                    cita.setDetalles(detalleDAO.listarPorCita(conn, cita));
                    citas.add(cita);
                }
                return citas;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Cita findById(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
            {
                Cita cita = buscarCabecera(conn, id);
                if (cita != null) cita.setDetalles(detalleDAO.listarPorCita(conn, cita));
                return cita;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    // Carga la cabecera sin volver a cargar detalles, para evitar recursion.
    Cita buscarCabecera(Connection conn, int id) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call buscar_cita_por_id(?)}")) {
            cmd.setInt("p_id", id);
            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Cita()) : null;
            }
        }
    }

    @Override
    public void insert(Cita cita) throws SQLException {
        if (cita == null) throw new IllegalArgumentException("La cita no puede ser nula");
        Connection conn = TransactionsManager.getConnection();
        try (CallableStatement cmd = conn.prepareCall("{call insertar_cita(?,?,?,?,?,?,?)}")) {
            parametros(cmd, cita);
            cmd.registerOutParameter("p_id", Types.INTEGER);
            cmd.execute();
            cita.setId(cmd.getInt("p_id"));
        }
        for (var detalle : cita.getDetalles()) detalleDAO.insert(conn, detalle, cita.getId());
    }

    @Override
    public void update(Cita cita) throws SQLException {
        if (cita == null) throw new IllegalArgumentException("La cita no puede ser nula");
        Connection conn = TransactionsManager.getConnection();
        try (CallableStatement cmd = conn.prepareCall("{call modificar_cita(?,?,?,?,?,?,?)}")) {
            cmd.setInt("p_id", cita.getId());
            parametros(cmd, cita);
            cmd.execute();
        }
        detalleDAO.actualizarPorCita(conn, cita);
    }

    private void parametros(CallableStatement cmd, Cita cita) throws SQLException {
        cmd.setObject("p_fecha_hora", cita.getFechaHora());
        cmd.setString("p_estado", cita.getEstado().name());
        cmd.setInt("p_id_mascota", cita.getMascota().getId());
        if (cita.getAdministrador() == null) cmd.setNull("p_id_administrador", Types.INTEGER);
        else cmd.setInt("p_id_administrador", cita.getAdministrador().getId());
        cmd.setBoolean("p_es_emergencia", cita.isEsEmergencia());
        cmd.setBoolean("p_activo", cita.isActivo());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        Connection conn = TransactionsManager.getConnection();
        detalleDAO.deleteDetalles(conn, id);
        try (CallableStatement cmd = conn.prepareCall("{call eliminar_cita(?)}")) {
            cmd.setInt("p_id", id);
            cmd.execute();
        }
    }

    @Override
    protected Cita mapear(ResultSet rs, Cita cita) throws SQLException {
        super.mapear(rs, cita);
        cita.setId(rs.getInt("id_cita"));
        cita.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        cita.setEstado(EstadoCita.valueOf(rs.getString("estado")));
        cita.setMascota(new MascotaDAOImpl().findById(rs.getInt("id_mascota")));
        int idAdministrador = rs.getInt("id_administrador");
        cita.setAdministrador(rs.wasNull() ? null : new AdministradorDAOImpl().findById(idAdministrador));
        cita.setEsEmergencia(rs.getBoolean("es_emergencia"));
        return cita;
    }
}
