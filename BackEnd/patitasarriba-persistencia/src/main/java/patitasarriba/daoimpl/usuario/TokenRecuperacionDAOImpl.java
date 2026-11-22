package patitasarriba.daoimpl.usuario;

import static patitasarriba.daoimpl.ConexionDAO.ejecutarComando;

import patitasarriba.dao.usuario.TokenRecuperacionDAO;
import patitasarriba.modelo.usuario.TokenRecuperacion;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

public class TokenRecuperacionDAOImpl implements TokenRecuperacionDAO {

    @Override
    public void insert(TokenRecuperacion token) throws SQLException {
        ejecutarComando(conn -> {
                if (token == null) {
                    throw new IllegalArgumentException("El token no puede ser nulo");
                }

                String sql = "{call insertar_token_recuperacion(?,?,?,?,?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_cuenta", token.getIdCuenta());
                    cmd.setString("p_token_hash", token.getTokenHash());
                    cmd.setTimestamp("p_fecha_creacion", Timestamp.valueOf(token.getFechaCreacion()));
                    cmd.setTimestamp("p_fecha_vencimiento", Timestamp.valueOf(token.getFechaVencimiento()));
                    cmd.registerOutParameter("p_id", Types.INTEGER);

                    cmd.execute();
                    token.setId(cmd.getInt("p_id"));
                }
                return null;
            });
    }

    @Override
    public TokenRecuperacion findByTokenHash(String tokenHash) throws SQLException {
        return ejecutarComando(conn -> {
                if (tokenHash == null || tokenHash.isBlank()) {
                    throw new IllegalArgumentException("El token no puede ser nulo o vacío");
                }

                String sql = "{call buscar_token_recuperacion_por_token(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_token_hash", tokenHash);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public void marcarUsado(int id) throws SQLException {
        ejecutarComando(conn -> {
                String sql = "{call marcar_token_recuperacion_usado(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id", id);
                    cmd.execute();
                }
                return null;
            });
    }

    @Override
    public void invalidarDeCuenta(int idCuenta) throws SQLException {
        ejecutarComando(conn -> {
                String sql = "{call invalidar_tokens_recuperacion_de_cuenta(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_cuenta", idCuenta);
                    cmd.execute();
                }
                return null;
            });
    }

    private TokenRecuperacion mapear(ResultSet rs) throws SQLException {
        TokenRecuperacion token = new TokenRecuperacion();
        token.setId(rs.getInt("id_token_recuperacion"));
        token.setIdCuenta(rs.getInt("id_cuenta"));
        token.setTokenHash(rs.getString("token_hash"));
        token.setFechaCreacion(rs.getTimestamp("fecha_creacion").toLocalDateTime());
        token.setFechaVencimiento(rs.getTimestamp("fecha_vencimiento").toLocalDateTime());
        token.setUsado(rs.getBoolean("usado"));
        return token;
    }
}
