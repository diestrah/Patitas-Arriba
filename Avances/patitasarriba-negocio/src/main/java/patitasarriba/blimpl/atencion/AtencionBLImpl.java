package patitasarriba.blimpl.atencion;

import patitasarriba.bl.atencion.AtencionBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.dao.producto.ArticuloDAO;
import patitasarriba.dao.atencion.AtencionDAO;
import patitasarriba.dao.boleta.BoletaDAO;
import patitasarriba.dao.atencion.DiagnosticoDAO;
import patitasarriba.daoimpl.atencion.AtencionDAOImpl;
import patitasarriba.daoimpl.atencion.DiagnosticoDAOImpl;
import patitasarriba.daoimpl.atencion.InsumoUtilizadoDAOImpl;
import patitasarriba.daoimpl.boleta.BoletaDAOImpl;
import patitasarriba.daoimpl.cita.DetalleCitaDAOImpl;
import patitasarriba.daoimpl.producto.ArticuloDAOImpl;
import patitasarriba.daoimpl.receta.RecetaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.AtencionDiagnostico;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.DetalleCita;
import patitasarriba.modelo.cita.EstadoCita;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.TipoServicio;
import patitasarriba.modelo.receta.Receta;

import java.sql.SQLException;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AtencionBLImpl implements AtencionBL {

    private final AtencionDAO atencionDAO = new AtencionDAOImpl();
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    private final DetalleCitaDAOImpl detalleCitaDAO = new DetalleCitaDAOImpl();
    private final DiagnosticoDAO diagnosticoDAO = new DiagnosticoDAOImpl();
    private final BoletaDAO boletaDAO = new BoletaDAOImpl();
    private final InsumoUtilizadoDAOImpl insumoUtilizadoDAO = new InsumoUtilizadoDAOImpl();

    @Override
    public List<Atencion> findAll() throws BLException {
        try {
            return atencionDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las atenciones medicas", e);
        }
    }

    @Override
    public Atencion findById(Integer id) throws BLException {
        try {
            Atencion atencion=atencionDAO.findById(id);
            if(atencion==null){
                /*
                * El SQLExeption solo lanza la excepcion si es que algo
                * salió mal tecnicamente, pero no valida el retorno de null, y como
                * lo vamos a necesitar en la capa REST para lanzar los codigos, pues es necesario
                * haré este cambio para todos los BLImpl :)
                * */
                throw new NoEncontradoException("No se encontró la atención con id ="+id);
            }
            return atencion;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la atencion medica", e);
        }
    }

    // InsumoUtilizadoDAOImpl.findById ya hace una sola consulta directa por id (procedure
    // buscar_insumo_utilizado_por_id); este metodo solo lo expone a quien no tiene acceso al DAO
    // (la capa REST), en vez de que esa capa tenga que cargar la atencion completa y recorrer
    // su lista de insumos a mano.
    @Override
    public InsumoUtilizado findInsumoById(Integer id) throws BLException {
        try {
            InsumoUtilizado insumo = insumoUtilizadoDAO.findById(id);
            if (insumo == null) {
                throw new NoEncontradoException("No existe un insumo utilizado con id " + id);
            }
            return insumo;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el insumo utilizado", e);
        }
    }

    @Override
    public List<Atencion> findByMascota(Integer idMascota) throws BLException {
        if (idMascota == null) throw new ValidacionException("El id de la mascota no puede ser nulo");
        List<Atencion> atenciones = new ArrayList<>();
        for (Atencion atencion : findAll()) {
            if (atencion.getDetalleCita().getCita().getMascota().getId() == idMascota) atenciones.add(atencion);
        }
        atenciones.sort(Comparator.comparing(Atencion::getFechaHora));
        return atenciones;
    }

    @Override
    public void insert(Atencion atencion) throws BLException {
        validar(atencion);
        validarDetalleCitaLibre(atencion);
        validarEstadoCita(atencion.getDetalleCita().getId());
        validarFechaAtencion(atencion);
        validarTipoServicio(atencion);
        validarDiagnosticosActivos(atencion);
        // activo es el borrado logico: registrar o modificar siempre deja el registro y sus lineas
        // activos, aunque el cliente mande "activo": false (para dar de baja esta el delete)
        marcarActiva(atencion);

        TransactionsManager.iniciar();
        try {
            //para el descuento de stock
            for (InsumoUtilizado insumo : atencion.getInsumosUtilizados()) {
                moverStock(insumo.getArticulo().getId(), -insumo.getCantidadUtilizada());
            }
            atencionDAO.insert(atencion);
            TransactionsManager.commit();
        } catch (BLException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw e;
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo insertar la atencion medica", e);
        }
    }

    @Override
    public void update(Atencion atencion) throws BLException {
        validar(atencion);
        validarExiste(atencion.getId());
        validarMismoDetalleCita(atencion);
        validarDetalleCitaLibre(atencion);
        validarEstadoCita(atencion.getDetalleCita().getId());
        validarFechaAtencion(atencion);
        validarTipoServicio(atencion);
        validarDiagnosticosActivos(atencion);
        validarInsumosFacturadosIntactos(atencion);
        // activo es el borrado logico: registrar o modificar siempre deja el registro y sus lineas
        // activos, aunque el cliente mande "activo": false (para dar de baja esta el delete)
        marcarActiva(atencion);

        TransactionsManager.iniciar();
        try {
            Atencion actual = atencionDAO.findById(atencion.getId());
            // Diferencia neta por articulo (lo que tenia antes menos lo que tiene ahora):
            // positiva = se devuelve al stock, negativa = se descuenta. Asi un insumo que no cambia
            // no mueve stock
            Map<Integer, Integer> neto = new LinkedHashMap<>();
            for (InsumoUtilizado anterior : actual.getInsumosUtilizados()) {
                neto.merge(anterior.getArticulo().getId(), anterior.getCantidadUtilizada(), Integer::sum);
            }
            for (InsumoUtilizado nuevo : atencion.getInsumosUtilizados()) {
                neto.merge(nuevo.getArticulo().getId(), -nuevo.getCantidadUtilizada(), Integer::sum);
            }
            for (Map.Entry<Integer, Integer> e : neto.entrySet()) {
                if (e.getValue() != 0) moverStock(e.getKey(), e.getValue());
            }
            // Los insumos que ya no vienen se mandan inactivos para que el DAO los desactive
            List<InsumoUtilizado> insumos = new ArrayList<>(atencion.getInsumosUtilizados());
            for (InsumoUtilizado anterior : actual.getInsumosUtilizados()) {
                boolean sigue = false;
                for (InsumoUtilizado nuevo : atencion.getInsumosUtilizados()) {
                    if (nuevo.getId() == anterior.getId()) {
                        sigue = true;
                        break;
                    }
                }
                if (!sigue) {
                    InsumoUtilizado quitado = new InsumoUtilizado(anterior);
                    quitado.setActivo(false);
                    insumos.add(quitado);
                }
            }
            atencion.setInsumosUtilizados(insumos);
            atencionDAO.update(atencion);
            TransactionsManager.commit();
        } catch (BLException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw e;
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la atencion medica", e);
        }
    }

    // Anular una atencion: sus insumos vuelven al stock y el servicio queda como no realizado
    // (no puede volver a atenderse, pero no impide completar la cita)
    @Override
    public void delete(Integer id) throws BLException {
        if (id == null) throw new ValidacionException("El id de la atención no puede ser nulo");
        Atencion actual;
        try {
            actual = atencionDAO.findById(id);
            if (actual == null) {
                throw new NoEncontradoException("No existe una atención activa con id " + id);
            }
            // Con recetas, las recetas quedarian colgando de una atencion anulada
            for (Receta receta : new RecetaDAOImpl().findAll()) {
                if (receta.getAtencion().getId() == id) {
                    throw new ValidacionException("No se puede anular la atención: tiene la receta " + receta.getId());
                }
            }
            // Un insumo ya cobrado no puede desaparecer de la atencion
            for (InsumoUtilizado insumo : actual.getInsumosUtilizados()) {
                if (boletaDAO.boletaConInsumo(insumo.getId()) != 0) {
                    throw new ValidacionException("No se puede anular la atención: el insumo "
                            + insumo.getArticulo().getNombre() + " ya fue facturado");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las recetas o boletas de la atención", e);
        }
        // Con la cita COMPLETADO la atencion queda cerrada
        validarEstadoCita(actual.getDetalleCita().getId());

        TransactionsManager.iniciar();
        try {
            // Los insumos de la atencion anulada vuelven al stock
            for (InsumoUtilizado insumo : actual.getInsumosUtilizados()) {
                moverStock(insumo.getArticulo().getId(), insumo.getCantidadUtilizada());
            }
            atencionDAO.delete(id);
            TransactionsManager.commit();
        } catch (BLException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw e;
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la atencion medica", e);
        }
    }

    private void marcarActiva(Atencion atencion) {
        atencion.setActivo(true);
        atencion.getInsumosUtilizados().forEach(i -> i.setActivo(true));
        atencion.getDiagnosticos().forEach(d -> d.setActivo(true));
    }

    private void validar(Atencion atencion) throws BLException {
        if (atencion == null) {
            throw new ValidacionException("La atencion medica no puede ser nula");
        }
        if (atencion.getDetalleCita() == null) {
            throw new ValidacionException("La atencion medica debe estar asociada a un detalle de cita");
        }
        if (atencion.getFechaHora() == null) {
            throw new ValidacionException("La atencion medica debe tener fecha y hora");
        }
        for (InsumoUtilizado insumo : atencion.getInsumosUtilizados()) {
            if (insumo == null || insumo.getArticulo() == null || insumo.getCantidadUtilizada() <= 0) {
                throw new ValidacionException("Cada insumo utilizado debe tener un articulo y una cantidad mayor a 0");
            }
        }
        for (AtencionDiagnostico diagnostico : atencion.getDiagnosticos()) {
            if (diagnostico == null || diagnostico.getDiagnostico() == null || diagnostico.getNivelGravedad() == null) {
                throw new ValidacionException("Cada diagnostico debe tener un diagnostico y un nivel de gravedad");
            }
        }
    }

    // En la BD id_detalle_cita es UNIQUE, incluso para atenciones anuladas
    private void validarDetalleCitaLibre(Atencion atencion) throws BLException {
        try {
            Atencion otra = atencionDAO.findByDetalleCita(atencion.getDetalleCita().getId());
            if (otra != null && otra.getId() != atencion.getId()) {
                throw new ValidacionException(otra.isActivo()
                        ? "Ese servicio de la cita ya tiene una atención registrada"
                        : "Ese servicio de la cita tuvo una atención que se anuló: no puede volver a atenderse");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar si el servicio de la cita ya fue atendido", e);
        }
    }

    // Solo se registra, modifica o anula una atencion mientras la cita esta APROBADO,
    // o PENDIENTE si es de emergencia (se atiende primero y el administrador la aprueba despues)
    private void validarEstadoCita(int idDetalleCita) throws BLException {
        try {
            DetalleCita detalle = detalleCitaDAO.buscarDetallePorId(idDetalleCita);
            if (detalle == null) {
                throw new ValidacionException("El servicio de la cita no existe o fue anulado");
            }
            Cita cita = detalle.getCita();
            boolean abierta = cita.getEstado() == EstadoCita.APROBADO
                    || (cita.getEstado() == EstadoCita.PENDIENTE && cita.isEsEmergencia());
            if (!abierta) {
                throw new ValidacionException("No se puede registrar ni modificar la atención: la cita está en estado "
                        + cita.getEstado() + (cita.getEstado() == EstadoCita.PENDIENTE
                        ? " (solo las emergencias se atienden antes de aprobarse)" : ""));
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el estado de la cita", e);
        }
    }

    // La atencion ocurre desde la hora de la cita y no puede registrarse a futuro
    private void validarFechaAtencion(Atencion atencion) throws BLException {
        try {
            DetalleCita detalle = detalleCitaDAO.buscarDetallePorId(atencion.getDetalleCita().getId());
            LocalDateTime fechaCita = detalle.getCita().getFechaHora();
            if (atencion.getFechaHora().isBefore(fechaCita)) {
                throw new ValidacionException("La atención no puede ser anterior a la cita (" + fechaCita + ")");
            }
            if (atencion.getFechaHora().isAfter(LocalDateTime.now())) {
                throw new ValidacionException("La fecha y hora de la atención no puede ser futura");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la fecha de la cita", e);
        }
    }

    // Una atencion no se mueve a otro servicio ni a otra cita
    private void validarMismoDetalleCita(Atencion atencion) throws BLException {
        try {
            Atencion actual = atencionDAO.findById(atencion.getId());
            if (actual.getDetalleCita().getId() != atencion.getDetalleCita().getId()) {
                throw new ValidacionException("No se puede cambiar el servicio de la cita al que pertenece una atención");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la atención", e);
        }
    }

    // Un insumo que ya se cobro en una boleta no se puede quitar ni cambiar
    private void validarInsumosFacturadosIntactos(Atencion atencion) throws BLException {
        try {
            Atencion actual = atencionDAO.findById(atencion.getId());
            for (InsumoUtilizado anterior : actual.getInsumosUtilizados()) {
                if (boletaDAO.boletaConInsumo(anterior.getId()) == 0) continue;
                InsumoUtilizado nuevo = atencion.getInsumosUtilizados().stream()
                        .filter(i -> i.getId() == anterior.getId()).findFirst().orElse(null);
                if (nuevo == null
                        || nuevo.getArticulo().getId() != anterior.getArticulo().getId()
                        || nuevo.getCantidadUtilizada() != anterior.getCantidadUtilizada()
                        || nuevo.isEsCobrable() != anterior.isEsCobrable()) {
                    throw new ValidacionException("El insumo " + anterior.getArticulo().getNombre()
                            + " ya fue facturado: no se puede quitar ni modificar");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los insumos facturados de la atención", e);
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (atencionDAO.findById(id) == null) {
                throw new NoEncontradoException("No existe una atencion medica con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la atencion medica", e);
        }
    }

    // Los diagnosticos de la atencion deben existir y estar activos en el catalogo
    private void validarDiagnosticosActivos(Atencion atencion) throws BLException {
        try {
            for (AtencionDiagnostico ad : atencion.getDiagnosticos()) {
                int id = ad.getDiagnostico().getId();
                if (diagnosticoDAO.findById(id) == null) {
                    throw new ValidacionException("El diagnóstico " + id + " no existe o está dado de baja");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los diagnósticos de la atención", e);
        }
    }

    // cantidad positiva = devolver al stock, negativa = descontar
    private void moverStock(int idArticulo, int cantidad) throws SQLException, BLException {
        Articulo articulo = articuloDAO.findById(idArticulo);
        if (articulo == null) throw new ValidacionException("El articulo " + idArticulo + " no existe o esta inactivo");
        int nuevoStock = articulo.getStockActual() + cantidad;
        if (nuevoStock < 0) {
            throw new ValidacionException("Stock insuficiente de " + articulo.getNombre()
                    + " (disponible: " + articulo.getStockActual() + ")");
        }
        articulo.setStockActual(nuevoStock);
        articuloDAO.update(TransactionsManager.getConnection(), articulo);
    }

    private void validarTipoServicio(Atencion atencion) throws BLException {
        try {
            DetalleCita detalle = detalleCitaDAO.buscarDetallePorId(atencion.getDetalleCita().getId());
            if (detalle == null) {
                throw new ValidacionException("El servicio de la cita no existe o fue anulado");
            }
            boolean esMedico = detalle.getServicio().getTipo() != TipoServicio.ESTETICA;
            if (esMedico && (atencion.getMotivoConsulta() == null || atencion.getMotivoConsulta().isBlank())) {
                throw new ValidacionException("El motivo de consulta es obligatorio en servicios médicos");
            }
            if (!esMedico && !atencion.getDiagnosticos().isEmpty()) {
                throw new ValidacionException("No se pueden registrar diagnósticos en un servicio estético");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el tipo de servicio de la atención", e);
        }
    }


}
