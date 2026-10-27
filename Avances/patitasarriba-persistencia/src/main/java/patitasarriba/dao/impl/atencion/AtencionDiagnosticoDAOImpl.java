package patitasarriba.dao.impl.atencion;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.AtencionDiagnostico;
import patitasarriba.modelo.atencion.Diagnostico;
import patitasarriba.modelo.atencion.NivelGravedad;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class AtencionDiagnosticoDAOImpl
extends RegistroDAOImpl<AtencionDiagnostico>
implements AtencionDiagnosticoDAO {

    @Override
    public void insertDiagnosticos(int idAtencion, List<AtencionDiagnostico> diagnosticos) throws SQLException {

        Connection conn = TransactionsManager.getConnection();
        String sql = "{call insertar_atencion_diagnostico(?,?,?,?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (AtencionDiagnostico detalle : diagnosticos) {
                if (!detalle.isActivo()) {
                    continue;
                }

                cmd.setInt("p_id_atencion", idAtencion);
                cmd.setInt("p_id_diagnostico", detalle.getDiagnostico().getId());
                cmd.setString("p_nivel_gravedad", detalle.getNivelGravedad().name());
                cmd.setString("p_detalle_diagnostico",detalle.getDetalleDiagnostico());

                cmd.execute();
            }
        }
    }

    @Override
    public void deleteDiagnosticos(int idAtencion) throws SQLException {
        Connection conn = TransactionsManager.getConnection();
        String sql = "{call eliminar_diagnosticos_por_atencion(?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_atencion", idAtencion);
            cmd.execute();
        }
    }

    @Override
    public List<AtencionDiagnostico> findByAtencionId(
        int idAtencion
    ) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_diagnosticos_por_atencion(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setInt("p_id_atencion", idAtencion);
                try (ResultSet rs = cmd.executeQuery()) {
                    List<AtencionDiagnostico> diagnosticos = new ArrayList<>();
                    while (rs.next()) {
                        diagnosticos.add(mapear(rs, new AtencionDiagnostico()));
                    }
                    return diagnosticos;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    protected AtencionDiagnostico mapear(ResultSet rs,AtencionDiagnostico detalle) throws SQLException {
        super.mapear(rs, detalle);

        Atencion atencion = new Atencion();
        atencion.setId(rs.getInt("id_atencion"));
        detalle.setAtencion(atencion);

        Diagnostico diagnostico = new DiagnosticoDAOImpl().findById(rs.getInt("id_diagnostico"));

        if (diagnostico == null) {
            throw new SQLException("No se encontró el diagnóstico asociado a la atención");
        }

        detalle.setDiagnostico(diagnostico);
        detalle.setNivelGravedad(NivelGravedad.valueOf(rs.getString("nivel_gravedad")));
        detalle.setDetalleDiagnostico(rs.getString("detalle_diagnostico"));

        return detalle;
    }
}
