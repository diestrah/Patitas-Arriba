package patitasarriba.dao.impl.horario;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.horario.HorarioPersonal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class HorarioAdministradorDAOImpl extends RegistroDAOImpl<HorarioPersonal>
        implements HorarioAdministradorDAO {

    @Override
    public void insert(int idAdministrador, HorarioPersonal horarioPersonal) throws SQLException {
        if (horarioPersonal == null) {
            throw new IllegalArgumentException("El horario personal no puede ser nulo");
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_horario_administrador(?,?,?,?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_administrador", idAdministrador);
            cmd.setInt("p_id_horario", horarioPersonal.getHorario().getId());
            cmd.setBoolean("p_activo", horarioPersonal.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            horarioPersonal.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(int idAdministrador, HorarioPersonal horarioPersonal) throws SQLException {
        if (horarioPersonal == null) {
            throw new IllegalArgumentException("El horario personal no puede ser nulo");
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call modificar_horario_administrador(?,?,?,?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_administrador", idAdministrador);
            cmd.setInt("p_id_horario", horarioPersonal.getHorario().getId());
            cmd.setBoolean("p_activo", horarioPersonal.isActivo());
            cmd.setInt("p_id", horarioPersonal.getId());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo modificar el horario del administrador");
            }
        }
    }

    @Override
    public void delete(int idHorarioAdministrador) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_horario_administrador(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", idHorarioAdministrador);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar el horario del administrador");
            }
        }
    }

    @Override
    public HorarioPersonal findById(int idHorarioAdministrador) throws SQLException {
        String sql = "{call buscar_horario_administrador_por_id(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", idHorarioAdministrador);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new HorarioPersonal()) : null;
            }
        }
    }

    @Override
    public List<HorarioPersonal> findAll() throws SQLException {
        String sql = "{call listar_horarios_administrador()}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {
            List<HorarioPersonal> horarios = new ArrayList<>();
            while (rs.next()) {
                horarios.add(mapear(rs, new HorarioPersonal()));
            }
            return horarios;
        }
    }

    @Override
    public List<HorarioPersonal> listarPorAdministrador(int idAdministrador) throws SQLException {
        String sql = "{call listar_horarios_por_administrador(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_administrador", idAdministrador);

            try (ResultSet rs = cmd.executeQuery()) {
                List<HorarioPersonal> horarios = new ArrayList<>();
                while (rs.next()) {
                    horarios.add(mapear(rs, new HorarioPersonal()));
                }
                return horarios;
            }
        }
    }

    @Override
    public void eliminarPorAdministrador(int idAdministrador) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_horarios_por_administrador(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_administrador", idAdministrador);
            cmd.executeUpdate();
        }
    }

    @Override
    protected HorarioPersonal mapear(ResultSet rs, HorarioPersonal horarioPersonal)
            throws SQLException {
        super.mapear(rs, horarioPersonal);
        horarioPersonal.setId(rs.getInt("ID_HORARIO_ADMIN"));
        horarioPersonal.setHorario(
                new HorarioDAOImpl().findById(rs.getInt("ID_HORARIO")));
        return horarioPersonal;
    }
}
