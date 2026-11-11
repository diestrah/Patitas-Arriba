package patitasarriba.dao;

import patitasarriba.modelo.boleta.Boleta;

import java.sql.SQLException;

public interface BoletaDAO extends DAO<Boleta, Integer> {
    int boletaConServicio(int idDetalleCita) throws SQLException;
    int boletaConInsumo(int idInsumoUtilizado) throws SQLException;
    int boletaConSerie(String serieCorrelativo) throws SQLException;
    // Último correlativo usado en una serie ('B001' -> 123 si la última es B001-000123), contando las anuladas. 0 si no hay
    int obtenerUltimoCorrelativo(String serie) throws SQLException;
}
