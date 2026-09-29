package patitasarriba.dao;

import patitasarriba.modelo.mascota.Mascota;

import java.sql.SQLException;
import java.util.List;

public interface MascotaDAO extends DAO<Mascota, Integer> {
    List<Mascota> findByNombre(String nombre) throws SQLException;

    List<Mascota> listarPorCliente(Integer idCliente) throws SQLException;

    void eliminarPorCliente(Integer idCliente) throws SQLException;
}
