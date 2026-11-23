package patitasarriba.daoimpl.mascota;

import patitasarriba.dao.mascota.MascotaDAO;
import patitasarriba.daoimpl.BaseDAOImpl;
import patitasarriba.daoimpl.usuario.ClienteDAOImpl;
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

public class MascotaDAOImpl extends BaseDAOImpl<Mascota> implements MascotaDAO {

    @Override
    public List<Mascota> findByNombre(String nombre) throws SQLException {
        return ejecutarComando(conn -> {
                if (nombre == null || nombre.isBlank()) {
                    throw new IllegalArgumentException("El nombre no puede ser nulo o vacío");
                }

                String sql = "{call buscar_mascota_por_nombre(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_nombre", nombre);

                    try (ResultSet rs = cmd.executeQuery()) {
                        List<Mascota> mascotas = new ArrayList<>();
                        while (rs.next()) {
                            mascotas.add(mapear(rs));
                        }
                        return mascotas;
                    }
                }
            });
    }

    @Override
    public List<Mascota> listarPorCliente(Integer idCliente) throws SQLException {
        return ejecutarComando(conn -> {
                validarIdCliente(idCliente);

                String sql = "{call listar_mascotas_por_cliente(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_cliente", idCliente);

                    try (ResultSet rs = cmd.executeQuery()) {
                        List<Mascota> mascotas = new ArrayList<>();
                        while (rs.next()) {
                            mascotas.add(mapear(rs));
                        }
                        return mascotas;
                    }
                }
            });
    }

    @Override
    public void eliminarPorCliente(Integer idCliente) throws SQLException {
        ejecutarComando(conn -> {
                validarIdCliente(idCliente);

                String sql = "{call eliminar_mascotas_por_cliente(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_cliente", idCliente);
                    cmd.execute();
                }
                return null;
            });
    }

    @Override
    protected Mascota mapear(ResultSet rs) throws SQLException {
        Mascota mascota = new Mascota();
        mascota.setActivo(rs.getBoolean("activo"));
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
        mascota.setFoto(rs.getString("FOTO"));

        mascota.setCliente(new ClienteDAOImpl().findById(rs.getInt("ID_CLIENTE")));

        return mascota;
    }

    private String mapearSexoABase(SexoMascota sexo) {
        return switch (sexo) {
            case MACHO -> "MACHO";
            case HEMBRA -> "HEMBRA";
        };
    }

    private SexoMascota mapearSexoDesdeBase(String sexo) throws SQLException {
        return switch (sexo) {
            case "MACHO" -> SexoMascota.MACHO;
            case "HEMBRA" -> SexoMascota.HEMBRA;
            default -> throw new SQLException("Sexo de mascota no válido: " + sexo);
        };
    }

    private void validarIdCliente(Integer idCliente) {
        if (idCliente == null) {
            throw new IllegalArgumentException("El id del cliente no puede ser nulo");
        }
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        String sql = "{call listar_mascotas()}";
        CallableStatement cmd = conn.prepareCall(sql);
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_mascota_por_id(?)}";
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
    protected CallableStatement comandoInsertar(Connection conn, Mascota modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula");
        }

        String sql = "{call insertar_mascota(?,?,?,?,?,?,?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id_cliente", modelo.getCliente().getId());
            cmd.setString("p_nombre", modelo.getNombre());
            cmd.setString("p_sexo", mapearSexoABase(modelo.getSexo()));
            cmd.setDouble("p_peso", modelo.getPeso());
            cmd.setDate("p_fecha_nacimiento", Date.valueOf(modelo.getFechaNacimiento()));
            cmd.setString("p_tipo_mascota", modelo.getTipoMascota().name());
            cmd.setString("p_raza", modelo.getRaza());
            cmd.setString("p_foto", modelo.getFoto());
            cmd.setBoolean("p_activo", modelo.isActivo());
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Mascota modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula");
        }

        String sql = "{call modificar_mascota(?,?,?,?,?,?,?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id_cliente", modelo.getCliente().getId());
            cmd.setString("p_nombre", modelo.getNombre());
            cmd.setString("p_sexo", mapearSexoABase(modelo.getSexo()));
            cmd.setDouble("p_peso", modelo.getPeso());
            cmd.setDate("p_fecha_nacimiento", Date.valueOf(modelo.getFechaNacimiento()));
            cmd.setString("p_tipo_mascota", modelo.getTipoMascota().name());
            cmd.setString("p_raza", modelo.getRaza());
            cmd.setString("p_foto", modelo.getFoto());
            cmd.setBoolean("p_activo", modelo.isActivo());
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

        String sql = "{call eliminar_mascota(?)}";
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
    protected void procesarResultadoInsertar(CallableStatement cmd, Mascota modelo) throws SQLException {
        modelo.setId(cmd.getInt("p_id"));
    }
}
