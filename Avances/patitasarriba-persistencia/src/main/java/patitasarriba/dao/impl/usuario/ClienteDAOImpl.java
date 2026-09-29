package patitasarriba.dao.impl.usuario;

import conexion.DBManager;
import patitasarriba.dao.ClienteDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.usuario.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl extends RegistroDAOImpl<Cliente> implements ClienteDAO {

    @Override
    public List<Cliente> findAll() throws SQLException {
        String sql = "{call listar_clientes()}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {
            List<Cliente> clientes = new ArrayList<>();
            while (rs.next()) {
                clientes.add(mapear(rs, new Cliente()));
            }
            return clientes;
        }
    }

    @Override
    public Cliente findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_cliente_por_id(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Cliente()) : null;
            }
        }
    }

    @Override
    public Cliente findByDni(String dni) throws SQLException {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El dni no puede ser nulo o vacío");
        }

        String sql = "{call buscar_cliente_por_dni(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setString("p_dni", dni);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Cliente()) : null;
            }
        }
    }

    @Override
    public void insert(Cliente cliente) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        String sql = "{call insertar_cliente(?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, cliente);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            cliente.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(Cliente cliente) throws SQLException {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        String sql = "{call modificar_cliente(?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, cliente);
            cmd.setInt("p_id", cliente.getId());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo modificar el cliente");
            }
        }
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
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_cliente(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar el cliente");
            }
        }
    }

    @Override
    protected Cliente mapear(ResultSet rs, Cliente cliente) throws SQLException {
        super.mapear(rs, cliente);
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
}
