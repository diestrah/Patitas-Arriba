package patitasarriba.dao.impl.atencion;

import conexion.DBManager;
import patitasarriba.dao.AtencionMedicaDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.AtencionMedica;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class AtencionMedicaDAOImpl extends RegistroDAOImpl<AtencionMedica> implements AtencionMedicaDAO {

    private final AtencionTratamientoDAO atencionTratamientoDAO = new AtencionTratamientoDAOImpl();
    private final AtencionDiagnosticoDAO atencionDiagnosticoDAO = new AtencionDiagnosticoDAOImpl();

    @Override
    public List<AtencionMedica> findAll() throws SQLException {
        String sql = "{call listar_atenciones_medicas()}";

        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {

            List<AtencionMedica> atenciones = new ArrayList<>();
            while (rs.next()) {
                atenciones.add(mapear(rs, new AtencionMedica()));
            }
            return atenciones;
        }
    }

    @Override
    public AtencionMedica findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_atencion_medica_por_id(?)}";

        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);
            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new AtencionMedica()) : null;
            }
        }
    }

    // Inserta en la tabla maestra y orquesta sus 2 listas de detalle (tratamientos
    // y diagnosticos) de forma atomica: requiere que la capa de negocio ya haya
    // llamado TransactionsManager.iniciar() antes de esto.
    @Override
    public void insert(AtencionMedica atencionMedica) throws SQLException {
        if (atencionMedica == null) {
            throw new IllegalArgumentException("La atencion medica no puede ser nula");
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_atencion_medica(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_veterinario", atencionMedica.getVeterinario().getId());
            cmd.setObject("p_fecha_hora", atencionMedica.getFechaHora());
            cmd.setString("p_motivo_consulta", atencionMedica.getMotivoConsulta());
            cmd.setDouble("p_peso_fisico", atencionMedica.getPesoFisico());
            cmd.setString("p_observaciones", atencionMedica.getObservaciones());
            cmd.setBoolean("p_activo", atencionMedica.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo insertar la atencion medica");
            }

            atencionMedica.setId(cmd.getInt("p_id"));
        }

        atencionTratamientoDAO.insertTratamientos(atencionMedica.getId(), atencionMedica.getTratamientos());
        atencionDiagnosticoDAO.insertDiagnosticos(atencionMedica.getId(), atencionMedica.getDiagnosticos());
    }

    @Override
    public void update(AtencionMedica atencionMedica) throws SQLException {
        if (atencionMedica == null) {
            throw new IllegalArgumentException("La atencion medica no puede ser nula");
        }

        Connection conn = TransactionsManager.getConnection();

        String sql = "{call actualizar_atencion_medica(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_veterinario", atencionMedica.getVeterinario().getId());
            cmd.setObject("p_fecha_hora", atencionMedica.getFechaHora());
            cmd.setString("p_motivo_consulta", atencionMedica.getMotivoConsulta());
            cmd.setDouble("p_peso_fisico", atencionMedica.getPesoFisico());
            cmd.setString("p_observaciones", atencionMedica.getObservaciones());
            cmd.setBoolean("p_activo", atencionMedica.isActivo());
            cmd.setInt("p_id", atencionMedica.getId());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo actualizar la atencion medica");
            }
        }

        // Eliminar detalles anteriores y reinsertar
        atencionTratamientoDAO.deleteTratamientos(atencionMedica.getId());
        atencionTratamientoDAO.insertTratamientos(atencionMedica.getId(), atencionMedica.getTratamientos());

        atencionDiagnosticoDAO.deleteDiagnosticos(atencionMedica.getId());
        atencionDiagnosticoDAO.insertDiagnosticos(atencionMedica.getId(), atencionMedica.getDiagnosticos());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        Connection conn = TransactionsManager.getConnection();

        // Primero eliminar los detalles (hijos antes que padre)
        atencionTratamientoDAO.deleteTratamientos(id);
        atencionDiagnosticoDAO.deleteDiagnosticos(id);

        String sql = "{call eliminar_atencion_medica(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);
            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar la atencion medica");
            }
        }
    }

    @Override
    protected AtencionMedica mapear(ResultSet rs, AtencionMedica atencionMedica) throws SQLException {
        super.mapear(rs, atencionMedica);
        atencionMedica.setId(rs.getInt("id_atencion_medica"));
        atencionMedica.setFechaHora(rs.getTimestamp("fecha_hora").toLocalDateTime());
        atencionMedica.setMotivoConsulta(rs.getString("motivo_consulta"));
        atencionMedica.setPesoFisico(rs.getDouble("peso_fisico"));
        atencionMedica.setObservaciones(rs.getString("observaciones"));

        // Veterinario no es parte de este modulo (no existe VeterinarioDAOImpl), asi
        // que se carga solo con el id; se asume la columna id_veterinario en
        // ATENCION_MEDICA porque el modelo la requiere.
        Veterinario veterinario = new Veterinario();
        veterinario.setId(rs.getInt("id_veterinario"));
        atencionMedica.setVeterinario(veterinario);

        atencionMedica.setTratamientos(atencionTratamientoDAO.findByAtencionMedicaId(atencionMedica.getId()));
        atencionMedica.setDiagnosticos(atencionDiagnosticoDAO.findByAtencionMedicaId(atencionMedica.getId()));

        return atencionMedica;
    }
}
