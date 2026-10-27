package patitasarriba.dao.impl.atencion;

import patitasarriba.modelo.atencion.AtencionDiagnostico;

import java.sql.SQLException;
import java.util.List;

// Sin modificador public: solo AtencionDAOImpl la usa, para orquestar
// el maestro-detalle (ATENCION -> ATENCION_DIAGNOSTICO).
interface AtencionDiagnosticoDAO {
    void insertDiagnosticos(int idAtencion, List<AtencionDiagnostico> diagnosticos) throws SQLException;
    void deleteDiagnosticos(int idAtencion) throws SQLException;
    List<AtencionDiagnostico> findByAtencionId(int idAtencion) throws SQLException;
}
