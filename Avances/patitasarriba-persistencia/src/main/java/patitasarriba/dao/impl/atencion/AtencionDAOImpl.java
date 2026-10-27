package patitasarriba.dao.impl.atencion;

import conexion.DBManager;
import patitasarriba.dao.AtencionDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.impl.cita.DetalleCitaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.cita.DetalleCita;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AtencionDAOImpl extends RegistroDAOImpl<Atencion> implements AtencionDAO {
    private final DetalleCitaDAOImpl detalleCitaDAO = new DetalleCitaDAOImpl();
    private final InsumoUtilizadoDAO insumoUtilizadoDAO = new InsumoUtilizadoDAOImpl();
    private final AtencionDiagnosticoDAO atencionDiagnosticoDAO = new AtencionDiagnosticoDAOImpl();

    @Override
    public List<Atencion> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_atenciones()}";

            try (CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {

                List<Atencion> atenciones = new ArrayList<>();
                while (rs.next()) {
                    atenciones.add(mapear(rs, new Atencion()));
                }
                return atenciones;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Atencion findById(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call buscar_atencion_por_id(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);
                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Atencion()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    // Inserta en la tabla maestra y orquesta sus 2 listas de detalle (insumos
    // y diagnosticos) de forma atomica: requiere que la capa de negocio ya haya
    // llamado TransactionsManager.iniciar() antes de esto.
    @Override
    public void insert(Atencion atencion) throws SQLException {
        if (atencion == null) {
            throw new IllegalArgumentException("La atencion medica no puede ser nula");
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_atencion(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_detalle_cita", atencion.getDetalleCita().getId());
            cmd.setObject("p_fecha_hora", atencion.getFechaHora());
            cmd.setString("p_motivo_consulta", atencion.getMotivoConsulta());
            cmd.setString("p_observaciones", atencion.getObservaciones());
            cmd.setBoolean("p_activo", atencion.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            atencion.setId(cmd.getInt("p_id"));
        }

        insumoUtilizadoDAO.insertInsumos(atencion.getId(), atencion.getInsumosUtilizados());
        atencionDiagnosticoDAO.insertDiagnosticos(atencion.getId(), atencion.getDiagnosticos());
    }

    @Override
    public void update(Atencion atencion) throws SQLException {
        if (atencion == null) {
            throw new IllegalArgumentException("La atencion medica no puede ser nula");
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call modificar_atencion(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_detalle_cita", atencion.getDetalleCita().getId());
            cmd.setObject("p_fecha_hora", atencion.getFechaHora());
            cmd.setString("p_motivo_consulta", atencion.getMotivoConsulta());
            cmd.setString("p_observaciones", atencion.getObservaciones());
            cmd.setBoolean("p_activo", atencion.isActivo());
            cmd.setInt("p_id", atencion.getId());

            cmd.execute();
        }

        // Eliminar detalles anteriores y reinsertar
        atencionDiagnosticoDAO.deleteDiagnosticos(atencion.getId());
        atencionDiagnosticoDAO.insertDiagnosticos(atencion.getId(), atencion.getDiagnosticos());
        insumoUtilizadoDAO.updateInsumos(atencion.getId(), atencion.getInsumosUtilizados());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        Connection conn = TransactionsManager.getConnection();

        // Primero eliminar los detalles (hijos antes que padre)
        insumoUtilizadoDAO.deleteInsumos(id);
        atencionDiagnosticoDAO.deleteDiagnosticos(id);

        String sql = "{call eliminar_atencion(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);
            cmd.execute();
        }
    }

    @Override
    protected Atencion mapear(ResultSet rs, Atencion atencion) throws SQLException {
        super.mapear(rs, atencion);
        atencion.setId(rs.getInt("id_atencion"));
        atencion.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        atencion.setMotivoConsulta(rs.getString("motivo_consulta"));
        atencion.setObservaciones(rs.getString("observaciones"));
        atencion.setDiagnosticos(atencionDiagnosticoDAO.findByAtencionId(atencion.getId()));
        atencion.setInsumosUtilizados(insumoUtilizadoDAO.findByAtencionId(atencion.getId()));

        int idDetalleCita = rs.getInt("id_detalle_cita");
        DetalleCita detalle = detalleCitaDAO.buscarDetallePorId(idDetalleCita);
        atencion.setDetalleCita(detalle);

        return atencion;
    }
}
