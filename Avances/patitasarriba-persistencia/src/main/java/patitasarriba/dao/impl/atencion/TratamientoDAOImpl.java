package patitasarriba.dao.impl.atencion;

import conexion.DBManager;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.modelo.atencion.Tratamiento;
import patitasarriba.dao.TratamientoDAO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TratamientoDAOImpl extends RegistroDAOImpl<Tratamiento> implements TratamientoDAO {
    @Override
    public List<Tratamiento> findAll() throws SQLException {

        String sql = "{call listar_tratamientos()}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql);
                ResultSet rs = cmd.executeQuery()){

            List<Tratamiento> tratamientos = new ArrayList<>();

            while (rs.next()){
                tratamientos.add(mapear(rs, new Tratamiento()));
            }
            return tratamientos;
        }
    }

    @Override
    public Tratamiento findById(Integer integer) throws SQLException {
        if (integer == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call buscar_tratamiento_por_id(?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id", integer);

            try (ResultSet rs = cmd.executeQuery()){
                return rs.next() ? mapear(rs, new Tratamiento()) : null;
            }
        }
    }

    @Override
    public void insert(Tratamiento modelo) throws SQLException {
        if (modelo == null){
            throw new IllegalArgumentException("El tratamiento no puede ser nulo");
        }

        String sql = "{call insertar_tratamiento(?, ?, ?, ?}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)){

            cmd.setString("p_nombre_procedimiento", modelo.getNombreProcedimiento());
            cmd.setString("p_descripcion", modelo.getDescripcion());
            cmd.setInt("p_estado", modelo.isActivo() ? 1 : 0);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo insertar el tratamiento");
            }

            modelo.setId(cmd.getInt("p_id"));
        }
    }

    @Override
    public void update(Tratamiento modelo) throws SQLException {
        if (modelo == null) {
            throw new IllegalArgumentException("El tratamiento no puede ser nulo");
        }

        String sql = "{call actualizar_tratamiento (?, ?, ?, ?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id", modelo.getId());
            cmd.setString("p_nombre", modelo.getNombreProcedimiento());
            cmd.setString("p_descripcion", modelo.getDescripcion());
            cmd.setInt("p_estado", modelo.isActivo() ? 1 : 0);

            if (cmd.executeUpdate() == 0){
                throw new SQLException("No se pudo actualizar el tratamiento");
            }
        }
    }

    @Override
    public void delete(Integer integer) throws SQLException {
        if (integer == null) {
            throw new IllegalArgumentException("El id no puede ser nulo");
        }

        String sql = "{call eliminar_tratamiento(?)}";
        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)) {

            cmd.setInt("p_id", integer);
            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar el tratamiento");
            }
        }
    }

    @Override
    public Tratamiento findByName(String nombre) throws SQLException {
        if (nombre == null) {
            throw new IllegalArgumentException("El nombre no puede set nulo");
        }

        String sql = "{call buscar_tratamiento_por_nombre(?)}";

        try (
                Connection conn = DBManager.getInstance().getConnection();
                CallableStatement cmd = conn.prepareCall(sql)){

            cmd.setString("p_nombre", nombre);

            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Tratamiento()) : null;
            }
        }
    }

    protected Tratamiento mapear(ResultSet rs, Tratamiento tratamiento) throws SQLException {
        super.mapear(rs, tratamiento);
        tratamiento.setId(rs.getInt("id_tratamiento"));
        tratamiento.setNombreProcedimiento(rs.getString("nombre_procedimiento"));
        tratamiento.setDescripcion(rs.getString("descripcion"));
        return tratamiento;
    }
}
