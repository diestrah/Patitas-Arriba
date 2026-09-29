package patitasarriba.dao.impl.mascota;

import conexion.DBManager;
import patitasarriba.dao.MascotaDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.impl.usuario.ClienteDAOImpl;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.modelo.mascota.SexoMascota;
import patitasarriba.modelo.mascota.TipoMascota;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class MascotaDAOImpl extends RegistroDAOImpl<Mascota> implements MascotaDAO {

    @Override
    public List<Mascota> findAll() throws SQLException {
        String sql = "{call listar_mascotas()}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {
            List<Mascota> mascotas = new ArrayList<>();
            while (rs.next()) {
                mascotas.add(mapear(rs, new Mascota()));
            }
            return mascotas;
        }
    }

    @Override
    public Mascota findById(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_mascota_por_id(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Mascota()) : null;
            }
        }
    }

    @Override
    public void insert(Mascota mascota) throws SQLException {
        if (mascota == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula");
        }

        String sql = "{call insertar_mascota(?,?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cliente", mascota.getCliente().getId());
            cmd.setString("p_nombre", mascota.getNombre());
            cmd.setString("p_sexo", mapearSexoABase(mascota.getSexo()));
            cmd.setDouble("p_peso", mascota.getPeso());
            cmd.setDate("p_fecha_nacimiento", Date.valueOf(mascota.getFechaNacimiento()));
            cmd.setString("p_tipo_mascota", mascota.getTipoMascota().name());
            cmd.setString("p_raza", mascota.getRaza());
            cmd.setBoolean("p_activo", mascota.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);

            if(cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo insertar la mascota");
            }

            mascota.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(Mascota mascota) throws SQLException {
        if (mascota == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula");
        }

        String sql = "{call modificar_mascota(?,?,?,?,?,?,?,?,?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cliente", mascota.getCliente().getId());
            cmd.setString("p_nombre", mascota.getNombre());
            cmd.setString("p_sexo", mapearSexoABase(mascota.getSexo()));
            cmd.setDouble("p_peso", mascota.getPeso());
            cmd.setDate("p_fecha_nacimiento", Date.valueOf(mascota.getFechaNacimiento()));
            cmd.setString("p_tipo_mascota", mascota.getTipoMascota().name());
            cmd.setString("p_raza", mascota.getRaza());
            cmd.setBoolean("p_activo", mascota.isActivo());
            cmd.setInt("p_id", mascota.getId());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo modificar la mascota");
            }
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_mascota(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar la mascota");
            }
        }
    }

    @Override
    public List<Mascota> findByNombre(String nombre) throws SQLException {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
        }

        String sql = "{call buscar_mascota_por_nombre(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setString("p_nombre", nombre);

            try (ResultSet rs = cmd.executeQuery()) {
                List<Mascota> mascotas = new ArrayList<>();
                while (rs.next()) {
                    mascotas.add(mapear(rs, new Mascota()));
                }
                return mascotas;
            }
        }
    }

    @Override
    public List<Mascota> listarPorCliente(Integer idCliente) throws SQLException {
        validarIdCliente(idCliente);

        String sql = "{call listar_mascotas_por_cliente(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cliente", idCliente);

            try (ResultSet rs = cmd.executeQuery()) {
                List<Mascota> mascotas = new ArrayList<>();
                while (rs.next()) {
                    mascotas.add(mapear(rs, new Mascota()));
                }
                return mascotas;
            }
        }
    }

    @Override
    public void eliminarPorCliente(Integer idCliente) throws SQLException {
        validarIdCliente(idCliente);

        String sql = "{call eliminar_mascotas_por_cliente(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cliente", idCliente);
            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudieron eliminar las mascotas del cliente");
            }
        }
    }

    @Override
    protected Mascota mapear(ResultSet rs, Mascota mascota) throws SQLException {
        super.mapear(rs, mascota);
        mascota.setId(rs.getInt("ID_MASCOTA"));
        mascota.setNombre(rs.getString("NOMBRE"));
        mascota.setSexo(mapearSexoDesdeBase(rs.getString("SEXO")));
        mascota.setPeso(rs.getDouble("PESO"));
        mascota.setFechaNacimiento(rs.getDate("FECHA_NACIMIENTO").toLocalDate());
        mascota.setTipoMascota(TipoMascota.valueOf(rs.getString("TIPO_MASCOTA")));
        String raza = rs.getString("RAZA");
        if (raza != null) {
            mascota.setRaza(raza);
        }

        mascota.setCliente(new ClienteDAOImpl().findById(rs.getInt("ID_CLIENTE")));

        return mascota;
    }

    private String mapearSexoABase(SexoMascota sexo) {
        return switch (sexo) {
            case MACHO -> "M";
            case HEMBRA -> "H";
        };
    }

    private SexoMascota mapearSexoDesdeBase(String sexo) throws SQLException {
        return switch (sexo) {
            case "M" -> SexoMascota.MACHO;
            case "H" -> SexoMascota.HEMBRA;
            default -> throw new SQLException("Sexo de mascota no válido: " + sexo);
        };
    }

    private void validarIdCliente(Integer idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El id del cliente no puede ser nulo");
        }
    }
}
