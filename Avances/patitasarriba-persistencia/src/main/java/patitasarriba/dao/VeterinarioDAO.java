package patitasarriba.dao;

import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;

public interface VeterinarioDAO extends DAO<Veterinario, Integer> {
    Veterinario findByDni(String dni) throws SQLException;
    // No filtra activo: detecta tambien registros dados de baja
    Veterinario findByTelefono(String telefono) throws SQLException;
    // No filtra activo: detecta tambien registros dados de baja
    Veterinario findByCuenta(Integer idCuenta) throws SQLException;
    // No filtra activo: detecta tambien registros dados de baja
    Veterinario findByColegiatura(String numeroColegiatura) throws SQLException;
    // Incluye registros dados de baja: para cargar relaciones del historial
    Veterinario findByIdHistorico(Integer id) throws SQLException;
}
