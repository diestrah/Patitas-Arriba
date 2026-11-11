package patitasarriba.dao.atencion;

import patitasarriba.dao.DAO;
import patitasarriba.modelo.atencion.Atencion;

import java.sql.SQLException;

public interface AtencionDAO extends DAO<Atencion, Integer> {
    // La atencion de un servicio de cita, activa o anulada (id_detalle_cita es UNIQUE); null si no tiene
    Atencion findByDetalleCita(int idDetalleCita) throws SQLException;
}
