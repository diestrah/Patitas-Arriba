package patitasarriba.dao.impl.producto;

import patitasarriba.dao.transacciones.TransactionsManager;

import conexion.DBManager;
import patitasarriba.dao.ServicioDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.producto.TipoServicio;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAOImpl extends RegistroDAOImpl<Servicio> implements ServicioDAO {
    @Override
    public List<Servicio> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_servicios()}";
            try (CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {

                List<Servicio> servicios = new ArrayList<>();

                while (rs.next()) {
                    servicios.add(mapear(rs, new Servicio()));
                }

                return servicios;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Servicio findById(Integer integer) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (integer == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call buscar_servicio_por_id(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setInt("p_id", integer);
                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Servicio()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void insert(Servicio modelo) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (modelo == null) {
                throw new IllegalArgumentException("El servicio no puede ser nulo");
            }

            String sql = "{call insertar_servicio(?,?,?,?,?,?,?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {
                setParametrosComunes(cmd, modelo);
                cmd.registerOutParameter("p_id", Types.INTEGER);
                cmd.execute();
                modelo.setId(cmd.getInt("p_id"));
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void update(Servicio modelo) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (modelo == null) {
                throw new IllegalArgumentException("El servicio no puede ser nulo");
            }

            String sql = "{call modificar_servicio(?,?,?,?,?,?,?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {
                setParametrosComunes(cmd, modelo);
                cmd.setInt("p_id", modelo.getId());

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

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
    public void delete(Integer integer) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (integer == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            String sql = "{call eliminar_servicio(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", integer);

                cmd.execute();
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Servicio findByName(String name) throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            if (name == null) {
                throw new IllegalArgumentException("El nombre no puede ser nulo");
            }

            String sql = "{call buscar_servicio_por_nombre(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {

                cmd.setString("p_nombre", name);
                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Servicio()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    protected Servicio mapear(ResultSet rs, Servicio servicio) throws SQLException {
        super.mapear(rs, servicio);
        servicio.setId(rs.getInt("id_servicio"));
        servicio.setNombre(rs.getString("nombre"));
        servicio.setDescripcion(rs.getString("descripcion"));
        servicio.setPrecioBase(rs.getDouble("precio_base"));
        int duracionEstimada = rs.getInt("duracion_estimada");
        servicio.setDuracionEstimada(rs.wasNull() ? null : duracionEstimada);
        servicio.setTipo(TipoServicio.valueOf(rs.getString("tipo_servicio")));
        return servicio;
    }
}
