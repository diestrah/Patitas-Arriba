package patitasarriba.dao.impl.usuario;

import patitasarriba.dao.transacciones.TransactionsManager;

import conexion.DBManager;
import patitasarriba.dao.CuentaDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.usuario.Cuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuentaDAOImpl extends RegistroDAOImpl<Cuenta> implements CuentaDAO {

    @Override
    public List<Cuenta> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_cuentas()}";
            try (CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {
                List<Cuenta> cuentas = new ArrayList<>();
                while (rs.next()) {
                    cuentas.add(mapear(rs, new Cuenta()));
                }
                return cuentas;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Cuenta findById(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call buscar_cuenta_por_id(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);

                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Cuenta()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Cuenta findByNombreUsuario(String nombreUsuario) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (nombreUsuario == null || nombreUsuario.isBlank()) {
                throw new IllegalArgumentException("El nombre de usuario no puede ser nulo o vacío");
            }

            String sql = "{call buscar_cuenta_por_nombre_usuario(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setString("p_nombre_usuario", nombreUsuario);

                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Cuenta()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void insert(Cuenta cuenta) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (cuenta == null) {
                throw new IllegalArgumentException("La cuenta no puede ser nula");
            }

            String sql = "{call insertar_cuenta(?,?,?,?,?,?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                setParametrosComunes(cmd, cuenta);
                cmd.registerOutParameter("p_id", Types.INTEGER);

                cmd.execute();
                cuenta.setId(cmd.getInt("p_id"));
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void update(Cuenta cuenta) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (cuenta == null) {
                throw new IllegalArgumentException("La cuenta no puede ser nula");
            }

            String sql = "{call modificar_cuenta(?,?,?,?,?,?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                setParametrosComunes(cmd, cuenta);
                cmd.setInt("p_id", cuenta.getId());

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
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
    public void delete(Integer id) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call eliminar_cuenta(?)}";
            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    protected Cuenta mapear(ResultSet rs, Cuenta cuenta) throws SQLException {
        super.mapear(rs, cuenta);
        cuenta.setId(rs.getInt("id_cuenta"));
        cuenta.setPassword(rs.getString("password"));
        cuenta.setCorreo(rs.getString("correo"));
        cuenta.setFechaCreacion(rs.getDate("fecha_creacion").toLocalDate());
        cuenta.setNombreUsuario(rs.getString("nombre_usuario"));
        return cuenta;
    }
}
