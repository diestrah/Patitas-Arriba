package patitasarriba.daoimpl.usuario;

import patitasarriba.dao.usuario.CuentaDAO;
import patitasarriba.daoimpl.BaseDAOImpl;
import patitasarriba.modelo.usuario.Cuenta;

import java.sql.*;

public class CuentaDAOImpl extends BaseDAOImpl<Cuenta> implements CuentaDAO {

    @Override
    public Cuenta findByCorreo(String correo) throws SQLException {
        return ejecutarComando(conn -> {
                if (correo == null || correo.isBlank()) {
                    throw new IllegalArgumentException("El correo no puede ser nulo o vacío");
                }

                String sql = "{call buscar_cuenta_por_correo(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_correo", correo.trim());

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public Cuenta findByNombreUsuario(String nombreUsuario) throws SQLException {
        return ejecutarComando(conn -> {
                if (nombreUsuario == null || nombreUsuario.isBlank()) {
                    throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
                }

                String sql = "{call buscar_cuenta_por_nombre_usuario(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_nombre_usuario", nombreUsuario);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    // Los parametros que insertar_cuenta y modificar_cuenta comparten
    private void setParametrosComunes(CallableStatement cmd, Cuenta cuenta) throws SQLException {
        cmd.setBoolean("p_activo", cuenta.isActivo());
        cmd.setString("p_password", cuenta.getPassword());
        cmd.setString("p_correo", cuenta.getCorreo());
        cmd.setDate("p_fecha_creacion", Date.valueOf(cuenta.getFechaCreacion()));
        cmd.setString("p_nombre_usuario", cuenta.getNombreUsuario());
    }

    @Override
    protected Cuenta mapear(ResultSet rs) throws SQLException {
        Cuenta cuenta = new Cuenta();
        cuenta.setActivo(rs.getBoolean("activo"));
        cuenta.setId(rs.getInt("id_cuenta"));
        cuenta.setPassword(rs.getString("password"));
        cuenta.setCorreo(rs.getString("correo"));
        cuenta.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
        cuenta.setNombreUsuario(rs.getString("nombre_usuario"));
        return cuenta;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        String sql = "{call listar_cuentas()}";
        CallableStatement cmd = conn.prepareCall(sql);
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_cuenta_por_id(?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Cuenta modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }

        String sql = "{call insertar_cuenta(?,?,?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            setParametrosComunes(cmd, modelo);
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Cuenta modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("La cuenta no puede ser nula");
        }

        String sql = "{call modificar_cuenta(?,?,?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            setParametrosComunes(cmd, modelo);
            cmd.setInt("p_id", modelo.getId());
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_cuenta(?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Cuenta modelo) throws SQLException {
        modelo.setId(cmd.getInt("p_id"));
    }
}
