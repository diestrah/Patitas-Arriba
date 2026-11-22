package patitasarriba.dao.atencion;

import patitasarriba.dao.DAO;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.EstadoAtencion;

import java.sql.SQLException;
import java.util.List;

public interface AtencionDAO extends DAO<Atencion, Integer> {
    // La atencion de un servicio de cita, activa o anulada (id_detalle_cita es UNIQUE); null si no tiene
    Atencion findByDetalleCita(int idDetalleCita) throws SQLException;

    // Atenciones activas de una mascota (historial clinico, RF15)
    List<Atencion> listarPorMascota(int idMascota) throws SQLException;

    // De que servicios de cita hay atencion, activa o anulada, sin cargar la atencion completa
    List<EstadoAtencion> listarEstados() throws SQLException;
}
