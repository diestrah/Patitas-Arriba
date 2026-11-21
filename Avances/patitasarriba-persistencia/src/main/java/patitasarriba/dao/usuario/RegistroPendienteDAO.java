package patitasarriba.dao.usuario;

import patitasarriba.modelo.usuario.RegistroPendiente;

import java.sql.SQLException;
import java.time.LocalDateTime;

public interface RegistroPendienteDAO {
    void insert(RegistroPendiente registro) throws SQLException;

    // Busca por el hash del token; null si no existe
    RegistroPendiente findByTokenHash(String tokenHash) throws SQLException;

    void delete(int id) throws SQLException;

    void deleteByCorreo(String correo) throws SQLException;

    void deleteVencidos(LocalDateTime ahora) throws SQLException;
}
