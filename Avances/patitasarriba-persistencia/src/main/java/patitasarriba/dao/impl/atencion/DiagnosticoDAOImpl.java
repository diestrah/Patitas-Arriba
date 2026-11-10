package patitasarriba.dao.impl.atencion;

import patitasarriba.dao.transacciones.TransactionsManager;

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
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_diagnosticos()}";

            try (CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {

                List<Diagnostico> diagnosticos = new ArrayList<>();
                while (rs.next()) {
                    diagnosticos.add(mapear(rs, new Diagnostico()));
                }
                return diagnosticos;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Diagnostico findById(Integer integer) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (integer == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call buscar_diagnostico_por_id(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setInt("p_id", integer);

                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Diagnostico()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void insert(Diagnostico modelo) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (modelo == null) {
                throw new IllegalArgumentException("El diagnostico no puede ser nulo");
            }

            String sql = "{call insertar_diagnostico(?, ?, ?, ?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setString("p_nombre_enfermedad", modelo.getNombreEnfermedad());
                cmd.setString("p_descripcion", modelo.getDescripcion());
                cmd.setBoolean("p_activo", modelo.isActivo());
                cmd.registerOutParameter("p_id", Types.INTEGER);

                cmd.execute();

                modelo.setId(cmd.getInt("p_id"));
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void update(Diagnostico modelo) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (modelo == null) {
                throw new IllegalArgumentException("El diagnostico no puede ser nulo");
            }

            String sql = "{call modificar_diagnostico(?, ?, ?, ?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setInt("p_id", modelo.getId());
                cmd.setString("p_nombre_enfermedad", modelo.getNombreEnfermedad());
                cmd.setString("p_descripcion", modelo.getDescripcion());
                cmd.setBoolean("p_activo", modelo.isActivo());

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void delete(Integer integer) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (integer == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call eliminar_diagnostico(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setInt("p_id", integer);
                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Diagnostico findByNombreEnfermedad(String nombreEnfermedad) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (nombreEnfermedad == null) {
                throw new IllegalArgumentException("El nombre de la enfermedad no puede ser nulo");
            }

            String sql = "{call buscar_diagnostico_por_nombre_enfermedad(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setString("p_nombre_enfermedad", nombreEnfermedad);

                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Diagnostico()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
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
