package patitasarriba.bl.impl.boleta;

import java.sql.SQLException;
import java.util.List;

import patitasarriba.bl.BLException;
import patitasarriba.bl.BoletaBL;
import patitasarriba.dao.ArticuloDAO;
import patitasarriba.dao.BoletaDAO;
import patitasarriba.dao.ServicioDAO;
import patitasarriba.dao.impl.boleta.BoletaDAOImpl;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.impl.producto.ServicioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoleta;
import patitasarriba.modelo.boleta.DetalleBoletaArticulo;
import patitasarriba.modelo.boleta.DetalleBoletaServicio;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Servicio;

public class BoletaBLImpl implements BoletaBL {

    private final BoletaDAO boletaDAO = new BoletaDAOImpl();
    // Inicialización de DAOs concretos para la validación de productos
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final ServicioDAO servicioDAO = new ServicioDAOImpl();

    @Override
    public List<Boleta> findAll() throws BLException {
        try {
            return boletaDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las boletas", e);
        }
    }

    @Override
    public Boleta findById(Integer id) throws BLException {
        try {
            return boletaDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la boleta", e);
        }
    }

    @Override
    public void insert(Boleta boleta) throws BLException {
        // Validar datos, recalcular total y verificar disponibilidad
        validarYCalcular(boleta);

        TransactionsManager.iniciar();
        try {
            // Guardar boleta y sus detalles
            boletaDAO.insert(boleta);

            // Actualizar inventario para productos físicos
            descontarStock(boleta);

            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la boleta", e);
        }
    }

    @Override
    public void update(Boleta boleta) throws BLException {
        validarYCalcular(boleta);

        TransactionsManager.iniciar();
        try {
            boletaDAO.update(boleta);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la boleta", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            boletaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la boleta", e);
        }
    }

    // Valida integridad de boleta, existencia de productos y recálculo de precios.
    private void validarYCalcular(Boleta boleta) throws BLException {
        if (boleta == null) {
            throw new BLException("La boleta no puede ser nula");
        }
        if (boleta.getCliente() == null) {
            throw new BLException("La boleta debe tener un cliente asociado");
        }

        if (boleta.getDetalles().isEmpty()) {
            throw new BLException("La boleta debe tener al menos un detalle");
        }

        if (boleta.getMetodoPago() == null) {
            throw new BLException("La boleta debe tener un método de pago");
        }

        if (boleta.getFecha() == null) {
            throw new BLException("La boleta debe tener una fecha");
        }

        double totalCalculado = 0.0;

        for (DetalleBoleta detalle : boleta.getDetalles()) {
            if (detalle == null) {
                throw new BLException("La boleta no puede contener detalles nulos");
            }
            if (detalle.getCantidad() < 1) {
                throw new BLException("La cantidad debe ser al menos 1");
            }

            double subTotalSeguro;

            try {
                // Enrutamiento polimórfico según el tipo de detalle
                if (detalle instanceof DetalleBoletaArticulo detalleArticulo) {
                    if (detalleArticulo.getArticulo() == null) {
                        throw new BLException("Cada detalle de articulo debe tener un articulo");
                    }

                    // Validaciones específicas para Artículos (incluye stock)
                    Articulo artReal = articuloDAO.findById(detalleArticulo.getArticulo().getId());

                    if (artReal == null || !artReal.isActivo()) {
                        throw new BLException("El artículo no existe o está inactivo en el catálogo");
                    }
                    if (artReal.getStockActual() < detalle.getCantidad()) {
                        throw new BLException("Stock insuficiente para el artículo: " + artReal.getNombre()
                                + " (Stock disponible: " + artReal.getStockActual() + ")");
                    }
                    subTotalSeguro = artReal.getPrecioBase() * detalle.getCantidad();

                } else if (detalle instanceof DetalleBoletaServicio detalleServicio) {
                    if (detalleServicio.getDetalleCita() == null || detalleServicio.getDetalleCita().getServicio() == null) {
                        throw new BLException("Cada detalle de servicio debe estar asociado a un servicio de una cita");
                    }

                    // Validaciones específicas para Servicios
                    Servicio servReal = servicioDAO.findById(detalleServicio.getDetalleCita().getServicio().getId());

                    if (servReal == null || !servReal.isActivo()) {
                        throw new BLException("El servicio no existe o está inactivo en el catálogo");
                    }
                    subTotalSeguro = servReal.getPrecioBase() * detalle.getCantidad();

                } else {
                    throw new BLException("Tipo de detalle de boleta no reconocido por el sistema");
                }

            } catch (SQLException e) {
                throw new BLException("Error técnico al consultar el catálogo de productos", e);
            }

            if (subTotalSeguro <= 0) {
                throw new BLException("El subtotal de cada detalle debe ser mayor a 0");
            }

            // Actualización segura del subtotal
            detalle.setSubtotal(subTotalSeguro);
            totalCalculado += subTotalSeguro;
        }

        boleta.setTotal(totalCalculado);
    }

    // Descuenta la cantidad vendida del stock actual de los artículos.
    private void descontarStock(Boleta boleta) throws SQLException {
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            // Filtro para aplicar actualización solo a Artículos
            if (detalle instanceof DetalleBoletaArticulo detalleArticulo) {
                Articulo art = detalleArticulo.getArticulo();

                // Cálculo de nuevo stock
                int nuevoStock = art.getStockActual() - detalle.getCantidad();
                art.setStockActual(nuevoStock);

                // Actualización en base de datos: usa la misma conexion de la
                // transaccion activa (evita autobloqueo con el candado que ya
                // sostiene el insert del detalle por la FK hacia ARTICULO)
                articuloDAO.update(TransactionsManager.getConnection(), art);
            }
        }
    }
}
