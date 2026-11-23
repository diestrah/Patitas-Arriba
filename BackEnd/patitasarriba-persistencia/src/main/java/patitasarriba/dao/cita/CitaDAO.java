package patitasarriba.dao.cita;

import patitasarriba.dao.DAO;
import patitasarriba.modelo.cita.Cita;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

public interface CitaDAO extends DAO<Cita, Integer> {
    // Citas activas de las mascotas de un cliente (RF10)
    List<Cita> listarPorCliente(int idCliente) throws SQLException;

    // Citas activas con fecha_hora en [desde, hasta). Con idVeterinario, solo las que tienen
    // algun servicio activo con ese veterinario; con null, de todos (RF11, RF12 y cruces de agenda)
    List<Cita> listarPorRango(LocalDateTime desde, LocalDateTime hasta, Integer idVeterinario) throws SQLException;
}
