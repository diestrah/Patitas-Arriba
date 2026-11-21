package patitasarriba.daoimpl.usuario;

import patitasarriba.dao.usuario.ClienteDAO;
import patitasarriba.daoimpl.BaseDAOImpl;
import patitasarriba.modelo.usuario.Cliente;

import java.sql.*;

public class ClienteDAOImpl extends BaseDAOImpl<Cliente> implements ClienteDAO {

    @Override
    public Cliente findByDni(String dni) throws SQLException {
        return ejecutarComando(conn -> {
                if (dni == null || dni.isBlank()) {
                    throw new IllegalArgumentException("El dni no puede ser nulo o vacío");
                }

                String sql = "{call buscar_cliente_por_dni(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_dni", dni);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public Cliente findByCuenta(Integer idCuenta) throws SQLException {
        return ejecutarComando(conn -> {
                if (idCuenta == null) {
                    throw new IllegalArgumentException("El id de la cuenta no puede ser nulo");
                }

                String sql = "{call buscar_cliente_por_cuenta(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_cuenta", idCuenta);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    // Los parametros que insertar_cliente y modificar_cliente comparten
    private void setParametrosComunes(CallableStatement cmd, Cliente cliente) throws SQLException {
        cmd.setInt("p_id_cuenta", cliente.getCuenta().getId());
        cmd.setBoolean("p_activo", cliente.isActivo());
        cmd.setString("p_dni", cliente.getDni());
        cmd.setString("p_nombres", cliente.getNombres());
        cmd.setString("p_apellido_paterno", cliente.getApellidoPaterno());
        cmd.setString("p_apellido_materno", cliente.getApellidoMaterno());
        cmd.setString("p_telefono", cliente.getTelefono());
    }

    @Override
    protected Cliente mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setActivo(rs.getBoolean("activo"));
        cliente.setId(rs.getInt("id_cliente"));
        cliente.setDni(rs.getString("dni"));
        cliente.setNombres(rs.getString("nombres"));
        cliente.setApellidoPaterno(rs.getString("apellido_paterno"));
        cliente.setApellidoMaterno(rs.getString("apellido_materno"));
        cliente.setTelefono(rs.getString("telefono"));

        // FK obligatoria: se resuelve el objeto completo
        int idCuenta = rs.getInt("id_cuenta");
        cliente.setCuenta(new CuentaDAOImpl().findById(idCuenta));

        return cliente;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        String sql = "{call listar_clientes()}";
        CallableStatement cmd = conn.prepareCall(sql);
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_cliente_por_id(?)}";
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
    protected CallableStatement comandoInsertar(Connection conn, Cliente modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        String sql = "{call insertar_cliente(?,?,?,?,?,?,?,?)}";
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
    protected CallableStatement comandoActualizar(Connection conn, Cliente modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        String sql = "{call modificar_cliente(?,?,?,?,?,?,?,?)}";
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

        String sql = "{call eliminar_cliente(?)}";
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
    protected void procesarResultadoInsertar(CallableStatement cmd, Cliente modelo) throws SQLException {
        modelo.setId(cmd.getInt("p_id"));
    }
}
