package patitasarriba.daoimpl.usuario;

import static patitasarriba.daoimpl.ConexionDAO.ejecutarComando;

import patitasarriba.dao.usuario.RegistroPendienteDAO;
import patitasarriba.modelo.usuario.RegistroPendiente;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;

public class RegistroPendienteDAOImpl implements RegistroPendienteDAO {

    @Override
    public void insert(RegistroPendiente registro) throws SQLException {
        ejecutarComando(conn -> {
                if (registro == null) {
                    throw new IllegalArgumentException("El registro pendiente no puede ser nulo");
                }

                String sql = "{call insertar_registro_pendiente(?,?,?,?,?,?,?,?,?,?,?,?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_dni", registro.getDni());
                    cmd.setString("p_nombres", registro.getNombres());
                    cmd.setString("p_apellido_paterno", registro.getApellidoPaterno());
                    cmd.setString("p_apellido_materno", registro.getApellidoMaterno());
                    cmd.setString("p_telefono", registro.getTelefono());
                    cmd.setString("p_correo", registro.getCorreo());
                    cmd.setString("p_nombre_usuario", registro.getNombreUsuario());
                    cmd.setString("p_password", registro.getPassword());
                    cmd.setString("p_token_hash", registro.getTokenHash());
                    cmd.setTimestamp("p_fecha_creacion", Timestamp.valueOf(registro.getFechaCreacion()));
                    cmd.setTimestamp("p_fecha_vencimiento", Timestamp.valueOf(registro.getFechaVencimiento()));
                    cmd.registerOutParameter("p_id", Types.INTEGER);

                    cmd.execute();
                    registro.setId(cmd.getInt("p_id"));
                }
                return null;
            });
    }

    @Override
    public RegistroPendiente findByTokenHash(String tokenHash) throws SQLException {
        return ejecutarComando(conn -> {
                if (tokenHash == null || tokenHash.isBlank()) {
                    throw new IllegalArgumentException("El token no puede ser nulo o vacío");
                }

                String sql = "{call buscar_registro_pendiente_por_token(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_token_hash", tokenHash);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public void delete(int id) throws SQLException {
        ejecutarComando(conn -> {
                String sql = "{call eliminar_registro_pendiente(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id", id);
                    cmd.execute();
                }
                return null;
            });
    }

    @Override
    public void deleteByCorreo(String correo) throws SQLException {
        ejecutarComando(conn -> {
                if (correo == null || correo.isBlank()) {
                    throw new IllegalArgumentException("El correo no puede ser nulo o vacío");
                }

                String sql = "{call eliminar_registros_pendientes_por_correo(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_correo", correo.trim());
                    cmd.execute();
                }
                return null;
            });
    }

    @Override
    public void deleteVencidos(LocalDateTime ahora) throws SQLException {
        ejecutarComando(conn -> {
                String sql = "{call eliminar_registros_pendientes_vencidos(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setTimestamp("p_ahora", Timestamp.valueOf(ahora));
                    cmd.execute();
                }
                return null;
            });
    }

    private RegistroPendiente mapear(ResultSet rs) throws SQLException {
        RegistroPendiente registro = new RegistroPendiente();
        registro.setId(rs.getInt("id_registro_pendiente"));
        registro.setDni(rs.getString("dni"));
        registro.setNombres(rs.getString("nombres"));
        registro.setApellidoPaterno(rs.getString("apellido_paterno"));
        registro.setApellidoMaterno(rs.getString("apellido_materno"));
        registro.setTelefono(rs.getString("telefono"));
        registro.setCorreo(rs.getString("correo"));
        registro.setNombreUsuario(rs.getString("nombre_usuario"));
        registro.setPassword(rs.getString("password"));
        registro.setTokenHash(rs.getString("token_hash"));
        registro.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        registro.setFechaVencimiento(rs.getTimestamp("fecha_vencimiento").toLocalDateTime());
        return registro;
    }
}
