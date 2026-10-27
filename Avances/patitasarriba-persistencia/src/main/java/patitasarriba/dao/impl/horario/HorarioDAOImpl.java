package patitasarriba.dao.impl.horario;

import patitasarriba.dao.transacciones.TransactionsManager;

import conexion.DBManager;
import patitasarriba.dao.HorarioDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.horario.DiaSemana;
import patitasarriba.modelo.horario.Horario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class HorarioDAOImpl extends RegistroDAOImpl<Horario> implements HorarioDAO {

    @Override
    public List<Horario> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_horarios()}";
            try (CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {
                List<Horario> horarios = new ArrayList<>();
                while (rs.next()) {
                    horarios.add(mapear(rs, new Horario()));
                }
                return horarios;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Horario findById(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call buscar_horario_por_id(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);

                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Horario()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void insert(Horario horario) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (horario == null) {
                throw new IllegalArgumentException("El horario no puede ser nulo");
            }

            String sql = "{call insertar_horario(?,?,?,?,?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setString("p_dia_semana", horario.getDiaSemana().name());
                cmd.setTime("p_hora_inicio", Time.valueOf(horario.getHoraInicio()));
                cmd.setTime("p_hora_fin", Time.valueOf(horario.getHoraFin()));
                cmd.setBoolean("p_activo", horario.isActivo());
                cmd.registerOutParameter("p_id", Types.INTEGER);

                cmd.execute();

                horario.setId(cmd.getInt("p_id"));
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void update(Horario horario) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (horario == null) {
                throw new IllegalArgumentException("El horario no puede ser nulo");
            }

            String sql = "{call modificar_horario(?,?,?,?,?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setString("p_dia_semana", horario.getDiaSemana().name());
                cmd.setTime("p_hora_inicio", Time.valueOf(horario.getHoraInicio()));
                cmd.setTime("p_hora_fin", Time.valueOf(horario.getHoraFin()));
                cmd.setBoolean("p_activo", horario.isActivo());
                cmd.setInt("p_id", horario.getId());

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call eliminar_horario(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    protected Horario mapear(ResultSet rs, Horario horario) throws SQLException {
        super.mapear(rs, horario);
        horario.setId(rs.getInt("ID_HORARIO"));
        horario.setDiaSemana(DiaSemana.valueOf(rs.getString("DIA_SEMANA")));
        horario.setHoraInicio(rs.getTime("HORA_INICIO").toLocalTime());
        horario.setHoraFin(rs.getTime("HORA_FIN").toLocalTime());
        return horario;
    }

}
