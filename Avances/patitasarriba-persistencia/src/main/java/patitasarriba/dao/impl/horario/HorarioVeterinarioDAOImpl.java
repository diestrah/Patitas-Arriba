package patitasarriba.dao.impl.horario;

import conexion.DBManager;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.horario.Horario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HorarioVeterinarioDAOImpl implements HorarioVeterinarioDAO {
    @Override
    public void insertarPorVeterinario(int idVeterinario, List<Horario> horarios) throws SQLException {
        insertarPorVeterinario(TransactionsManager.getConnection(), idVeterinario, horarios);
    }

    public void insertarPorVeterinario(Connection conn, int idVeterinario, List<Horario> horarios) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call insertar_horario_veterinario(?,?)}")) {
            for (Horario horario : horarios) {
                if (!horario.isActivo()) continue;
                cmd.setInt("p_id_veterinario", idVeterinario);
                cmd.setInt("p_id_horario", horario.getId());
                cmd.execute();
            }
        }
    }

    @Override
    public List<Horario> listarPorVeterinario(int idVeterinario) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            try (CallableStatement cmd = conn.prepareCall("{call listar_horarios_por_veterinario(?)}")) {
                cmd.setInt("p_id_veterinario", idVeterinario);
                try (ResultSet rs = cmd.executeQuery()) {
                    List<Horario> horarios = new ArrayList<>();
                    while (rs.next()) {
                        horarios.add(new HorarioDAOImpl().mapear(rs, new Horario()));
                    }
                    return horarios;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void eliminarPorVeterinario(int idVeterinario) throws SQLException {
        eliminarPorVeterinario(TransactionsManager.getConnection(), idVeterinario);
    }

    public void eliminarPorVeterinario(Connection conn, int idVeterinario) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call eliminar_horarios_por_veterinario(?)}")) {
            cmd.setInt("p_id_veterinario", idVeterinario);
            cmd.execute();
        }
    }
}
