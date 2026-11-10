package patitasarriba.dao;

import patitasarriba.modelo.boleta.Boleta;

import java.sql.SQLException;

public interface BoletaDAO extends DAO<Boleta, Integer> {
    int boletaConServicio(int idDetalleCita) throws SQLException;
    int boletaConInsumo(int idInsumoUtilizado) throws SQLException;
    int boletaConSerie(String serieCorrelativo) throws SQLException;
}
