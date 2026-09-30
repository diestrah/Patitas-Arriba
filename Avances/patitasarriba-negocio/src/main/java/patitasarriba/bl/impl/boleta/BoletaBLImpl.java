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
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Producto;
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

    /**
     * Validaciones de negocio antes de persistir.
     */
    private void validar(Boleta boleta) throws BLException {
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

            Producto prodFrontend = detalle.getProducto();
            if (prodFrontend == null) {
                throw new BLException("Cada detalle debe tener un producto");
            }

            double subTotalSeguro = 0.0;

            try {
                // Enrutamiento polimórfico para búsqueda de producto
                if (prodFrontend instanceof Articulo) {

                    // Validaciones específicas para Artículos (incluye stock)
                    Articulo artReal = articuloDAO.findById(prodFrontend.getId());

                    if (artReal == null || !artReal.isActivo()) {
                        throw new BLException("El artículo no existe o está inactivo en el catálogo");
                    }
                    if (artReal.getStockActual() < detalle.getCantidad()) {
                        throw new BLException("Stock insuficiente para el artículo: " + artReal.getNombre()
                                + " (Stock disponible: " + artReal.getStockActual() + ")");
                    }
                    subTotalSeguro = artReal.getPrecioBase() * detalle.getCantidad();

                } else if (prodFrontend instanceof Servicio) {

                    // Validaciones específicas para Servicios
                    Servicio servReal = servicioDAO.findById(prodFrontend.getId());

                    if (servReal == null || !servReal.isActivo()) {
                        throw new BLException("El servicio no existe o está inactivo en el catálogo");
                    }
                    subTotalSeguro = servReal.getPrecioBase() * detalle.getCantidad();

                } else {
                    throw new BLException("Tipo de producto no reconocido por el sistema");
                }

                // Actualización segura del subtotal
                detalle.setSubTotal(subTotalSeguro);
                totalCalculado += subTotalSeguro;

            } catch (SQLException e) {
                throw new BLException("Error técnico al consultar el catálogo de productos", e);
            }
            if (detalle.getSubTotal() <= 0) {
                throw new BLException("El subtotal de cada detalle debe ser mayor a 0");
            }
            totalCalculado += detalle.getSubTotal();
        }

        boleta.setTotal(totalCalculado);
    }

    // Descuenta la cantidad vendida del stock actual de los artículos.
    private void descontarStock(Boleta boleta) throws SQLException {
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            Producto prod = detalle.getProducto();

            // Filtro para aplicar actualización solo a Artículos
            if (prod instanceof Articulo) {
                Articulo art = (Articulo) prod;

                // Cálculo de nuevo stock
                int nuevoStock = art.getStockActual() - detalle.getCantidad();
                art.setStockActual(nuevoStock);

                // Actualización en base de datos
                articuloDAO.update(art);
            }
        }
    }
}
