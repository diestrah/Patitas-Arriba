package patitasarriba.dao.impl.atencion;

import patitasarriba.modelo.atencion.InsumoUtilizado;

import java.sql.SQLException;
import java.util.List;

// Sin modificador public: solo AtencionTratamientoDAOImpl la usa, para orquestar
// el maestro-detalle (ATENCION_TRATAMIENTO -> INSUMO_UTILIZADO).
interface InsumoUtilizadoDAO {
    void insertInsumos(int idAtencionTratamiento, List<InsumoUtilizado> insumos) throws SQLException;
    void deleteInsumos(int idAtencionTratamiento) throws SQLException;
    List<InsumoUtilizado> findByAtencionTratamientoId(int idAtencionTratamiento) throws SQLException;
}
