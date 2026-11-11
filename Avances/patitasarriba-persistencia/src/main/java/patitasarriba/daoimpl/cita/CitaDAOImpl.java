package patitasarriba.daoimpl.cita;

import patitasarriba.daoimpl.BaseDAOImpl;

import patitasarriba.dao.cita.CitaDAO;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.daoimpl.mascota.MascotaDAOImpl;
import patitasarriba.daoimpl.usuario.AdministradorDAOImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.EstadoCita;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CitaDAOImpl extends BaseDAOImpl<Cita> implements CitaDAO {
    private final DetalleCitaDAOImpl detalleDAO = new DetalleCitaDAOImpl();

    @Override
    public List<Cita> findAll() throws SQLException {
        return ejecutarComando(conn -> {
                try (CallableStatement cmd = comandoListar(conn);
                ResultSet rs = cmd.executeQuery()) {
                    List<Cita> citas = new ArrayList<>();
                    while (rs.next()) {
                        Cita cita = mapear(rs);
                        cita.setDetalles(detalleDAO.listarPorCita(conn, cita));
                        citas.add(cita);
                    }
                    return citas;
                }
            });
    }

    @Override
    public Cita findById(Integer id) throws SQLException {
        return ejecutarComando(conn -> {
                if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
                {
                    Cita cita = buscarCabecera(conn, id);
                    if (cita != null) cita.setDetalles(detalleDAO.listarPorCita(conn, cita));
                    return cita;
                }
            });
    }

    // Carga la cabecera sin volver a cargar detalles, para evitar recursion.
    Cita buscarCabecera(Connection conn, int id) throws SQLException {
        try (CallableStatement cmd = comandoBuscar(conn, id)) {
            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    @Override
    public void insert(Cita cita) throws SQLException {
        if (cita == null) throw new IllegalArgumentException("La cita no puede ser nula");
        Connection conn = TransactionsManager.getConnection();
        ejecutarComandoInsertar(conn, cita);
        for (var detalle : cita.getDetalles()) detalleDAO.insert(conn, detalle, cita.getId());
    }

    @Override
    public void update(Cita cita) throws SQLException {
        if (cita == null) throw new IllegalArgumentException("La cita no puede ser nula");
        Connection conn = TransactionsManager.getConnection();
        ejecutarComandoActualizar(conn, cita);
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
        ejecutarComandoEliminar(conn, id);
    }

    @Override
    protected Cita mapear(ResultSet rs) throws SQLException {
        Cita cita = new Cita();
        cita.setActivo(rs.getBoolean("activo"));
        cita.setId(rs.getInt("id_cita"));
        cita.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        cita.setEstado(EstadoCita.valueOf(rs.getString("estado")));
        cita.setMascota(new MascotaDAOImpl().findById(rs.getInt("id_mascota")));
        int idAdministrador = rs.getInt("id_administrador");
        cita.setAdministrador(rs.wasNull() ? null : new AdministradorDAOImpl().findById(idAdministrador));
        cita.setEsEmergencia(rs.getBoolean("es_emergencia"));
        return cita;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call listar_cita()}");
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call buscar_cita_por_id(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Cita modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call insertar_cita(?,?,?,?,?,?,?)}");
        try {
            parametros(cmd, modelo);
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Cita modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call modificar_cita(?,?,?,?,?,?,?)}");
        try {
            cmd.setInt("p_id", modelo.getId());
            parametros(cmd, modelo);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call eliminar_cita(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Cita modelo) throws SQLException {
        int id = cmd.getInt("p_id");
        if (cmd.wasNull() || id <= 0) throw new SQLException("No se obtuvo un ID válido para Cita.");
        modelo.setId(id);
    }
}
