package patitasarriba.dao.impl.usuario;

import conexion.DBManager;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAOImpl extends RegistroDAOImpl<Veterinario> implements VeterinarioDAO {

    @Override
    public List<Veterinario> findAll() throws SQLException {
        String sql = "{call listar_veterinarios()}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {
            List<Veterinario> veterinarios = new ArrayList<>();
            while (rs.next()) {
                veterinarios.add(mapear(rs, new Veterinario()));
            }
            return veterinarios;
        }
    }

    @Override
    public Veterinario findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_veterinario_por_id(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Veterinario()) : null;
            }
        }
    }

    @Override
    public Veterinario findByDni(String dni) throws SQLException {
        if (dni == null || dni.isBlank()) {
            throw new IllegalArgumentException("El dni no puede ser nulo o vacío");
        }

        String sql = "{call buscar_veterinario_por_dni(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setString("p_dni", dni);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Veterinario()) : null;
            }
        }
    }

    @Override
    public void insert(Veterinario veterinario) throws SQLException {
        if (veterinario == null) {
            throw new IllegalArgumentException("El veterinario no puede ser nulo");
        }

        String sql = "{call insertar_veterinario(?,?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, veterinario);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();
            veterinario.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(Veterinario veterinario) throws SQLException {
        if (veterinario == null) {
            throw new IllegalArgumentException("El veterinario no puede ser nulo");
        }

        String sql = "{call modificar_veterinario(?,?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            setParametrosComunes(cmd, veterinario);
            cmd.setInt("p_id", veterinario.getId());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo modificar el veterinario");
            }
        }
    }

    // Los parametros que insertar_veterinario y modificar_veterinario comparten
    private void setParametrosComunes(CallableStatement cmd, Veterinario veterinario) throws SQLException {
        cmd.setInt("p_id_cuenta", veterinario.getCuenta().getId());
        cmd.setBoolean("p_activo", veterinario.isActivo());
        cmd.setString("p_dni", veterinario.getDni());
        cmd.setString("p_nombres", veterinario.getNombres());
        cmd.setString("p_apellido_paterno", veterinario.getApellidoPaterno());
        cmd.setString("p_apellido_materno", veterinario.getApellidoMaterno());
        cmd.setString("p_telefono", veterinario.getTelefono());
        cmd.setString("p_numero_colegiatura", veterinario.getNumeroColegiatura());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_veterinario(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar el veterinario");
            }
        }
    }

    @Override
    protected Veterinario mapear(ResultSet rs, Veterinario veterinario) throws SQLException {
        super.mapear(rs, veterinario);
        veterinario.setId(rs.getInt("id_veterinario"));
        veterinario.setDni(rs.getString("dni"));
        veterinario.setNombres(rs.getString("nombres"));
        veterinario.setApellidoPaterno(rs.getString("apellido_paterno"));
        veterinario.setApellidoMaterno(rs.getString("apellido_materno"));
        veterinario.setTelefono(rs.getString("telefono"));
        veterinario.setNumeroColegiatura(rs.getString("numero_colegiatura"));

        // FK obligatoria: se resuelve el objeto completo
        int idCuenta = rs.getInt("id_cuenta");
        veterinario.setCuenta(new CuentaDAOImpl().findById(idCuenta));

        return veterinario;
    }
}
