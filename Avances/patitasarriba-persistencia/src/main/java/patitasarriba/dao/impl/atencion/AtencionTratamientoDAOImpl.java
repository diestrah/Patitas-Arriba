package patitasarriba.dao.impl.atencion;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.AtencionTratamiento;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class AtencionTratamientoDAOImpl extends RegistroDAOImpl<AtencionTratamiento> implements AtencionTratamientoDAO {

    private final InsumoUtilizadoDAO insumoUtilizadoDAO = new InsumoUtilizadoDAOImpl();

    @Override
    public void insertTratamientos(int idAtencionMedica, List<AtencionTratamiento> tratamientos) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_atencion_tratamiento(?, ?, ?, ?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (AtencionTratamiento atencionTratamiento : tratamientos) {
                cmd.setInt("p_id_atencion_medica", idAtencionMedica);
                cmd.setInt("p_id_tratamiento", atencionTratamiento.getTratamiento().getId());
                cmd.setBoolean("p_activo", atencionTratamiento.isActivo());
                cmd.registerOutParameter("p_id", Types.INTEGER);

                if (cmd.executeUpdate() == 0) {
                    throw new SQLException("No se pudo insertar el tratamiento de la atencion medica");
                }

                atencionTratamiento.setId(cmd.getInt("p_id"));

                // Cada tratamiento orquesta la insercion de sus propios insumos
                insumoUtilizadoDAO.insertInsumos(atencionTratamiento.getId(), atencionTratamiento.getInsumosUtilizados());
            }
        }
    }

    @Override
    public void deleteTratamientos(int idAtencionMedica) throws SQLException {
        // Los insumos de cada tratamiento se eliminan antes que los tratamientos (hijos antes que padre)
        for (AtencionTratamiento atencionTratamiento : findByAtencionMedicaId(idAtencionMedica)) {
            insumoUtilizadoDAO.deleteInsumos(atencionTratamiento.getId());
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_atencion_tratamientos_por_atencion_medica(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_atencion_medica", idAtencionMedica);
            cmd.executeUpdate();
        }
    }

    @Override
    public List<AtencionTratamiento> findByAtencionMedicaId(int idAtencionMedica) throws SQLException {
        String sql = "{call listar_atencion_tratamientos_por_atencion_medica(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id_atencion_medica", idAtencionMedica);
            try (ResultSet rs = cmd.executeQuery()) {
                List<AtencionTratamiento> tratamientos = new ArrayList<>();
                while (rs.next()) {
                    tratamientos.add(mapear(rs, new AtencionTratamiento()));
                }
                return tratamientos;
            }
        }
    }

    @Override
    protected AtencionTratamiento mapear(ResultSet rs, AtencionTratamiento atencionTratamiento) throws SQLException {
        super.mapear(rs, atencionTratamiento);
        atencionTratamiento.setId(rs.getInt("id_atencion_tratamiento"));

        // Tratamiento se carga completo porque ya existe TratamientoDAOImpl
        atencionTratamiento.setTratamiento(new TratamientoDAOImpl().findById(rs.getInt("id_tratamiento")));

        atencionTratamiento.setInsumosUtilizados(
                insumoUtilizadoDAO.findByAtencionTratamientoId(atencionTratamiento.getId()));

        return atencionTratamiento;
    }
}
