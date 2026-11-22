package patitasarriba.daoimpl.usuario;

import patitasarriba.daoimpl.BaseDAOImpl;

import patitasarriba.daoimpl.horario.HorarioVeterinarioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.dao.usuario.VeterinarioDAO;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.*;

public class VeterinarioDAOImpl extends BaseDAOImpl<Veterinario> implements VeterinarioDAO {

    @Override
    public Veterinario findByDni(String dni) throws SQLException {
        return ejecutarComando(conn -> {
                if (dni == null || dni.isBlank()) {
                    throw new IllegalArgumentException("El dni no puede ser nulo o vacío");
                }

                String sql = "{call buscar_veterinario_por_dni(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setString("p_dni", dni);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public Veterinario findByCuenta(Integer idCuenta) throws SQLException {
        return ejecutarComando(conn -> {
                if (idCuenta == null) {
                    throw new IllegalArgumentException("El id de la cuenta no puede ser nulo");
                }

                String sql = "{call buscar_veterinario_por_cuenta(?)}";
                try (CallableStatement cmd = conn.prepareCall(sql)) {
                    cmd.setInt("p_id_cuenta", idCuenta);

                    try (ResultSet rs = cmd.executeQuery()) {
                        return rs.next() ? mapear(rs) : null;
                    }
                }
            });
    }

    @Override
    public void insert(Veterinario veterinario) throws SQLException {
        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        Connection conn = TransactionsManager.getConnection();
        try {
            if (veterinario == null) {
                throw new IllegalArgumentException("El veterinario no puede ser nulo");
            }

            ejecutarComandoInsertar(conn, veterinario);
            new HorarioVeterinarioDAOImpl().insertarPorVeterinario(TransactionsManager.getConnection(), veterinario.getId(), veterinario.getHorarios());
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    @Override
    public void update(Veterinario veterinario) throws SQLException {
        boolean propia = !TransactionsManager.activa();
        if (propia) TransactionsManager.iniciar();
        Connection conn = TransactionsManager.getConnection();
        try {
            if (veterinario == null) {
                throw new IllegalArgumentException("El veterinario no puede ser nulo");
            }

            ejecutarComandoActualizar(conn, veterinario);
            new HorarioVeterinarioDAOImpl().eliminarPorVeterinario(TransactionsManager.getConnection(), veterinario.getId());
            new HorarioVeterinarioDAOImpl().insertarPorVeterinario(TransactionsManager.getConnection(), veterinario.getId(), veterinario.getHorarios());
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    // Los parametros que insertar_veterinario y modificar_veterinario comparten
    private void setParametrosComunes(CallableStatement cmd, Veterinario veterinario) throws SQLException {
        cmd.setInt("p_id_cuenta", veterinario.getCuenta().getId());
        cmd.setBoolean("p_activo", veterinario.isActivo());
        cmd.setString("p_dni", veterinario.getDni());
        cmd.setString("p_nombres", veterinario.getNombres());
        cmd.setString("p_apellido_paterno", veterinario.getApellidoPaterno());
        cmd.setString("p_apellido_materno", veterinario.getApellidoMaterno());
        cmd.setString("p_telefono", veterinario.getTelefono());
        cmd.setString("p_numero_colegiatura", veterinario.getNumeroColegiatura());
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

            new HorarioVeterinarioDAOImpl().eliminarPorVeterinario(TransactionsManager.getConnection(), id);
            ejecutarComandoEliminar(conn, id);
            if (propia) TransactionsManager.commit();
        } catch (SQLException | RuntimeException ex) {
            if (propia && TransactionsManager.activa()) TransactionsManager.rollback();
            throw ex;
        }
    }

    @Override
    protected Veterinario mapear(ResultSet rs) throws SQLException {
        Veterinario veterinario = new Veterinario();
        veterinario.setActivo(rs.getBoolean("activo"));
        veterinario.setId(rs.getInt("id_veterinario"));
        veterinario.setDni(rs.getString("dni"));
        veterinario.setNombres(rs.getString("nombres"));
        veterinario.setApellidoPaterno(rs.getString("apellido_paterno"));
        veterinario.setApellidoMaterno(rs.getString("apellido_materno"));
        veterinario.setTelefono(rs.getString("telefono"));
        veterinario.setNumeroColegiatura(rs.getString("numero_colegiatura"));

        // FK obligatoria: se resuelve el objeto completo
        int idCuenta = rs.getInt("id_cuenta");
        veterinario.setCuenta(new CuentaDAOImpl().findById(idCuenta));
        veterinario.setHorarios(new HorarioVeterinarioDAOImpl().listarPorVeterinario(veterinario.getId()));

        return veterinario;
    }

    @Override
    protected CallableStatement comandoListar(Connection conn) throws SQLException {
        CallableStatement cmd = conn.prepareCall("{call listar_veterinarios()}");
        return cmd;
    }

    @Override
    protected CallableStatement comandoBuscar(Connection conn, Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call buscar_veterinario_por_id(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected CallableStatement comandoInsertar(Connection conn, Veterinario modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call insertar_veterinario(?,?,?,?,?,?,?,?,?)}");
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
    protected CallableStatement comandoActualizar(Connection conn, Veterinario modelo) throws SQLException {
        if (modelo == null) throw new IllegalArgumentException("El modelo no puede ser nulo");
        CallableStatement cmd = conn.prepareCall("{call modificar_veterinario(?,?,?,?,?,?,?,?,?)}");
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
        CallableStatement cmd = conn.prepareCall("{call eliminar_veterinario(?)}");
        try {
            cmd.setInt("p_id", id);
            return cmd;
        } catch (SQLException | RuntimeException ex) {
            try { cmd.close(); } catch (SQLException cierre) { ex.addSuppressed(cierre); }
            throw ex;
        }
    }

    @Override
    protected void procesarResultadoInsertar(CallableStatement cmd, Veterinario modelo) throws SQLException {
        int id = cmd.getInt("p_id");
        if (cmd.wasNull() || id <= 0) throw new SQLException("No se obtuvo un ID válido para Veterinario.");
        modelo.setId(id);
    }
}
