package patitasarriba.dao.impl.boleta;

import patitasarriba.modelo.boleta.DetalleBoleta;

import java.sql.SQLException;
import java.util.List;

interface DetalleBoletaServDAO {
    void insertDetalles(int idBoleta, List<DetalleBoleta> detalles) throws SQLException;
    void deleteDetallesPorBoleta(int idBoleta) throws SQLException;
    List<DetalleBoleta> findByBoletaId(int idBoleta) throws SQLException;
}
