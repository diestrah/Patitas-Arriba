package patitasarriba.daoimpl;

import patitasarriba.dao.ComandoDAO;
import patitasarriba.dao.DAO;
import patitasarriba.modelo.Registro;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseDAOImpl<T extends Registro> implements DAO<T, Integer> {

    protected abstract CallableStatement comandoInsertar(Connection conn, T modelo) throws SQLException;

    protected abstract CallableStatement comandoActualizar(Connection conn, T modelo) throws SQLException;

    protected abstract CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException;

    protected abstract CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException;

    protected abstract CallableStatement comandoListar(Connection conn) throws SQLException;

    protected abstract T mapear(ResultSet rs) throws SQLException;

    protected abstract void procesarResultadoInsertar(CallableStatement cmd, T modelo) throws SQLException;

    protected <R> R ejecutarComando(ComandoDAO<R> comando) throws SQLException {
        return ConexionDAO.ejecutarComando(comando);
    }

    protected void ejecutarComandoInsertar(Connection conn, T modelo) throws SQLException {

        try (CallableStatement cmd = comandoInsertar(conn, modelo)) {
            cmd.execute();
            procesarResultadoInsertar(cmd, modelo);
        }
    }

    protected boolean ejecutarComandoActualizar(Connection conn, T modelo) throws SQLException {

        try (CallableStatement cmd = comandoActualizar(conn, modelo)) {
            return cmd.executeUpdate() > 0;
        }
    }

    protected boolean ejecutarComandoEliminar(Connection conn, Integer id) throws SQLException {

        try (CallableStatement cmd = comandoEliminar(conn, id)) {
            return cmd.executeUpdate() > 0;
        }
    }

    @Override
    public List<T> findAll() throws SQLException {
        return ejecutarComando(conn -> {
                try (
                CallableStatement cmd = comandoListar(conn);
                ResultSet rs = cmd.executeQuery()
            ) {

                    List<T> modelos = new ArrayList<>();
                    while (rs.next()) {
                        modelos.add(mapear(rs));
                    }

                    return modelos;
                }
            });
    }

    @Override
    public T findById(Integer id) throws SQLException {
        return ejecutarComando(conn -> {
                try (
                CallableStatement cmd = comandoBuscar(conn, id);
                ResultSet rs = cmd.executeQuery()
            ) {
                    if (!rs.next()) {
                        return null;
                    }

                    return mapear(rs);
                }
            });
    }

    @Override
    public void insert(T modelo) throws SQLException {
        ejecutarComando(conn -> {
                ejecutarComandoInsertar(conn, modelo);
                return null;
            });
    }

    @Override
    public void update(T modelo) throws SQLException {
        ejecutarComando(conn -> ejecutarComandoActualizar(conn, modelo));
    }

    @Override
    public void delete(Integer id) throws SQLException {
        ejecutarComando(conn -> ejecutarComandoEliminar(conn, id));
    }
}
