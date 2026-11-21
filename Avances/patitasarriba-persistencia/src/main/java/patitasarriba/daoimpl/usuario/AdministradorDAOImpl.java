package patitasarriba.daoimpl.usuario;

import patitasarriba.daoimpl.BaseDAOImpl;

import patitasarriba.daoimpl.horario.HorarioAdministradorDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.dao.usuario.AdministradorDAO;
import patitasarriba.modelo.usuario.Administrador;

import java.sql.*;

public class AdministradorDAOImpl extends BaseDAOImpl<Administrador> implements AdministradorDAO {

    @Override
    public Administrador findByDni(String dni) throws SQLException {
        return ejecutarComando(conn -> {
                if (dni == null || dni.isBlank()) {
                    throw new IllegalArgumentException("El dni no puede ser nulo o vacío");
                }

                String sql = "{call buscar_administrador_por_dni(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_dni", dni);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public Administrador findByCuenta(Integer idCuenta) throws SQLException {
        return ejecutarComando(conn -> {
                if (idCuenta == null) {
                    throw new IllegalArgumentException("El id de la cuenta no puede ser nulo");
                }

                String sql = "{call buscar_administrador_por_cuenta(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_cuenta", idCuenta);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public void insert(Administrador administrador) throws SQLException {
        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        Connection conn = TransactionsManager.getConnection();
        try {
            if (administrador == null) {
                throw new IllegalArgumentException("El administrador no puede ser nulo");
            }

            ejecutarComandoInsertar(conn, administrador);
            new HorarioAdministradorDAOImpl().insertarPorAdministrador(TransactionsManager.getConnection(), administrador.getId(), administrador.getHorarios());
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    @Override
    public void update(Administrador administrador) throws SQLException {
        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        Connection conn = TransactionsManager.getConnection();
        try {
            if (administrador == null) {
                throw new IllegalArgumentException("El administrador no puede ser nulo");
            }

            ejecutarComandoActualizar(conn, administrador);
            new HorarioAdministradorDAOImpl().eliminarPorAdministrador(TransactionsManager.getConnection(), administrador.getId());
            new HorarioAdministradorDAOImpl().insertarPorAdministrador(TransactionsManager.getConnection(), administrador.getId(), administrador.getHorarios());
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    // Los parametros que insertar_administrador y modificar_administrador comparten
    private void setParametrosComunes(CallableStatement cmd, Administrador administrador) throws SQLException {
        cmd.setInt("p_id_cuenta", administrador.getCuenta().getId());
        cmd.setBoolean("p_activo", administrador.isActivo());
        cmd.setString("p_dni", administrador.getDni());
        cmd.setString("p_nombres", administrador.getNombres());
        cmd.setString("p_apellido_paterno", administrador.getApellidoPaterno());
        cmd.setString("p_apellido_materno", administrador.getApellidoMaterno());
        cmd.setString("p_telefono", administrador.getTelefono());
    }

    @Override
    public void delete(Integer id) throws SQLException {
        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        Connection conn = TransactionsManager.getConnection();
        try {
            if (id == null) {
                throw new IllegalArgumentException("El id no puede ser nulo");
            }

            new HorarioAdministradorDAOImpl().eliminarPorAdministrador(TransactionsManager.getConnection(), id);
            ejecutarComandoEliminar(conn, id);
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    @Override
    protected Administrador mapear(ResultSet rs) throws SQLException {
        Administrador administrador = new Administrador();
        administrador.setActivo(rs.getBoolean("activo"));
        administrador.setId(rs.getInt("id_administrador"));
        administrador.setDni(rs.getString("dni"));
        administrador.setNombres(rs.getString("nombres"));
        administrador.setApellidoPaterno(rs.getString("apellido_paterno"));
        administrador.setApellidoMaterno(rs.getString("apellido_materno"));
        administrador.setTelefono(rs.getString("telefono"));

        // FK obligatoria: se resuelve el objeto completo
        int idCuenta = rs.getInt("id_cuenta");
        administrador.setCuenta(new CuentaDAOImpl().findById(idCuenta));
        administrador.setHorarios(new HorarioAdministradorDAOImpl().listarPorAdministrador(administrador.getId()));

        return administrador;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call listar_administradores()}");
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call buscar_administrador_por_id(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Administrador modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call insertar_administrador(?,?,?,?,?,?,?,?)}");
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
    protected CallableStatement comandoActualizar(Connection conn, Administrador modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call modificar_administrador(?,?,?,?,?,?,?,?)}");
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
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call eliminar_administrador(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Administrador modelo) throws SQLException {
        int id = cmd.getInt("p_id");
        if (cmd.wasNull() || id <= 0) throw new SQLException("No se obtuvo un ID válido para Administrador.");
        modelo.setId(id);
    }
}
