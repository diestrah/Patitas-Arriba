package patitasarriba.dao.impl.atencion;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.InsumoUtilizado;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

class InsumoUtilizadoDAOImpl extends RegistroDAOImpl<InsumoUtilizado> implements InsumoUtilizadoDAO {

    @Override
    public void insertInsumos(int idAtencionTratamiento, List<InsumoUtilizado> insumos) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_insumo_utilizado(?, ?, ?, ?, ?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            for (InsumoUtilizado insumo : insumos) {
                cmd.setInt("p_id_atencion_tratamiento", idAtencionTratamiento);
                cmd.setInt("p_id_articulo", insumo.getArticulo().getId());
                cmd.setInt("p_cantidad_utilizada", insumo.getCantidadUtilizada());
                cmd.setBoolean("p_activo", insumo.isActivo());
                cmd.registerOutParameter("p_id", Types.INTEGER);

                if (cmd.executeUpdate() == 0) {
                    throw new SQLException("No se pudo insertar el insumo utilizado");
                }

                insumo.setId(cmd.getInt("p_id"));
            }
        }
    }

    @Override
    public void deleteInsumos(int idAtencionTratamiento) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call eliminar_insumos_por_atencion_tratamiento(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_atencion_tratamiento", idAtencionTratamiento);
            cmd.executeUpdate();
        }
    }

    @Override
    public List<InsumoUtilizado> findByAtencionTratamientoId(int idAtencionTratamiento) throws SQLException {
        String sql = "{call listar_insumos_por_atencion_tratamiento(?)}";
        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id_atencion_tratamiento", idAtencionTratamiento);
            try (ResultSet rs = cmd.executeQuery()) {
                List<InsumoUtilizado> insumos = new ArrayList<>();
                while (rs.next()) {
                    insumos.add(mapear(rs, new InsumoUtilizado()));
                }
                return insumos;
            }
        }
    }

    @Override
    protected InsumoUtilizado mapear(ResultSet rs, InsumoUtilizado insumo) throws SQLException {
        super.mapear(rs, insumo);
        insumo.setId(rs.getInt("id_insumo_utilizado"));
        insumo.setCantidadUtilizada(rs.getInt("cantidad_utilizada"));

        // Articulo se carga completo porque ya existe ArticuloDAOImpl
        insumo.setArticulo(new ArticuloDAOImpl().findById(rs.getInt("id_articulo")));

        return insumo;
    }
}
