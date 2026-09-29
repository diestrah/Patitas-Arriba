package patitasarriba.dao.impl.boleta;

import patitasarriba.dao.BoletaDAO;
import patitasarriba.dao.impl.RegistroDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoleta;
import patitasarriba.modelo.boleta.MetodoPago;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Servicio;
import patitasarriba.modelo.usuario.Cliente;
import conexion.DBManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BoletaDAOImpl extends RegistroDAOImpl<Boleta> implements BoletaDAO {

    private final DetalleBoletaArtDAO detalleArtDAO = new DetalleBoletaArtDAOImpl();
    private final DetalleBoletaServDAO detalleServDAO = new DetalleBoletaServDAOImpl();

    @Override
    public List<Boleta> findAll() throws SQLException {
        String sql = "{call listar_boletas()}";

        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql);
             ResultSet rs = cmd.executeQuery()) {

            List<Boleta> boletas = new ArrayList<>();
            while (rs.next()) {
                boletas.add(mapear(rs, new Boleta()));
            }
            return boletas;
        }
    }

    @Override
    public Boleta findById(Integer id) throws SQLException {
        String sql = "{call buscar_boleta_por_id(?)}";

        try (Connection conn = DBManager.getInstance().getConnection();
             CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);
            try (ResultSet rs = cmd.executeQuery()) {
                return rs.next() ? mapear(rs, new Boleta()) : null;
            }
        }
    }

    @Override
    public void insert(Boleta boleta) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call insertar_boleta(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cliente", boleta.getCliente().getId());
            cmd.setBoolean("p_activo", boleta.isActivo());
            cmd.setDate("p_fecha", java.sql.Date.valueOf(boleta.getFecha()));
            cmd.setDouble("p_total", boleta.getTotal());
            cmd.setString("p_metodo_pago", mapearMetodoPagoABD(boleta.getMetodoPago()));
            cmd.registerOutParameter("p_id", Types.INTEGER);

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo insertar la boleta");
            }

            boleta.setId(cmd.getInt("p_id"));
        }

        // Enrutar los detalles según su tipo concreto (instanceof)
        insertarDetallesPorTipo(boleta);
    }

    @Override
    public void update(Boleta boleta) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        String sql = "{call modificar_boleta(?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id_cliente", boleta.getCliente().getId());
            cmd.setBoolean("p_activo", boleta.isActivo());
            cmd.setDate("p_fecha", java.sql.Date.valueOf(boleta.getFecha()));
            cmd.setDouble("p_total", boleta.getTotal());
            cmd.setString("p_metodo_pago", mapearMetodoPagoABD(boleta.getMetodoPago()));
            cmd.setInt("p_id", boleta.getId());

            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo actualizar la boleta");
            }
        }

        // Eliminar detalles anteriores y reinsertar
        detalleArtDAO.deleteDetallesPorBoleta(boleta.getId());
        detalleServDAO.deleteDetallesPorBoleta(boleta.getId());
        insertarDetallesPorTipo(boleta);
    }

    @Override
    public void delete(Integer id) throws SQLException {
        Connection conn = TransactionsManager.getConnection();

        // Primero eliminar los detalles (hijos antes que padre)
        detalleArtDAO.deleteDetallesPorBoleta(id);
        detalleServDAO.deleteDetallesPorBoleta(id);

        String sql = "{call eliminar_boleta(?)}";
        try (CallableStatement cmd = conn.prepareCall(sql)) {
            cmd.setInt("p_id", id);
            if (cmd.executeUpdate() == 0) {
                throw new SQLException("No se pudo eliminar la boleta");
            }
        }
    }

    // -----------------------------------------------------------------------
    // Métodos privados
    // -----------------------------------------------------------------------

    /**
     * Enruta la inserción de cada detalle al DAO correcto según el tipo
     * concreto del Producto (instanceof Articulo o Servicio).
     */
    private void insertarDetallesPorTipo(Boleta boleta) throws SQLException {
        List<DetalleBoleta> detallesArt = new ArrayList<>();
        List<DetalleBoleta> detallesServ = new ArrayList<>();

        for (DetalleBoleta detalle : boleta.getDetalles()) {
            if (detalle.getProducto() instanceof Articulo) {
                detallesArt.add(detalle);
            } else if (detalle.getProducto() instanceof Servicio) {
                detallesServ.add(detalle);
            }
        }

        if (!detallesArt.isEmpty()) {
            detalleArtDAO.insertDetalles(boleta.getId(), detallesArt);
        }
        if (!detallesServ.isEmpty()) {
            detalleServDAO.insertDetalles(boleta.getId(), detallesServ);
        }
    }

    /**
     * Mapea un ResultSet de BOLETA a un objeto Boleta.
     * Fusiona los detalles de ambas tablas (ART + SERV) en una sola lista.
     */
    @Override
    protected Boleta mapear(ResultSet rs, Boleta boleta) throws SQLException {
        boleta.setId(rs.getInt("ID_BOLETA"));
        boleta.setActivo(rs.getBoolean("ACTIVO"));
        boleta.setFecha(rs.getDate("FECHA").toLocalDate());
        boleta.setTotal(rs.getDouble("TOTAL"));
        boleta.setMetodoPago(mapearMetodoPagoDesdeDB(rs.getString("METODO_PAGO")));

        // Mapear cliente (solo con id)
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("ID_CLIENTE"));
        boleta.setCliente(cliente);

        // Fusión de detalles: consultar ambas tablas y unificar en una sola lista
        List<DetalleBoleta> todosLosDetalles = new ArrayList<>();
        todosLosDetalles.addAll(detalleArtDAO.findByBoletaId(boleta.getId()));
        todosLosDetalles.addAll(detalleServDAO.findByBoletaId(boleta.getId()));
        boleta.setDetalles(todosLosDetalles);

        return boleta;
    }

    /**
     * Convierte el enum MetodoPago de Java al valor del ENUM de MySQL.
     */
    private String mapearMetodoPagoABD(MetodoPago metodoPago) {
        switch (metodoPago) {
            case EFECTIVO:
                return "Efectivo";
            case TARJETA_DE_CREDITO:
                return "Tarjeta de credito";
            default:
                return "Efectivo";
        }
    }

    /**
     * Convierte el valor del ENUM de MySQL al enum MetodoPago de Java.
     */
    private MetodoPago mapearMetodoPagoDesdeDB(String valor) {
        switch (valor) {
            case "Efectivo":
                return MetodoPago.EFECTIVO;
            case "Tarjeta de credito":
                return MetodoPago.TARJETA_DE_CREDITO;
            default:
                return MetodoPago.EFECTIVO;
        }
    }
}
