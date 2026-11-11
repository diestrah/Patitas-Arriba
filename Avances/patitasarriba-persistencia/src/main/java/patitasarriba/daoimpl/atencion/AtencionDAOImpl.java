package patitasarriba.daoimpl.atencion;

import patitasarriba.daoimpl.BaseDAOImpl;

import patitasarriba.dao.atencion.AtencionDAO;
import patitasarriba.daoimpl.cita.DetalleCitaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.cita.DetalleCita;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class AtencionDAOImpl extends BaseDAOImpl<Atencion> implements AtencionDAO {
    private final DetalleCitaDAOImpl detalleCitaDAO = new DetalleCitaDAOImpl();
    private final InsumoUtilizadoDAO insumoUtilizadoDAO = new InsumoUtilizadoDAOImpl();
    private final AtencionDiagnosticoDAO atencionDiagnosticoDAO = new AtencionDiagnosticoDAOImpl();

    @Override
    public Atencion findByDetalleCita(int idDetalleCita) throws SQLException {
        return ejecutarComando(conn -> {
                String sql = "{call buscar_atencion_por_detalle_cita(?)}";

                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_detalle_cita", idDetalleCita);
                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
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

        ejecutarComandoInsertar(conn, atencion);

        insumoUtilizadoDAO.insertInsumos(atencion.getId(), atencion.getInsumosUtilizados());
        atencionDiagnosticoDAO.insertDiagnosticos(atencion.getId(), atencion.getDiagnosticos());
    }

    @Override
    public void update(Atencion atencion) throws SQLException {
        if (atencion == null) {
            throw new IllegalArgumentException("La atencion medica no puede ser nula");
        }

        Connection conn = TransactionsManager.getConnection();

        ejecutarComandoActualizar(conn, atencion);

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

        ejecutarComandoEliminar(conn, id);
    }

    @Override
    protected Atencion mapear(ResultSet rs) throws SQLException {
        Atencion atencion = new Atencion();
        atencion.setActivo(rs.getBoolean("activo"));
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

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call listar_atenciones()}");
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call buscar_atencion_por_id(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Atencion modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call insertar_atencion(?, ?, ?, ?, ?, ?)}");
        try {
            cmd.setInt("p_id_detalle_cita", modelo.getDetalleCita().getId());
            cmd.setObject("p_fecha_hora", modelo.getFechaHora());
            cmd.setString("p_motivo_consulta", modelo.getMotivoConsulta());
            cmd.setString("p_observaciones", modelo.getObservaciones());
            cmd.setBoolean("p_activo", modelo.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Atencion modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call modificar_atencion(?, ?, ?, ?, ?, ?)}");
        try {
            cmd.setInt("p_id_detalle_cita", modelo.getDetalleCita().getId());
            cmd.setObject("p_fecha_hora", modelo.getFechaHora());
            cmd.setString("p_motivo_consulta", modelo.getMotivoConsulta());
            cmd.setString("p_observaciones", modelo.getObservaciones());
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
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call eliminar_atencion(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Atencion modelo) throws SQLException {
        int id = cmd.getInt("p_id");
        if (cmd.wasNull() || id <= 0) throw new SQLException("No se obtuvo un ID válido para Atencion.");
        modelo.setId(id);
    }
}
