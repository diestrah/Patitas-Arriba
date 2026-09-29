package patitasarriba.dao.impl.usuario;

import conexion.DBManager;
import patitasarriba.dao.AdministradorDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.usuario.Administrador;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDAOImpl extends RegistroDAOImpl<Administrador> implements AdministradorDAO {

    @Override
    public List<Administrador> findAll() throws SQLException {
        String sql = "{call listar_administradores()}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {
            List<Administrador> administradores = new ArrayList<>();
            while (rs.next()) {
                administradores.add(mapear(rs, new Administrador()));
            }
            return administradores;
        }
    }

    @Override
    public Administrador findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_administrador_por_id(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Administrador()) : null;
            }
        }
    }

    @Override
    public Administrador findByDni(String dni) throws SQLException {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El dni no puede ser nulo o vacío");
        }

        String sql = "{call buscar_administrador_por_dni(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setString("p_dni", dni);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Administrador()) : null;
            }
        }
    }

    @Override
    public void insert(Administrador administrador) throws SQLException {
        if (administrador == null) {
            throw new IllegalArgumentException("El administrador no puede ser nulo");
        }

        String sql = "{call insertar_administrador(?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, administrador);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            administrador.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(Administrador administrador) throws SQLException {
        if (administrador == null) {
            throw new IllegalArgumentException("El administrador no puede ser nulo");
        }

        String sql = "{call modificar_administrador(?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, administrador);
            cmd.setInt("p_id", administrador.getId());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo modificar el administrador");
            }
        }
    }

    // Los parametros que insertar_administrador y modificar_administrador comparten
    private void setParametrosComunes(CallableStatement cmd, Administrador administrador) throws SQLException {
        cmd.setInt("p_id_cuenta", administrador.getCuenta().getId());
        cmd.setBoolean("p_activo", administrador.isActivo());
        cmd.setString("p_dni", administrador.getDni());
        cmd.setString("p_nombres", administrador.getNombres());
        cmd.setString("p_apellido_paterno", administrador.getApellidoPaterno());
        cmd.setString("p_apellido_materno", administrador.getApellidoMaterno());
        cmd.setString("p_telefono", administrador.getTelefono());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_administrador(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar el administrador");
            }
        }
    }

    @Override
    protected Administrador mapear(ResultSet rs, Administrador administrador) throws SQLException {
        super.mapear(rs, administrador);
        administrador.setId(rs.getInt("id_administrador"));
        administrador.setDni(rs.getString("dni"));
        administrador.setNombres(rs.getString("nombres"));
        administrador.setApellidoPaterno(rs.getString("apellido_paterno"));
        administrador.setApellidoMaterno(rs.getString("apellido_materno"));
        administrador.setTelefono(rs.getString("telefono"));

        // FK obligatoria: se resuelve el objeto completo
        int idCuenta = rs.getInt("id_cuenta");
        administrador.setCuenta(new CuentaDAOImpl().findById(idCuenta));

        return administrador;
    }
}
