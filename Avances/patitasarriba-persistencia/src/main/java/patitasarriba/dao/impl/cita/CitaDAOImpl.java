package patitasarriba.dao.impl.cita;

import conexion.DBManager;
import patitasarriba.dao.CitaDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.cita.EstadoCita;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CitaDAOImpl extends RegistroDAOImpl<Cita> implements CitaDAO {
    private final DetalleCitaDAOImpl detalleCitaDAO = new DetalleCitaDAOImpl();

    @Override
    public List<Cita> findAll() throws SQLException {
        String sql = "{call listar_cita()}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {
            List<Cita> citas = new ArrayList<>();
            while (rs.next()) {
                Cita cita = mapear(rs, new Cita());
                cita.setDetalles(detalleCitaDAO.listarPorCita(conn, cita.getId()));
                citas.add(cita);
            }
            return citas;
        }
    }

    @Override
    public Cita findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_cita_por_id(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            try (ResultSet rs = cmd.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Cita cita = mapear(rs, new Cita());
                cita.setDetalles(detalleCitaDAO.listarPorCita(conn, cita.getId()));
                return cita;
            }
        }
    }

    // Inserta en 2 tablas (cita + detalle_cita) de forma atomica: requiere que la
    // capa de negocio ya haya llamado TransactionsManager.iniciar() antes de esto.
    @Override
    public void insert(Cita cita) throws SQLException {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede ser nula");
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_cita(?,?,?,?,?,?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setObject("p_fecha_hora", cita.getFechaHora());
            cmd.setString("p_estado", cita.getEstado().name());
            cmd.setInt("p_id_mascota", cita.getMascota().getId());
            cmd.setInt("p_id_veterinario", cita.getVeterinario().getId());
            cmd.setBoolean("p_activo", cita.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            cita.setId(cmd.getInt("p_id"));
        }

        // El maestro orquesta la insercion de cada detalle, en la misma transaccion
        for (DetalleCita detalle : cita.getDetalles()) {
            detalleCitaDAO.insert(conn, detalle, cita.getId());
        }
    }

    @Override
    public void update(Cita cita) throws SQLException {
        if (cita == null) {
            throw new IllegalArgumentException("La cita no puede ser nula");
        }

        String sql = "{call modificar_cita(?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", cita.getId());
            cmd.setObject("p_fecha_hora", cita.getFechaHora());
            cmd.setString("p_estado", cita.getEstado().name());
            cmd.setInt("p_id_mascota", cita.getMascota().getId());
            cmd.setInt("p_id_veterinario", cita.getVeterinario().getId());
            cmd.setBoolean("p_activo", cita.isActivo());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo modificar la cita");
            }
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_cita(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar la cita");
            }
        }
    }

    @Override
    protected Cita mapear(ResultSet rs, Cita cita) throws SQLException {
        super.mapear(rs, cita);
        cita.setId(rs.getInt("id_cita"));
        cita.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        cita.setEstado(EstadoCita.valueOf(rs.getString("estado")));

        // Mascota y Veterinario no son parte de este modulo: se dejan con solo el id
        // hasta que existan MascotaDAOImpl / VeterinarioDAOImpl
        Mascota mascota = new Mascota();
        mascota.setId(rs.getInt("id_mascota"));
        cita.setMascota(mascota);

        Veterinario veterinario = new Veterinario();
        veterinario.setId(rs.getInt("id_veterinario"));
        cita.setVeterinario(veterinario);

        return cita;
    }
}
