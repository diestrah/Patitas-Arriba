package patitasarriba.dao.impl.atencion;

import conexion.DBManager;
import patitasarriba.dao.DiagnosticoDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.atencion.Diagnostico;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticoDAOImpl extends RegistroDAOImpl<Diagnostico> implements DiagnosticoDAO {

    @Override
    public List<Diagnostico> findAll() throws SQLException {
        String sql = "{call listar_diagnosticos()}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {

            List<Diagnostico> diagnosticos = new ArrayList<>();
            while (rs.next()) {
                diagnosticos.add(mapear(rs, new Diagnostico()));
            }
            return diagnosticos;
        }
    }

    @Override
    public Diagnostico findById(Integer integer) throws SQLException {
        if (integer == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_diagnostico_por_id(?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id", integer);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Diagnostico()) : null;
            }
        }
    }

    @Override
    public void insert(Diagnostico modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El diagnostico no puede ser nulo");
        }

        String sql = "{call insertar_diagnostico(?, ?, ?, ?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setString("p_nombre_enfermedad", modelo.getNombreEnfermedad());
            cmd.setString("p_descripcion", modelo.getDescripcion());
            cmd.setInt("p_estado", modelo.isActivo() ? 1 : 0);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo insertar el diagnostico");
            }

            modelo.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(Diagnostico modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El diagnostico no puede ser nulo");
        }

        String sql = "{call actualizar_diagnostico(?, ?, ?, ?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id", modelo.getId());
            cmd.setString("p_nombre_enfermedad", modelo.getNombreEnfermedad());
            cmd.setString("p_descripcion", modelo.getDescripcion());
            cmd.setInt("p_estado", modelo.isActivo() ? 1 : 0);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo actualizar el diagnostico");
            }
        }
    }

    @Override
    public void delete(Integer integer) throws SQLException {
        if (integer == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_diagnostico(?)}";
        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id", integer);
            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar el diagnostico");
            }
        }
    }

    @Override
    public Diagnostico findByNombreEnfermedad(String nombreEnfermedad) throws SQLException {
        if (nombreEnfermedad == null) {
            throw new IllegalArgumentException("El nombre de la enfermedad no puede ser nulo");
        }

        String sql = "{call buscar_diagnostico_por_nombre_enfermedad(?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setString("p_nombre_enfermedad", nombreEnfermedad);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Diagnostico()) : null;
            }
        }
    }

    @Override
    protected Diagnostico mapear(ResultSet rs, Diagnostico diagnostico) throws SQLException {
        super.mapear(rs, diagnostico);
        diagnostico.setId(rs.getInt("id_diagnostico"));
        diagnostico.setNombreEnfermedad(rs.getString("nombre_enfermedad"));
        diagnostico.setDescripcion(rs.getString("descripcion"));
        return diagnostico;
    }
}
