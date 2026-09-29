package patitasarriba.dao.impl.atencion;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.AtencionDiagnostico;
import patitasarriba.modelo.atencion.NivelGravedad;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class AtencionDiagnosticoDAOImpl extends RegistroDAOImpl<AtencionDiagnostico>
        implements AtencionDiagnosticoDAO {

    @Override
    public void insertDiagnosticos(int idAtencionMedica, List<AtencionDiagnostico> diagnosticos) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_atencion_diagnostico(?, ?, ?, ?, ?, ?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (AtencionDiagnostico atencionDiagnostico : diagnosticos) {
                cmd.setInt("p_id_atencion_medica", idAtencionMedica);
                cmd.setInt("p_id_diagnostico", atencionDiagnostico.getDiagnostico().getId());
                cmd.setString("p_nivel_gravedad", atencionDiagnostico.getNivelGravedad().name());
                cmd.setString("p_detalle_diagnostico", atencionDiagnostico.getDetalleDiagnostico());
                cmd.setBoolean("p_activo", atencionDiagnostico.isActivo());
                cmd.registerOutParameter("p_id", Types.INTEGER);

                if (cmd.executeUpdate() == 0) {
                    throw new SQLException("No se pudo insertar el diagnostico de la atencion medica");
                }

                atencionDiagnostico.setId(cmd.getInt("p_id"));
            }
        }
    }

    @Override
    public void deleteDiagnosticos(int idAtencionMedica) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_atencion_diagnosticos_por_atencion_medica(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_atencion_medica", idAtencionMedica);
            cmd.executeUpdate();
        }
    }

    @Override
    public List<AtencionDiagnostico> findByAtencionMedicaId(int idAtencionMedica) throws SQLException {
        String sql = "{call listar_atencion_diagnosticos_por_atencion_medica(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id_atencion_medica", idAtencionMedica);
            try (ResultSet rs = cmd.executeQuery()) {
                List<AtencionDiagnostico> diagnosticos = new ArrayList<>();
                while (rs.next()) {
                    diagnosticos.add(mapear(rs, new AtencionDiagnostico()));
                }
                return diagnosticos;
            }
        }
    }

    @Override
    protected AtencionDiagnostico mapear(ResultSet rs, AtencionDiagnostico atencionDiagnostico) throws SQLException {
        super.mapear(rs, atencionDiagnostico);
        atencionDiagnostico.setId(rs.getInt("id_atencion_diagnostico"));
        atencionDiagnostico.setNivelGravedad(NivelGravedad.valueOf(rs.getString("nivel_gravedad")));
        atencionDiagnostico.setDetalleDiagnostico(rs.getString("detalle_diagnostico"));

        // Diagnostico se carga completo porque ya existe DiagnosticoDAOImpl
        atencionDiagnostico.setDiagnostico(new DiagnosticoDAOImpl().findById(rs.getInt("id_diagnostico")));

        return atencionDiagnostico;
    }
}
