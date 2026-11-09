package patitasarriba.bl.impl.boleta;

import patitasarriba.bl.util.Activos;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import patitasarriba.bl.BLException;
import patitasarriba.bl.BoletaBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.dao.ArticuloDAO;
import patitasarriba.dao.AtencionDAO;
import patitasarriba.dao.ClienteDAO;
import patitasarriba.dao.impl.usuario.ClienteDAOImpl;
import patitasarriba.dao.BoletaDAO;
import patitasarriba.dao.impl.atencion.AtencionDAOImpl;
import patitasarriba.dao.impl.boleta.BoletaDAOImpl;
import patitasarriba.dao.impl.cita.DetalleCitaDAOImpl;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
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
import patitasarriba.modelo.usuario.Cliente;

public class BoletaBLImpl implements BoletaBL {

    private final BoletaDAO boletaDAO = new BoletaDAOImpl();
    private static final String SERIE = "B001";
    // Inicialización de DAOs concretos para la validación de productos
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final DetalleCitaDAOImpl detalleCitaDAO = new DetalleCitaDAOImpl();
    private final AtencionDAO atencionDAO = new AtencionDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();

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
        Activos.marcar(boleta);
        if (boleta != null) Activos.marcarTodos(boleta.getDetalles());
        // RN-BO-03: el numero lo asigna el sistema (siguiente correlativo de la serie)
        if (boleta != null) boleta.setSerieCorrelativo(siguienteNumero());
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
        Activos.marcar(boleta);
        if (boleta != null) Activos.marcarTodos(boleta.getDetalles());
        if (boleta == null) throw new BLException("La boleta no puede ser nula");
        // RN-BO-03: el numero de una boleta emitida no cambia
        boleta.setSerieCorrelativo(obtenerActiva(boleta.getId()).getSerieCorrelativo());
        validarYCalcular(boleta);
        validarNoRepetidos(boleta);
        validarDuenio(boleta);
        TransactionsManager.iniciar();
        try {
            Boleta actual = boletaDAO.findById(boleta.getId());
            // Devuelve al stock lo que la boleta habia vendido en caja
            for (DetalleBoleta d : actual.getDetalles()) {
                if (d instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() == null) {
                    // Devolver no exige que el articulo siga activo (pudo darse de baja despues)
                    articuloDAO.ajustarStock(TransactionsManager.getConnection(), a.getArticulo().getId(), a.getCantidad());
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
        if (id == null) throw new BLException("El id de la boleta no puede ser nulo");
        Boleta actual = obtenerActiva(id);
        TransactionsManager.iniciar();
        try {
            // RN-BO-17: anular devuelve al stock lo vendido en caja. Los insumos no, porque se
            // usaron en la atencion; y servicios e insumos no pueden volver a cobrarse (RN-BO-10/13)
            for (DetalleBoleta d : actual.getDetalles()) {
                if (d instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() == null) {
                    articuloDAO.ajustarStock(TransactionsManager.getConnection(), a.getArticulo().getId(), a.getCantidad());
                }
            }
            boletaDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la boleta", e);
        }
    }

    @Override
    public Boleta prepararCobro(Integer idCliente) throws BLException {
        try {
            Cliente cliente = clienteDAO.findById(idCliente);
            if (cliente == null) {
                throw new NoEncontradoException("No existe un cliente activo con id " + idCliente);
            }
            Boleta borrador = new Boleta();
            borrador.setCliente(cliente);
            borrador.setFecha(LocalDateTime.now());
            double total = 0;
            for (Atencion atencion : atencionDAO.findAll()) {
                DetalleCita servicio = atencion.getDetalleCita();
                if (servicio.getCita().getMascota().getCliente().getId() != idCliente) continue;
                if (boletaDAO.boletaConServicio(servicio.getId()) == 0) {
                    double precio = servicio.getServicio().getPrecioBase();
                    borrador.agregarDetalleServicio(0, true, 1, precio, servicio);
                    total += precio;
                }
                for (InsumoUtilizado insumo : atencion.getInsumosUtilizados()) {
                    if (!insumo.isEsCobrable() || boletaDAO.boletaConInsumo(insumo.getId()) != 0) continue;
                    double precio = insumo.getArticulo().getPrecioBase();
                    borrador.agregarDetalleArticulo(0, true, insumo.getCantidadUtilizada(), precio,
                            insumo.getArticulo(), insumo);
                    total += precio * insumo.getCantidadUtilizada();
                }
            }
            borrador.setTotal(total);
            return borrador;
        } catch (SQLException e) {
            throw new BLException("No se pudo preparar el cobro del cliente", e);
        }
    }

    // Siguiente numero de la serie unica de boletas, por ejemplo B001-000124
    private String siguienteNumero() throws BLException {
        try {
            return String.format("%s-%06d", SERIE, boletaDAO.ultimoCorrelativo(SERIE) + 1);
        } catch (SQLException e) {
            throw new BLException("No se pudo generar el número de la boleta", e);
        }
    }

    // Una boleta anulada ya no se modifica ni se vuelve a anular
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
                throw new BLException("La boleta no puede contener detalles nulos");
            }
            if (detalle.getCantidad() < 1) {
                throw new BLException("La cantidad debe ser al menos 1");
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

    // Valida los datos propios de la boleta (cliente, metodo de pago, fecha, serie), sin mirar los detalles
    private void validarDatosBasicos(Boleta boleta) throws BLException {
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
    }

    // Valida un detalle de articulo (venta en caja o cobro de un insumo) y devuelve su subtotal
    private double calcularSubtotalArticulo(Boleta boleta, DetalleBoletaArticulo detalleArticulo) throws BLException, SQLException {
        if (detalleArticulo.getArticulo() == null) {
            throw new BLException("Cada detalle de articulo debe tener un articulo");
        }

        // Venta en caja: solo articulos activos del catalogo. Insumo: ya es parte del historial de una
        // atencion, asi que se cobra aunque el articulo se haya dado de baja despues (RN-BO-08)
        boolean esVentaEnCaja = detalleArticulo.getInsumoUtilizado() == null;
        int idArticulo = detalleArticulo.getArticulo().getId();
        Articulo artReal = esVentaEnCaja ? articuloDAO.findById(idArticulo) : articuloDAO.findByIdHistorico(idArticulo);

        if (artReal == null) {
            throw new BLException(esVentaEnCaja ? "El artículo no existe o está inactivo en el catálogo"
                    : "El artículo del insumo no existe");
        }

        if (esVentaEnCaja) {
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

        return artReal.getPrecioBase() * detalleArticulo.getCantidad();
    }

    // Valida un detalle de servicio (cobro de un servicio de una cita) y devuelve su subtotal
    private double calcularSubtotalServicio(DetalleBoletaServicio detalleServicio) throws BLException, SQLException {
        if (detalleServicio.getDetalleCita() == null) {
            throw new BLException("Cada detalle de servicio debe estar asociado a un servicio de una cita");
        }
        if (detalleServicio.getCantidad() != 1) {
            throw new BLException("Cada servicio de la cita se cobra una sola vez (cantidad 1)");
        }
        // RN-BO-05: el servicio (y su precio) se toma del DetalleCita guardado, nunca del que llega en
        // la peticion. El DAO ya lo carga con findByIdHistorico, asi que se cobra aunque el servicio se
        // haya dado de baja del catalogo despues (RN-BO-08)
        DetalleCita real = detalleCitaDAO.buscarDetallePorId(detalleServicio.getDetalleCita().getId());
        if (real == null) {
            throw new BLException("El servicio de la cita no existe o fue quitado de la cita");
        }
        // RN-BO-16: solo se cobra un DetalleCita que ya tiene su atencion registrada (y activa)
        Atencion atencion = atencionDAO.findByDetalleCita(real.getId());
        if (atencion == null || !atencion.isActivo()) {
            throw new BLException("El servicio " + real.getServicio().getNombre()
                    + " aún no fue atendido: solo se cobran servicios con atención registrada");
        }
        return real.getServicio().getPrecioBase() * detalleServicio.getCantidad();
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
                // RN-AR-04: descuento atomico en la BD, con la conexion de la transaccion activa
                // (evita autobloqueo con el candado del insert del detalle por la FK hacia ARTICULO).
                // Si otra venta se llevo el stock entre la validacion y este punto, falla y se revierte todo
                if (!articuloDAO.ajustarStock(TransactionsManager.getConnection(),
                        detalleArticulo.getArticulo().getId(), -detalle.getCantidad())) {
                    throw new SQLException("Stock insuficiente de " + detalleArticulo.getArticulo().getNombre()
                            + ": otra operación lo consumió mientras se emitía la boleta");
                }
            }
        }
    }

    private void validarNoRepetidos(Boleta boleta) throws BLException {
        // RN-BO-18: dentro de la misma boleta, cada DetalleCita e insumo se cobra en una sola linea
        Set<Integer> detallesCita = new HashSet<>();
        Set<Integer> insumos = new HashSet<>();
        for (DetalleBoleta detalle : boleta.getDetalles()) {
            if (detalle instanceof DetalleBoletaServicio s && !detallesCita.add(s.getDetalleCita().getId())) {
                throw new BLException("La boleta cobra dos veces el mismo servicio de la cita");
            }
            if (detalle instanceof DetalleBoletaArticulo a && a.getInsumoUtilizado() != null
                    && !insumos.add(a.getInsumoUtilizado().getId())) {
                throw new BLException("La boleta cobra dos veces el mismo insumo de la atención");
            }
        }
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
            // RN-BO-04: el cliente debe existir y estar activo, aunque la boleta sea solo de venta en caja
            if (clienteDAO.findById(boleta.getCliente().getId()) == null) {
                throw new BLException("El cliente de la boleta no existe o está dado de baja");
            }
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