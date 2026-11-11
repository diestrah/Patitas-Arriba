package patitasarriba.daoimpl.atencion;

import static patitasarriba.daoimpl.ConexionDAO.ejecutarComando;

import patitasarriba.daoimpl.producto.ArticuloDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.producto.Articulo;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class InsumoUtilizadoDAOImpl
implements InsumoUtilizadoDAO {

    public InsumoUtilizado findById(int id) throws SQLException {
        return ejecutarComando(conn -> {
                try (CallableStatement cmd = conn.prepareCall("{call buscar_insumo_utilizado_por_id(?)}")) {
                    cmd.setInt("p_id", id);
                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public void insertInsumos(int idAtencion, List<InsumoUtilizado> insumos) throws SQLException {
        Connection conn = TransactionsManager.getConnection();
        String sql = "{call insertar_insumo_utilizado(?,?,?,?,?,?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (InsumoUtilizado insumo : insumos) {
                cmd.setInt("p_id_atencion", idAtencion);
                cmd.setInt("p_id_articulo", insumo.getArticulo().getId());
                cmd.setInt("p_cantidad_utilizada", insumo.getCantidadUtilizada());
                cmd.setBoolean("p_es_cobrable", insumo.isEsCobrable());
                cmd.setBoolean("p_activo", insumo.isActivo());
                cmd.registerOutParameter("p_id", Types.INTEGER);

                cmd.execute();
                insumo.setId(cmd.getInt("p_id"));
            }
        }
    }

    @Override
    public void deleteInsumos(int idAtencion) throws SQLException {
        Connection conn = TransactionsManager.getConnection();
        String sql = "{call eliminar_insumos_por_atencion(?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_atencion", idAtencion);
            cmd.execute();
        }
    }

    @Override
    public List<InsumoUtilizado> findByAtencionId(int idAtencion) throws SQLException {
        return ejecutarComando(conn -> {
                String sql = "{call listar_insumos_por_atencion(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {

                    cmd.setInt("p_id_atencion", idAtencion);

                    try (ResultSet rs = cmd.executeQuery()) {
                        List<InsumoUtilizado> insumos = new ArrayList<>();
                        while (rs.next()) {
                            insumos.add(mapear(rs));
                        }
                        return insumos;
                    }
                }
            });
    }

    @Override
    public void updateInsumos(int idAtencion, List<InsumoUtilizado> insumos) throws SQLException {
        Connection conn = TransactionsManager.getConnection();
        String sql = "{call modificar_insumo_utilizado(?,?,?,?,?,?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (InsumoUtilizado insumo : insumos) {
                if (insumo.getId() == 0) {
                    insertInsumos(idAtencion, List.of(insumo));
                    continue;
                }

                cmd.setInt("p_id", insumo.getId());
                cmd.setInt("p_id_atencion", idAtencion);
                cmd.setInt("p_id_articulo", insumo.getArticulo().getId());
                cmd.setInt("p_cantidad_utilizada", insumo.getCantidadUtilizada());
                cmd.setBoolean("p_es_cobrable", insumo.isEsCobrable());
                cmd.setBoolean("p_activo", insumo.isActivo());

                cmd.execute();
            }
        }

    }

    protected InsumoUtilizado mapear(ResultSet rs) throws SQLException {
        InsumoUtilizado insumo = new InsumoUtilizado();
        insumo.setActivo(rs.getBoolean("activo"));
        insumo.setId(rs.getInt("id_insumo_utilizado"));
        insumo.setCantidadUtilizada(rs.getInt("cantidad_utilizada"));
        insumo.setEsCobrable(rs.getBoolean("es_cobrable"));

        Atencion atencion = new Atencion();
        atencion.setId(rs.getInt("id_atencion"));
        insumo.setAtencion(atencion);

        Articulo articulo = new ArticuloDAOImpl().findById(rs.getInt("id_articulo"));

        if (articulo == null) {
            throw new SQLException("No se encontró el artículo del insumo utilizado");
        }
        insumo.setArticulo(articulo);
        return insumo;
    }
}
