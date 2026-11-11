package patitasarriba.daoimpl.horario;

import static patitasarriba.daoimpl.ConexionDAO.ejecutarComando;

import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.horario.Horario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HorarioAdministradorDAOImpl implements HorarioAdministradorDAO {
    @Override
    public void insertarPorAdministrador(int idAdministrador, List<Horario> horarios) throws SQLException {
        insertarPorAdministrador(TransactionsManager.getConnection(), idAdministrador, horarios);
    }

    public void insertarPorAdministrador(Connection conn, int idAdministrador, List<Horario> horarios) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call insertar_horario_administrador(?,?)}")) {
            for (Horario horario : horarios) {
                if (!horario.isActivo()) continue;
                cmd.setInt("p_id_administrador", idAdministrador);
                cmd.setInt("p_id_horario", horario.getId());
                cmd.execute();
            }
        }
    }

    @Override
    public List<Horario> listarPorAdministrador(int idAdministrador) throws SQLException {
        return ejecutarComando(conn -> {
                try (CallableStatement cmd = conn.prepareCall("{call listar_horarios_por_administrador(?)}")) {
                    cmd.setInt("p_id_administrador", idAdministrador);
                    try (ResultSet rs = cmd.executeQuery()) {
                        List<Horario> horarios = new ArrayList<>();
                        while (rs.next()) {
                            horarios.add(new HorarioDAOImpl().mapear(rs));
                        }
                        return horarios;
                    }
                }
            });
    }

    @Override
    public void eliminarPorAdministrador(int idAdministrador) throws SQLException {
        eliminarPorAdministrador(TransactionsManager.getConnection(), idAdministrador);
    }

    public void eliminarPorAdministrador(Connection conn, int idAdministrador) throws SQLException {
        try (CallableStatement cmd = conn.prepareCall("{call eliminar_horarios_por_administrador(?)}")) {
            cmd.setInt("p_id_administrador", idAdministrador);
            cmd.execute();
        }
    }
}
