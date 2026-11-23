package patitasarriba.daoimpl.producto;

import patitasarriba.dao.producto.ServicioDAO;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.producto.TipoServicio;

import java.math.BigDecimal;
import java.sql.*;

public class ServicioDAOImpl extends ProductoDAOImpl<Servicio> implements ServicioDAO {

    private void setParametrosComunes(CallableStatement cmd, Servicio servicio) throws SQLException {
        cmd.setString("p_nombre", servicio.getNombre());
        cmd.setBigDecimal("p_precio_base", BigDecimal.valueOf(servicio.getPrecioBase()));
        cmd.setString("p_descripcion", servicio.getDescripcion());
        cmd.setBoolean("p_activo", servicio.isActivo());
        if (servicio.getDuracionEstimada() == null) {
            cmd.setNull("p_duracion_estimada", Types.INTEGER);
        } else {
            cmd.setInt("p_duracion_estimada", servicio.getDuracionEstimada());
        }
        cmd.setString("p_tipo_servicio", servicio.getTipo().name());
    }

    @Override
    public Servicio findByName(String name) throws SQLException {
        return ejecutarComando(conn -> {
                if (name == null) {
                    throw new IllegalArgumentException("El nombre no puede ser nulo");
                }

                String sql = "{call buscar_servicio_por_nombre(?)}";

                try (CallableStatement cmd = conn.prepareCall(sql)) {

                    cmd.setString("p_nombre", name);
                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    protected Servicio mapear(ResultSet rs) throws SQLException {
        Servicio servicio = new Servicio();
        mapearProducto(rs, servicio);
        servicio.setId(rs.getInt("id_servicio"));
        int duracionEstimada = rs.getInt("duracion_estimada");
        servicio.setDuracionEstimada(rs.wasNull() ? null : duracionEstimada);
        servicio.setTipo(TipoServicio.valueOf(rs.getString("tipo_servicio")));
        return servicio;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        String sql = "{call listar_servicios()}";
        CallableStatement cmd = conn.prepareCall(sql);
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_servicio_por_id(?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Servicio modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }

        String sql = "{call insertar_servicio(?,?,?,?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            setParametrosComunes(cmd, modelo);
            cmd.registerOutParameter("p_id", Types.INTEGER);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoActualizar(Connection conn, Servicio modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }

        String sql = "{call modificar_servicio(?,?,?,?,?,?,?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            setParametrosComunes(cmd, modelo);
            cmd.setInt("p_id", modelo.getId());
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoEliminar(Connection conn, Integer id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_servicio(?)}";
        CallableStatement cmd = conn.prepareCall(sql);
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Servicio modelo) throws SQLException {
        modelo.setId(cmd.getInt("p_id"));
    }
}
