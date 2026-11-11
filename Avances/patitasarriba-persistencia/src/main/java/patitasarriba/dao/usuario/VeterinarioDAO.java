package patitasarriba.dao.usuario;

import patitasarriba.dao.DAO;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;

public interface VeterinarioDAO extends DAO<Veterinario, Integer> {
    Veterinario findByDni(String dni) throws SQLException;
    // RF01: persona duena de la cuenta (no filtra activo)
    Veterinario findByCuenta(Integer idCuenta) throws SQLException;
}
