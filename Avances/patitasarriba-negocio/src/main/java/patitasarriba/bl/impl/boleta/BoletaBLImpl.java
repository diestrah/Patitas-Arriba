package patitasarriba.bl.impl.boleta;

import java.sql.SQLException;
import java.util.List;

import patitasarriba.bl.BLException;
import patitasarriba.bl.BoletaBL;
import patitasarriba.dao.ArticuloDAO;
import patitasarriba.dao.AtencionDAO;
import patitasarriba.dao.BoletaDAO;
import patitasarriba.dao.ServicioDAO;
import patitasarriba.dao.impl.atencion.AtencionDAOImpl;
import patitasarriba.dao.impl.boleta.BoletaDAOImpl;
import patitasarriba.dao.impl.cita.DetalleCitaDAOImpl;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.impl.producto.ServicioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoleta;
import patitasarriba.modelo.boleta.DetalleBoletaArticulo;
import patitasarriba.modelo.boleta.DetalleBoletaServicio;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.Servicio;

public class BoletaBLImpl implements BoletaBL {

    private final BoletaDAO boletaDAO = new BoletaDAOImpl();
    // Inicialización de DAOs concretos para la validación de productos
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final ServicioDAO servicioDAO = new ServicioDAOImpl();
    private final DetalleCitaDAOImpl detalleCitaDAO = new DetalleCitaDAOImpl();
    private final AtencionDAO atencionDAO = new AtencionDAOImpl();

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
        validarNoRepetidos(boleta);
        validarDuenio(boleta);
        TransactionsManager.iniciar();
        try {
            // Guardar boleta y sus detalles
            boletaDAO.insert(boleta);

            // Actualizar inventario para productos físicos
            descontarStock(boleta);

            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo insertar la boleta", e);
        }
    }

    @Override
    public void update(Boleta boleta) throws BLException {
        validarYCalcular(boleta);
        validarNoRepetidos(boleta);
        validarDuenio(boleta);
        TransactionsManager.iniciar();
        try {
            Boleta actual = boletaDAO.findById(boleta.getId());
            // Devuelve al stock lo que la boleta habia vendido en caja
            for (DetalleBoleta d : actual.getDetalles()) {
                if (d instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() == null) {
                    Articulo art = articuloDAO.findById(a.getArticulo().getId());
                    art.setStockActual(art.getStockActual() + a.getCantidad());
                    articuloDAO.update(TransactionsManager.getConnection(), art);
                }
            }
            boletaDAO.update(boleta);
            // Descuenta lo que vende ahora
            descontarStock(boleta);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la boleta", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            boletaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
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

        if (boleta.getSerieCorrelativo() == null || boleta.getSerieCorrelativo().isBlank()) {
            throw new BLException("La boleta debe tener serie y correlativo");
        }

        if (!boleta.getSerieCorrelativo().matches("[A-Z]\\d{3}-\\d{6}")) {
            throw new BLException("El número de boleta debe tener el formato B001-000123");
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

                    // Validaciones específicas para Artículos
                    Articulo artReal = articuloDAO.findById(detalleArticulo.getArticulo().getId());

                    if (artReal == null || !artReal.isActivo()) {
                        throw new BLException("El artículo no existe o está inactivo en el catálogo");
                    }

                    if (detalleArticulo.getInsumoUtilizado() == null) {
                        // Venta en caja: suma todas las lineas del mismo articulo, para que dos lineas
                        // (por ejemplo 3 + 3 con stock 5) no dejen el stock en negativo.
                        // Al modificar, cuenta tambien lo que esta boleta ya habia descontado.
                        int pedido = 0;
                        for (DetalleBoleta d : boleta.getDetalles()) {
                            if (d instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() == null
                                    && a.getArticulo().getId() == artReal.getId()) {
                                pedido += a.getCantidad();
                            }
                        }
                        int disponible = artReal.getStockActual() + cantidadYaVendida(boleta.getId(), artReal.getId());
                        if (pedido > disponible) {
                            throw new BLException("Stock insuficiente para el artículo: " + artReal.getNombre()
                                    + " (Stock disponible: " + disponible + ")");
                        }
                    } else {
                        // Insumo de atencion: no se revisa stock (ya se desconto al registrar la atencion)
                        validarInsumo(detalleArticulo);
                    }

                    subTotalSeguro = artReal.getPrecioBase() * detalle.getCantidad();

                } else if (detalle instanceof DetalleBoletaServicio detalleServicio) {
                    if (detalleServicio.getDetalleCita() == null || detalleServicio.getDetalleCita().getServicio() == null) {
                        throw new BLException("Cada detalle de servicio debe estar asociado a un servicio de una cita");
                    }
                    if (detalle.getCantidad() != 1) {
                        throw new BLException("Cada servicio de la cita se cobra una sola vez (cantidad 1)");
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
            detalle.setPrecioUnitario(subTotalSeguro / detalle.getCantidad());
            // Actualización segura del subtotal
            detalle.setSubtotal(subTotalSeguro);
            totalCalculado += subTotalSeguro;
        }

        boleta.setTotal(totalCalculado);
    }

    // Reglas del PDF: el insumo debe ser cobrable, del mismo articulo y sin superar lo usado
    private void validarInsumo(DetalleBoletaArticulo linea) throws BLException, SQLException {
        Atencion atencion = atencionDAO.findById(linea.getInsumoUtilizado().getAtencion().getId());
        if (atencion == null) {
            throw new BLException("La atención del insumo no existe o fue anulada");
        }
        InsumoUtilizado real = null;
        for (InsumoUtilizado i : atencion.getInsumosUtilizados()) {
            if (i.getId() == linea.getInsumoUtilizado().getId()) {
                real = i;
                break;
            }
        }
        if (real == null) {
            throw new BLException("El insumo no existe o fue quitado de la atención");
        }
        if (!real.isEsCobrable()) {
            throw new BLException("El insumo " + real.getArticulo().getNombre() + " no es cobrable");
        }
        if (linea.getArticulo().getId() != real.getArticulo().getId()) {
            throw new BLException("El artículo de la línea no coincide con el del insumo");
        }
        if (linea.getCantidad() > real.getCantidadUtilizada()) {
            throw new BLException("No se puede cobrar más de lo usado en la atención ("
                    + real.getCantidadUtilizada() + ")");
        }
    }

    // Unidades de un articulo vendidas en caja por esta boleta (0 si es una boleta nueva)
    private int cantidadYaVendida(int idBoleta, int idArticulo) throws SQLException {
        if (idBoleta == 0) return 0;
        Boleta actual = boletaDAO.findById(idBoleta);
        if (actual == null) return 0;
        int cantidad = 0;
        for (DetalleBoleta d : actual.getDetalles()) {
            if (d instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() == null
                    && a.getArticulo().getId() == idArticulo) {
                cantidad += a.getCantidad();
            }
        }
        return cantidad;
    }

    // Descuenta la cantidad vendida del stock actual de los artículos.
    private void descontarStock(Boleta boleta) throws SQLException {
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            // Solo ventas en caja: los insumos ya se descontaron en la atencion
            if (detalle instanceof DetalleBoletaArticulo detalleArticulo && detalleArticulo.getInsumoUtilizado() == null) {
                // Se relee el articulo de la BD: el DAO reescribe todos sus campos
                // y sus categorias, asi que no se puede usar el que viene del frontend
                Articulo art = articuloDAO.findById(detalleArticulo.getArticulo().getId());

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

    private void validarNoRepetidos(Boleta boleta) throws BLException {
        try {
            int otra = boletaDAO.boletaConSerie(boleta.getSerieCorrelativo());
            if (otra != 0 && otra != boleta.getId()) {
                throw new BLException("Ya existe una boleta con el número " + boleta.getSerieCorrelativo());
            }
            for (DetalleBoleta detalle : boleta.getDetalles()) {
                if (detalle instanceof DetalleBoletaServicio s) {
                    otra = boletaDAO.boletaConServicio(s.getDetalleCita().getId());
                    if (otra != 0 && otra != boleta.getId()) {
                        throw new BLException("El servicio " + s.getDetalleCita().getServicio().getNombre()
                                + " ya fue facturado (la boleta pudo haberse anulado)");
                    }
                } else if (detalle instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() != null) {
                    otra = boletaDAO.boletaConInsumo(a.getInsumoUtilizado().getId());
                    if (otra != 0 && otra != boleta.getId()) {
                        throw new BLException("El insumo " + a.getArticulo().getNombre()
                                + " ya fue cobrado (la boleta pudo haberse anulado)");
                    }
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar si la boleta repite datos ya facturados", e);
        }
    }

    // La boleta se emite al dueño de la mascota (servicios e insumos). Se relee desde la base.
    private void validarDuenio(Boleta boleta) throws BLException {
        try {
            for (DetalleBoleta d : boleta.getDetalles()) {
                Cita cita = null;
                if (d instanceof DetalleBoletaServicio s) {
                    DetalleCita dc = detalleCitaDAO.buscarDetallePorId(s.getDetalleCita().getId());
                    if (dc == null) throw new BLException("El servicio de la cita no existe o fue anulado");
                    cita = dc.getCita();
                } else if (d instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() != null) {
                    Atencion at = atencionDAO.findById(a.getInsumoUtilizado().getAtencion().getId());
                    if (at == null) throw new BLException("La atención del insumo no existe o fue anulada");
                    cita = at.getDetalleCita().getCita();
                }
                if (cita != null && cita.getMascota().getCliente().getId() != boleta.getCliente().getId()) {
                    throw new BLException("La boleta debe emitirse al dueño de la mascota " + cita.getMascota().getNombre());
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el dueño de la mascota", e);
        }
    }
}