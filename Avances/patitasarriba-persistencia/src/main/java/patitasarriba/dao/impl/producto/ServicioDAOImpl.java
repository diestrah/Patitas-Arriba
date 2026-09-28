package patitasarriba.dao.impl.producto;

import conexion.DBManager;
import patitasarriba.dao.ServicioDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.producto.TipoServicio;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAOImpl extends RegistroDAOImpl implements ServicioDAO {
    @Override
    public List<Servicio> findAll() throws SQLException {

        String sql = "{call listar_servicios()}";
        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()) {

            List<Servicio> servicios = new ArrayList<>();

            while (rs.next()) {
                servicios.add(mapear(rs, new Servicio()));
            }

            return servicios;
        }
    }

    @Override
    public Servicio findById(Integer integer) throws SQLException {
        if (integer == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_servicio_por_id(?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id", integer);
            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Servicio()) : null;
            }
        }
    }

    @Override
    public void insert(Servicio modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }

        String sql = "{call insertar_servicio (?, ?, ?, ?, ?, ?, ?, ?, ?) }";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            setParametrosComunes(cmd, modelo);
            cmd.registerOutParameter("p_id", Types.INTEGER);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo insertar el tratamiento");
            }

            modelo.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(Servicio modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }

        String sql = "{call modificar_servicio(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id", modelo.getId());
            setParametrosComunes(cmd, modelo);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo actualizar el servicio");
            }
        }
    }

    private void setParametrosComunes(CallableStatement cmd, Servicio servicio) throws SQLException {
        cmd.setString("p_nombre", servicio.getNombre());
        cmd.setBigDecimal("p_precio_base", BigDecimal.valueOf(servicio.getPrecioBase()));
        cmd.setString("p_descripcion", servicio.getDescripcion());
        cmd.setBoolean("p_estado", servicio.isActivo());
        cmd.setInt("p_duracion_estimada", servicio.getDuracionEstimada());
        cmd.setString("p_servicio_medico", servicio.getTipo().toString());
        cmd.setInt("p_requiere_triaje", servicio.isRequiereTriaje() ? 1 : 0);
        cmd.setInt("p_requiere_vacuna", servicio.isRequiereVacuna() ? 1 : 0);
    }

    @Override
    public void delete(Integer integer) throws SQLException {
        if (integer == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_servicio(?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", integer);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar el servicio");
            }
        }
    }

    @Override
    public Servicio findByName(String name) throws SQLException {
        if (name == null) {
            throw new IllegalArgumentException("El nombre no puede ser nulo");
        }

        String sql = "{call buscar_servicio_por_nombre(?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setString("p_nombre", name);
            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Servicio()) : null;
            }
        }
    }

    protected Servicio mapear(ResultSet rs, Servicio servicio) throws SQLException {
        super.mapear(rs, servicio);
        servicio.setId(rs.getInt("id_servicio"));
        servicio.setNombre(rs.getString("nombre"));
        servicio.setDescripcion(rs.getString("descripcion"));
        servicio.setPrecioBase(rs.getDouble("precio_base"));
        servicio.setDuracionEstimada(rs.getInt("duracion_estimada"));
        servicio.setTipo(Enum.valueOf(TipoServicio.class, rs.getString("tipo_servicio_medico")));
        return servicio;
    }
}
