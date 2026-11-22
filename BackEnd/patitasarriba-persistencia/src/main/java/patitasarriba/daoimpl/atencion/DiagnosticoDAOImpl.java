package patitasarriba.daoimpl.atencion;

import patitasarriba.dao.atencion.DiagnosticoDAO;
import patitasarriba.daoimpl.BaseDAOImpl;
import patitasarriba.modelo.atencion.Diagnostico;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class DiagnosticoDAOImpl extends BaseDAOImpl<Diagnostico> implements DiagnosticoDAO {

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Diagnostico modelo) throws SQLException {

        if (modelo == null) {
            throw new IllegalArgumentException("El diagnostico no puede ser nulo");
        }

        String sql = "{call insertar_diagnostico(?, ?, ?, ?)}";
        CallableStatement cmd = conn.prepareCall(sql);

        try {
            cmd.setString("p_nombre_enfermedad", modelo.getNombreEnfermedad());
            cmd.setString("p_descripcion", modelo.getDescripcion());
            cmd.setBoolean("p_activo", modelo.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);

            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try {
                cmd.close();
            } catch (SQLException cierre) {
                ex.addSuppressed(cierre);
            }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Diagnostico modelo) throws SQLException {

        if (modelo == null) {
            throw new IllegalArgumentException("El diagnostico no puede ser nulo");
        }

        String sql = "{call modificar_diagnostico(?, ?, ?, ?)}";
        CallableStatement cmd = conn.prepareCall(sql);

        try {
            cmd.setInt("p_id", modelo.getId());
            cmd.setString("p_nombre_enfermedad", modelo.getNombreEnfermedad());
            cmd.setString("p_descripcion", modelo.getDescripcion());
            cmd.setBoolean("p_activo", modelo.isActivo());

            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try {
                cmd.close();
            } catch (SQLException cierre) {
                ex.addSuppressed(cierre);
            }

            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException {

        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_diagnostico(?)}";
        CallableStatement cmd = conn.prepareCall(sql);

        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try {
                cmd.close();
            } catch (SQLException cierre) {
                ex.addSuppressed(cierre);
            }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {

        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_diagnostico_por_id(?)}";
        CallableStatement cmd = conn.prepareCall(sql);

        try {
            cmd.setInt("p_id", id);

            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try {
                cmd.close();
            } catch (SQLException cierre) {
                ex.addSuppressed(cierre);
            }

            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {

        String sql = "{call listar_diagnosticos()}";
        return conn.prepareCall(sql);
    }

    @Override
    protected void procesarResultadoInsertar(
        CallableStatement cmd, Diagnostico modelo
    ) throws SQLException {

        int id = cmd.getInt("p_id");

        if (cmd.wasNull() || id <= 0) {
            throw new SQLException("No se obtuvo un ID valido para el diagnostico");
        }

        modelo.setId(id);
    }

    @Override
    public Diagnostico findByNombreEnfermedad(String nombreEnfermedad)
    throws SQLException {

        if (nombreEnfermedad == null || nombreEnfermedad.isBlank()) {
            throw new IllegalArgumentException("El nombre de la enfermedad no puede ser nulo o vacio");
        }

        return ejecutarComando(conn -> {
                String sql = "{call buscar_diagnostico_por_nombre_enfermedad(?)}";

                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_nombre_enfermedad", nombreEnfermedad);

                    try (ResultSet rs = cmd.executeQuery()) {
                        if (!rs.next()) {
                            return null;
                        }

                        return mapear(rs);
                    }
                }
            });
    }

    @Override
    protected Diagnostico mapear(ResultSet rs) throws SQLException {
        Diagnostico diagnostico = new Diagnostico();

        diagnostico.setActivo(rs.getBoolean("activo"));
        diagnostico.setId(rs.getInt("id_diagnostico"));
        diagnostico.setNombreEnfermedad(rs.getString("nombre_enfermedad"));
        diagnostico.setDescripcion(rs.getString("descripcion"));

        return diagnostico;
    }
}
