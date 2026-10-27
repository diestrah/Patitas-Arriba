package patitasarriba.dao.impl.boleta;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import conexion.DBManager;
import patitasarriba.dao.BoletaDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoleta;
import patitasarriba.modelo.boleta.MetodoPago;
import patitasarriba.modelo.boleta.DetalleBoletaArticulo;
import patitasarriba.modelo.boleta.DetalleBoletaServicio;
import patitasarriba.modelo.usuario.Cliente;

public class BoletaDAOImpl extends RegistroDAOImpl<Boleta> implements BoletaDAO {

    private final DetalleBoletaArtDAO detalleArtDAO = new DetalleBoletaArtDAOImpl();
    private final DetalleBoletaServDAO detalleServDAO = new DetalleBoletaServDAOImpl();

    @Override
    public List<Boleta> findAll() throws SQLException {
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call listar_boletas()}";

            try (CallableStatement cmd = conn.prepareCall(sql); ResultSet rs = cmd.executeQuery()) {

                List<Boleta> boletas = new ArrayList<>();
                while (rs.next()) {
                    boletas.add(mapear(rs, new Boleta()));
                }
                return boletas;
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public Boleta findById(Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        boolean cerrarConexion = !TransactionsManager.activa();
        Connection conn = cerrarConexion ? DBManager.getInstance().getConnection()
                : TransactionsManager.getConnection();
        try {
            String sql = "{call buscar_boleta_por_id(?)}";

            try (CallableStatement cmd = conn.prepareCall(sql)) {
                cmd.setInt("p_id", id);
                try (ResultSet rs = cmd.executeQuery()) {
                    return rs.next() ? mapear(rs, new Boleta()) : null;
                }
            }
        } finally {
            if (cerrarConexion) conn.close();
        }
    }

    @Override
    public void insert(Boleta boleta) throws SQLException {
        if (boleta == null) throw new IllegalArgumentException("La boleta no puede ser nula");
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            if (detalle.getId() != 0) throw new SQLException("Una boleta nueva requiere detalles nuevos");
        }
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_boleta(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cliente", boleta.getCliente().getId());
            cmd.setBoolean("p_activo", boleta.isActivo());
            cmd.setObject("p_fecha", boleta.getFecha());
            cmd.setString("p_serie_correlativo", boleta.getSerieCorrelativo());
            cmd.setDouble("p_total", boleta.getTotal());
            cmd.setString("p_metodo_pago", boleta.getMetodoPago().name());
            cmd.registerOutParameter("p_id", Types.INTEGER);

            cmd.execute();

            boleta.setId(cmd.getInt("p_id"));
        }

        // Enrutar los detalles según su tipo concreto (instanceof)
        insertarDetallesPorTipo(boleta);
    }

    @Override
    public void update(Boleta boleta) throws SQLException {
        if (boleta == null) throw new IllegalArgumentException("La boleta no puede ser nula");
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call modificar_boleta(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cliente", boleta.getCliente().getId());
            cmd.setBoolean("p_activo", boleta.isActivo());
            cmd.setObject("p_fecha", boleta.getFecha());
            cmd.setString("p_serie_correlativo", boleta.getSerieCorrelativo());
            cmd.setDouble("p_total", boleta.getTotal());
            cmd.setString("p_metodo_pago", boleta.getMetodoPago().name());
            cmd.setInt("p_id", boleta.getId());

            cmd.execute();
        }

        validarDetallesExistentes(boleta);
        // Desactivar ausentes y actualizar existentes conservando sus IDs.
        detalleArtDAO.deleteDetallesPorBoleta(boleta.getId());
        detalleServDAO.deleteDetallesPorBoleta(boleta.getId());
        insertarDetallesPorTipo(boleta);
    }

    @Override
    public void delete(Integer id) throws SQLException {
        if (id == null) throw new IllegalArgumentException("El id no puede ser nulo");
        Connection conn = TransactionsManager.getConnection();

        // Primero eliminar los detalles (hijos antes que padre)
        detalleArtDAO.deleteDetallesPorBoleta(id);
        detalleServDAO.deleteDetallesPorBoleta(id);

        String sql = "{call eliminar_boleta(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);
            cmd.execute();
        }
    }

    // -----------------------------------------------------------------------
    // Métodos privados
    // -----------------------------------------------------------------------
    // Enruta la inserción de cada detalle al DAO correcto según el tipo
    // concreto del detalle (articulo o servicio).
    private void insertarDetallesPorTipo(Boleta boleta) throws SQLException {
        List<DetalleBoleta> detallesArt = new ArrayList<>();
        List<DetalleBoleta> detallesServ = new ArrayList<>();

        for (DetalleBoleta detalle : boleta.getDetalles()) {
            if (detalle instanceof DetalleBoletaArticulo) {
                detallesArt.add(detalle);
            } else if (detalle instanceof DetalleBoletaServicio) {
                detallesServ.add(detalle);
            } else {
                throw new SQLException("Tipo de detalle de boleta no soportado");
            }
        }

        if (!detallesArt.isEmpty()) {
            detalleArtDAO.insertDetalles(boleta.getId(), detallesArt);
        }
        if (!detallesServ.isEmpty()) {
            detalleServDAO.insertDetalles(boleta.getId(), detallesServ);
        }
    }

    private void validarDetallesExistentes(Boleta boleta) throws SQLException {
        var articulos = new java.util.HashSet<Integer>();
        var servicios = new java.util.HashSet<Integer>();
        for (var detalle : detalleArtDAO.findByBoletaId(boleta.getId())) articulos.add(detalle.getId());
        for (var detalle : detalleServDAO.findByBoletaId(boleta.getId())) servicios.add(detalle.getId());
        for (var detalle : boleta.getDetalles()) {
            if (detalle.getId() == 0) continue;
            boolean valido = detalle instanceof DetalleBoletaArticulo
            ? articulos.remove(detalle.getId())
            : detalle instanceof DetalleBoletaServicio && servicios.remove(detalle.getId());
            if (!valido) throw new SQLException("Detalle duplicado o ajeno a la boleta: " + detalle.getId());
        }
    }

    // Mapea un ResultSet de BOLETA a un objeto Boleta.
    // Fusiona los detalles de ambas tablas (ART + SERV) en una sola lista.
    @Override
    protected Boleta mapear(ResultSet rs, Boleta boleta) throws SQLException {
        super.mapear(rs, boleta);
        boleta.setId(rs.getInt("ID_BOLETA"));
        boleta.setFecha(rs.getTimestamp("FECHA").toLocalDateTime());
        boleta.setTotal(rs.getDouble("TOTAL"));
        boleta.setSerieCorrelativo(rs.getString("serie_correlativo"));
        boleta.setMetodoPago(MetodoPago.valueOf(rs.getString("METODO_PAGO")));

        // Cargar cliente completo para las copias defensivas del modelo.
        Cliente cliente = new patitasarriba.dao.impl.usuario.ClienteDAOImpl().findById(rs.getInt("id_cliente"));
        boleta.setCliente(cliente);

        // Fusión de detalles: consultar ambas tablas y unificar en una sola lista
        List<DetalleBoleta> todosLosDetalles = new ArrayList<>();
        todosLosDetalles.addAll(detalleArtDAO.findByBoletaId(boleta.getId()));
        todosLosDetalles.addAll(detalleServDAO.findByBoletaId(boleta.getId()));
        boleta.setDetalles(todosLosDetalles);

        return boleta;
    }
}
