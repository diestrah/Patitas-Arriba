package patitasarriba.dao.impl.atencion;

import patitasarriba.modelo.atencion.AtencionDiagnostico;

import java.sql.SQLException;
import java.util.List;

// Sin modificador public: solo AtencionMedicaDAOImpl la usa, para orquestar
// el maestro-detalle (ATENCION_MEDICA -> ATENCION_DIAGNOSTICO).
interface AtencionDiagnosticoDAO {
    void insertDiagnosticos(int idAtencionMedica, List<AtencionDiagnostico> diagnosticos) throws SQLException;
    void deleteDiagnosticos(int idAtencionMedica) throws SQLException;
    List<AtencionDiagnostico> findByAtencionMedicaId(int idAtencionMedica) throws SQLException;
}
