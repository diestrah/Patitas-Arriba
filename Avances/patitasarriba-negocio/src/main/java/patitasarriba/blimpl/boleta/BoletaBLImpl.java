package patitasarriba.blimpl.boleta;

import java.sql.SQLException;
import java.util.List;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.bl.boleta.BoletaBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.dao.producto.ArticuloDAO;
import patitasarriba.dao.atencion.AtencionDAO;
import patitasarriba.dao.boleta.BoletaDAO;
import patitasarriba.dao.producto.ServicioDAO;
import patitasarriba.daoimpl.atencion.AtencionDAOImpl;
import patitasarriba.daoimpl.boleta.BoletaDAOImpl;
import patitasarriba.daoimpl.cita.DetalleCitaDAOImpl;
import patitasarriba.daoimpl.producto.ArticuloDAOImpl;
import patitasarriba.daoimpl.producto.ServicioDAOImpl;
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

    // Serie de las boletas: el número completo es B001-000123 (RN-BO-03)
    private static final String SERIE = "B001";

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
            Boleta boleta=boletaDAO.findById(id);
            if(boleta==null){
                throw new NoEncontradoException("No se pudo recuperar la boleta con id: "+ id);
            }
            return boleta;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la boleta", e);
        }
    }

    @Override
    public void insert(Boleta boleta) throws BLException {
        asignarNumeroSiFalta(boleta);
        // Validar datos, recalcular total y verificar disponibilidad
        validarYCalcular(boleta);
        validarNoRepetidos(boleta);
        validarDuenio(boleta);
        marcarActiva(boleta);
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
        obtenerActiva(boleta.getId());
        validarNoRepetidos(boleta);
        validarDuenio(boleta);
        marcarActiva(boleta);
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
        if (id == null) throw new ValidacionException("El id de la boleta no puede ser nulo");
        Boleta actual = obtenerActiva(id);
        TransactionsManager.iniciar();
        try {
            // Anular devuelve al stock lo vendido en caja. Los insumos no, porque se usaron en la atencion
            for (DetalleBoleta d : actual.getDetalles()) {
                if (d instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() == null) {
                    Articulo art = articuloDAO.findById(a.getArticulo().getId());
                    art.setStockActual(art.getStockActual() + a.getCantidad());
                    articuloDAO.update(TransactionsManager.getConnection(), art);
                }
            }
            boletaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la boleta", e);
        }
    }

    // activo es el borrado logico: emitir o modificar siempre deja la boleta y sus lineas activas,
    // aunque el cliente mande "activo": false (para anular esta el delete)
    private void marcarActiva(Boleta boleta) {
        boleta.setActivo(true);
        boleta.getDetalles().forEach(d -> d.setActivo(true));
    }

    // Una boleta anulada ya no se vuelve a anular (su stock ya se devolvio)
    private Boleta obtenerActiva(int id) throws BLException {
        try {
            Boleta actual = boletaDAO.findById(id);
            if (actual == null) {
                throw new NoEncontradoException("No existe una boleta activa con id " + id);
            }
            return actual;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la boleta", e);
        }
    }

    // Valida integridad de boleta, existencia de productos y recálculo de precios.
    private void validarYCalcular(Boleta boleta) throws BLException {

        //refactorizado para no tener una sábana de código
        validarDatosBasicos(boleta);

        double totalCalculado = 0.0;

        for (DetalleBoleta detalle : boleta.getDetalles()) {
            if (detalle == null) {
                throw new ValidacionException("La boleta no puede contener detalles nulos");
            }
            if (detalle.getCantidad() < 1) {
                throw new ValidacionException("La cantidad debe ser al menos 1");
            }

            double subTotalSeguro;

            try {
                // Enrutamiento polimórfico según el tipo de detalle
                if (detalle instanceof DetalleBoletaArticulo detalleArticulo) {
                    //refactorizado para no tener una sábana de código
                    subTotalSeguro = calcularSubtotalArticulo(boleta, detalleArticulo);
                } else if (detalle instanceof DetalleBoletaServicio detalleServicio) {
                    //refactorizado para no tener una sábana de código
                    subTotalSeguro = calcularSubtotalServicio(detalleServicio);
                } else {
                    throw new ValidacionException("Tipo de detalle de boleta no reconocido por el sistema");
                }
            } catch (SQLException e) {
                throw new BLException("Error técnico al consultar el catálogo de productos", e);
            }

            if (subTotalSeguro <= 0) {
                throw new ValidacionException("El subtotal de cada detalle debe ser mayor a 0");
            }
            detalle.setPrecioUnitario(subTotalSeguro / detalle.getCantidad());
            // Actualización segura del subtotal
            detalle.setSubtotal(subTotalSeguro);
            totalCalculado += subTotalSeguro;
        }

        boleta.setTotal(totalCalculado);
    }

    // Valida los datos propios de la boleta (cliente, metodo de pago, fecha, serie), sin mirar los detalles
    private void validarDatosBasicos(Boleta boleta) throws BLException {
        if (boleta == null) {
            throw new ValidacionException("La boleta no puede ser nula");
        }
        if (boleta.getCliente() == null) {
            throw new ValidacionException("La boleta debe tener un cliente asociado");
        }
        if (boleta.getDetalles().isEmpty()) {
            throw new ValidacionException("La boleta debe tener al menos un detalle");
        }
        if (boleta.getMetodoPago() == null) {
            throw new ValidacionException("La boleta debe tener un método de pago");
        }
        if (boleta.getFecha() == null) {
            throw new ValidacionException("La boleta debe tener una fecha");
        }
        if (boleta.getSerieCorrelativo() == null || boleta.getSerieCorrelativo().isBlank()) {
            throw new ValidacionException("La boleta debe tener serie y correlativo");
        }
        if (!boleta.getSerieCorrelativo().matches("[A-Z]\\d{3}-\\d{6}")) {
            throw new ValidacionException("El número de boleta debe tener el formato B001-000123");
        }
    }

    // Valida un detalle de articulo (venta en caja o cobro de un insumo) y devuelve su subtotal
    private double calcularSubtotalArticulo(Boleta boleta, DetalleBoletaArticulo detalleArticulo) throws BLException, SQLException {
        if (detalleArticulo.getArticulo() == null) {
            throw new ValidacionException("Cada detalle de articulo debe tener un articulo");
        }

        Articulo artReal = articuloDAO.findById(detalleArticulo.getArticulo().getId());

        if (artReal == null || !artReal.isActivo()) {
            throw new ValidacionException("El artículo no existe o está inactivo en el catálogo");
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
                throw new ValidacionException("Stock insuficiente para el artículo: " + artReal.getNombre()
                        + " (Stock disponible: " + disponible + ")");
            }
        } else {
            // Insumo de atencion: no se revisa stock (ya se desconto al registrar la atencion)
            validarInsumo(detalleArticulo);
        }

        return artReal.getPrecioBase() * detalleArticulo.getCantidad();
    }

    // Valida un detalle de servicio (cobro de un servicio de una cita) y devuelve su subtotal
    private double calcularSubtotalServicio(DetalleBoletaServicio detalleServicio) throws BLException, SQLException {
        if (detalleServicio.getDetalleCita() == null || detalleServicio.getDetalleCita().getServicio() == null) {
            throw new ValidacionException("Cada detalle de servicio debe estar asociado a un servicio de una cita");
        }
        if (detalleServicio.getCantidad() != 1) {
            throw new ValidacionException("Cada servicio de la cita se cobra una sola vez (cantidad 1)");
        }

        Servicio servReal = servicioDAO.findById(detalleServicio.getDetalleCita().getServicio().getId());

        if (servReal == null || !servReal.isActivo()) {
            throw new ValidacionException("El servicio no existe o está inactivo en el catálogo");
        }
        // Solo se cobra un servicio de la cita que ya tiene su atencion registrada (y activa)
        Atencion atencion = atencionDAO.findByDetalleCita(detalleServicio.getDetalleCita().getId());
        if (atencion == null || !atencion.isActivo()) {
            throw new ValidacionException("El servicio " + servReal.getNombre()
                    + " aún no fue atendido: solo se cobran servicios con atención registrada");
        }
        return servReal.getPrecioBase() * detalleServicio.getCantidad();
    }

    // Reglas del PDF: el insumo debe ser cobrable, del mismo articulo y sin superar lo usado
    private void validarInsumo(DetalleBoletaArticulo linea) throws BLException, SQLException {
        Atencion atencion = atencionDAO.findById(linea.getInsumoUtilizado().getAtencion().getId());
        if (atencion == null) {
            throw new ValidacionException("La atención del insumo no existe o fue anulada");
        }
        InsumoUtilizado real = null;
        for (InsumoUtilizado i : atencion.getInsumosUtilizados()) {
            if (i.getId() == linea.getInsumoUtilizado().getId()) {
                real = i;
                break;
            }
        }
        if (real == null) {
            throw new ValidacionException("El insumo no existe o fue quitado de la atención");
        }
        if (!real.isEsCobrable()) {
            throw new ValidacionException("El insumo " + real.getArticulo().getNombre() + " no es cobrable");
        }
        if (linea.getArticulo().getId() != real.getArticulo().getId()) {
            throw new ValidacionException("El artículo de la línea no coincide con el del insumo");
        }
        if (linea.getCantidad() > real.getCantidadUtilizada()) {
            throw new ValidacionException("No se puede cobrar más de lo usado en la atención ("
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

    // RN-BO-03: si la boleta llega sin número, recibe el siguiente de la serie (B001-000123 -> B001-000124).
    // El procedure cuenta también las anuladas, así un número nunca se repite
    private void asignarNumeroSiFalta(Boleta boleta) throws BLException {
        if (boleta == null || (boleta.getSerieCorrelativo() != null && !boleta.getSerieCorrelativo().isBlank())) {
            return;
        }
        try {
            int ultimo = boletaDAO.obtenerUltimoCorrelativo(SERIE);
            boleta.setSerieCorrelativo(String.format("%s-%06d", SERIE, ultimo + 1));
        } catch (SQLException e) {
            throw new BLException("No se pudo obtener el número de la boleta", e);
        }
    }

    private void validarNoRepetidos(Boleta boleta) throws BLException {
        try {
            int otra = boletaDAO.boletaConSerie(boleta.getSerieCorrelativo());
            if (otra != 0 && otra != boleta.getId()) {
                throw new ValidacionException("Ya existe una boleta con el número " + boleta.getSerieCorrelativo());
            }
            for (DetalleBoleta detalle : boleta.getDetalles()) {
                if (detalle instanceof DetalleBoletaServicio s) {
                    otra = boletaDAO.boletaConServicio(s.getDetalleCita().getId());
                    if (otra != 0 && otra != boleta.getId()) {
                        throw new ValidacionException("El servicio " + s.getDetalleCita().getServicio().getNombre()
                                + " ya fue facturado (la boleta pudo haberse anulado)");
                    }
                } else if (detalle instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() != null) {
                    otra = boletaDAO.boletaConInsumo(a.getInsumoUtilizado().getId());
                    if (otra != 0 && otra != boleta.getId()) {
                        throw new ValidacionException("El insumo " + a.getArticulo().getNombre()
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
                    if (dc == null) throw new ValidacionException("El servicio de la cita no existe o fue anulado");
                    cita = dc.getCita();
                } else if (d instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() != null) {
                    Atencion at = atencionDAO.findById(a.getInsumoUtilizado().getAtencion().getId());
                    if (at == null) throw new ValidacionException("La atención del insumo no existe o fue anulada");
                    cita = at.getDetalleCita().getCita();
                }
                if (cita != null && cita.getMascota().getCliente().getId() != boleta.getCliente().getId()) {
                    throw new ValidacionException("La boleta debe emitirse al dueño de la mascota " + cita.getMascota().getNombre());
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el dueño de la mascota", e);
        }
    }
}