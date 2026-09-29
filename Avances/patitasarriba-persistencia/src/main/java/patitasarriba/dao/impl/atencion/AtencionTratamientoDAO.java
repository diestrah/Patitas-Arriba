package patitasarriba.dao.impl.atencion;

import patitasarriba.modelo.atencion.AtencionTratamiento;

import java.sql.SQLException;
import java.util.List;

// Sin modificador public: solo AtencionMedicaDAOImpl la usa, para orquestar
// el maestro-detalle (ATENCION_MEDICA -> ATENCION_TRATAMIENTO).
interface AtencionTratamientoDAO {
    void insertTratamientos(int idAtencionMedica, List<AtencionTratamiento> tratamientos) throws SQLException;
    void deleteTratamientos(int idAtencionMedica) throws SQLException;
    List<AtencionTratamiento> findByAtencionMedicaId(int idAtencionMedica) throws SQLException;
}
