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

class HorarioVeterinarioDAOImpl extends RegistroDAOImpl<HorarioPersonal>
        implements HorarioVeterinarioDAO {

    @Override
    public void insertarPorVeterinario(int idVeterinario, List<HorarioPersonal> horarios) throws SQLException {
        if (horarios == null) {
            throw new IllegalArgumentException("La lista de horarios no puede ser nula");
        }

        Connection conn = TransactionsManager.getConnection();
        String sql = "{call insertar_horario_veterinario(?,?,?,?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (HorarioPersonal horarioPersonal : horarios) {
                cmd.setInt("p_id_veterinario", idVeterinario);
                cmd.setInt("p_id_horario", horarioPersonal.getHorario().getId());
                cmd.setBoolean("p_activo", true);
                cmd.registerOutParameter("p_id", Types.INTEGER);

                if (cmd.executeUpdate() == 0) {
                    throw new SQLException("No se pudo insertar la línea de la orden de venta");
                }
                horarioPersonal.setId(cmd.getInt("p_id"));
            }
        }
    }

    @Override
    public List<HorarioPersonal> listarPorVeterinario(int idVeterinario) throws SQLException {
        String sql = "{call listar_horarios_por_veterinario(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_veterinario", idVeterinario);

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
    public void eliminarPorVeterinario(int idVeterinario) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_horarios_por_veterinario(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_veterinario", idVeterinario);
            cmd.executeUpdate();
        }
    }

    @Override
    protected HorarioPersonal mapear(ResultSet rs, HorarioPersonal horarioPersonal)
            throws SQLException {
        super.mapear(rs, horarioPersonal);
        horarioPersonal.setId(rs.getInt("ID_HORARIO_VET"));
        horarioPersonal.setHorario(
                new HorarioDAOImpl().findById(rs.getInt("ID_HORARIO")));
        return horarioPersonal;
    }
}
