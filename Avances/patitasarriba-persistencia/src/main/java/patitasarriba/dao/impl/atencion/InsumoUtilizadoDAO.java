package patitasarriba.dao.impl.atencion;

import patitasarriba.modelo.atencion.InsumoUtilizado;

import java.sql.SQLException;
import java.util.List;

// Sin modificador public: solo AtencionDAOImpl la usa, para orquestar
// el maestro-detalle (ATENCION -> INSUMO_UTILIZADO).
interface InsumoUtilizadoDAO {
    void insertInsumos(int idAtencion, List<InsumoUtilizado> insumos) throws SQLException;
    void deleteInsumos(int idAtencion) throws SQLException;
    void updateInsumos(int idAtencion, List<InsumoUtilizado> insumos) throws SQLException;
    List<InsumoUtilizado> findByAtencionId(int idAtencion) throws SQLException;
}
