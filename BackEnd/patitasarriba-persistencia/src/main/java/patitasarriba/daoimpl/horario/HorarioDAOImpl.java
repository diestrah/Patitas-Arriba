package patitasarriba.daoimpl.horario;

import patitasarriba.dao.horario.HorarioDAO;
import patitasarriba.daoimpl.BaseDAOImpl;
import patitasarriba.modelo.horario.DiaSemana;
import patitasarriba.modelo.horario.Horario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;

public class HorarioDAOImpl extends BaseDAOImpl<Horario> implements HorarioDAO {

    @Override
    protected Horario mapear(ResultSet rs) throws SQLException {
        Horario horario = new Horario();
        horario.setActivo(rs.getBoolean("activo"));
        horario.setId(rs.getInt("ID_HORARIO"));
        horario.setDiaSemana(DiaSemana.valueOf(rs.getString("DIA_SEMANA")));
        horario.setHoraInicio(rs.getTime("HORA_INICIO").toLocalTime());
        horario.setHoraFin(rs.getTime("HORA_FIN").toLocalTime());
        return horario;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        String sql = "{call listar_horarios()}";
        CallableStatement cmd = conn.prepareCall(sql);
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_horario_por_id(?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Horario modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El horario no puede ser nulo");
        }

        String sql = "{call insertar_horario(?,?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setString("p_dia_semana", modelo.getDiaSemana().name());
            cmd.setTime("p_hora_inicio", Time.valueOf(modelo.getHoraInicio()));
            cmd.setTime("p_hora_fin", Time.valueOf(modelo.getHoraFin()));
            cmd.setBoolean("p_activo", modelo.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Horario modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El horario no puede ser nulo");
        }

        String sql = "{call modificar_horario(?,?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setString("p_dia_semana", modelo.getDiaSemana().name());
            cmd.setTime("p_hora_inicio", Time.valueOf(modelo.getHoraInicio()));
            cmd.setTime("p_hora_fin", Time.valueOf(modelo.getHoraFin()));
            cmd.setBoolean("p_activo", modelo.isActivo());
            cmd.setInt("p_id", modelo.getId());
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_horario(?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Horario modelo) throws SQLException {
        modelo.setId(cmd.getInt("p_id"));
    }
}
