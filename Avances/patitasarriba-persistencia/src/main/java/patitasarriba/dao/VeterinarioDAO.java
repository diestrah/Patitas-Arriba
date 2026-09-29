package patitasarriba.dao;

import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;

public interface VeterinarioDAO extends DAO<Veterinario, Integer> {
    Veterinario findByDni(String dni) throws SQLException;
}
