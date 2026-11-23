package patitasarriba.dao.usuario;

import patitasarriba.modelo.usuario.TokenRecuperacion;

import java.sql.SQLException;

public interface TokenRecuperacionDAO {
    void insert(TokenRecuperacion token) throws SQLException;

    // Busca por el hash del token; null si no existe (no filtra usado ni vencido)
    TokenRecuperacion findByTokenHash(String tokenHash) throws SQLException;

    void marcarUsado(int id) throws SQLException;

    // Deja inservibles los tokens que la cuenta tenia pendientes
    void invalidarDeCuenta(int idCuenta) throws SQLException;
}
