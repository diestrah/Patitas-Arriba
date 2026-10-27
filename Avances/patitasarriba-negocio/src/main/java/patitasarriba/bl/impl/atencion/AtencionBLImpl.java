package patitasarriba.bl.impl.atencion;

import patitasarriba.bl.AtencionBL;
import patitasarriba.bl.BLException;
import patitasarriba.dao.ArticuloDAO;
import patitasarriba.dao.AtencionDAO;
import patitasarriba.dao.impl.atencion.AtencionDAOImpl;
import patitasarriba.dao.impl.producto.ArticuloDAOImpl;
import patitasarriba.dao.impl.receta.RecetaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.AtencionDiagnostico;
import patitasarriba.modelo.atencion.InsumoUtilizado;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.receta.Receta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AtencionBLImpl implements AtencionBL {

    private final AtencionDAO atencionDAO = new AtencionDAOImpl();
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();

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
            return atencionDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la atencion medica", e);
        }
    }

    @Override
    public void insert(Atencion atencion) throws BLException {
        validar(atencion);
        validarDetalleCitaLibre(atencion);

        TransactionsManager.iniciar();
        try {
            //para el descuento de stock
            for (InsumoUtilizado insumo : atencion.getInsumosUtilizados()) {
                moverStock(insumo.getArticulo().getId(), -insumo.getCantidadUtilizada());
            }
            atencionDAO.insert(atencion);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la atencion medica", e);
        }
    }

    @Override
    public void update(Atencion atencion) throws BLException {
        validar(atencion);
        validarDetalleCitaLibre(atencion);
        validarExiste(atencion.getId());

        TransactionsManager.iniciar();
        try {
            Atencion actual = atencionDAO.findById(atencion.getId());
            // Devuelve al stock lo que tenia antes
            for (InsumoUtilizado anterior : actual.getInsumosUtilizados()) {
                moverStock(anterior.getArticulo().getId(), anterior.getCantidadUtilizada());
            }
            // Descuenta lo que tiene ahora
            for (InsumoUtilizado nuevo : atencion.getInsumosUtilizados()) {
                moverStock(nuevo.getArticulo().getId(), -nuevo.getCantidadUtilizada());
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
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la atencion medica", e);
        }
    }

    //para que atencion no borre boletas
    @Override
    public void delete(Integer id) throws BLException {
        // 1. Verifica que la atención no tenga recetas (si no, las recetas dejarían de poder leerse)
        try {
            for (Receta receta : new RecetaDAOImpl().findAll()) {
                if (receta.getAtencion().getId() == id) {
                    throw new BLException("No se puede anular la atención: tiene la receta " + receta.getId());
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las recetas de la atención", e);
        }

        // 2. Lo que ya tenías
        TransactionsManager.iniciar();
        try {
            atencionDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la atencion medica", e);
        }
    }

    private void validar(Atencion atencion) throws BLException {
        if (atencion == null) {
            throw new BLException("La atencion medica no puede ser nula");
        }
        if (atencion.getDetalleCita() == null) {
            throw new BLException("La atencion medica debe estar asociada a un detalle de cita");
        }
        if (atencion.getFechaHora() == null) {
            throw new BLException("La atencion medica debe tener fecha y hora");
        }
        for (InsumoUtilizado insumo : atencion.getInsumosUtilizados()) {
            if (insumo == null || insumo.getArticulo() == null || insumo.getCantidadUtilizada() <= 0) {
                throw new BLException("Cada insumo utilizado debe tener un articulo y una cantidad mayor a 0");
            }
        }
        for (AtencionDiagnostico diagnostico : atencion.getDiagnosticos()) {
            if (diagnostico == null || diagnostico.getDiagnostico() == null || diagnostico.getNivelGravedad() == null) {
                throw new BLException("Cada diagnostico debe tener un diagnostico y un nivel de gravedad");
            }
        }
    }

    // En la BD id_detalle_cita es UNIQUE: un servicio de la cita tiene una sola atencion
    private void validarDetalleCitaLibre(Atencion atencion) throws BLException {
        try {
            for (Atencion otra : atencionDAO.findAll()) {
                if (otra.getId() != atencion.getId()
                        && otra.getDetalleCita() != null
                        && otra.getDetalleCita().getId() == atencion.getDetalleCita().getId()) {
                    throw new BLException("Ese servicio de la cita ya tiene una atencion registrada");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar si el servicio de la cita ya fue atendido", e);
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (atencionDAO.findById(id) == null) {
                throw new BLException("No existe una atencion medica con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la atencion medica", e);
        }
    }

    // cantidad positiva = devolver al stock, negativa = descontar
    private void moverStock(int idArticulo, int cantidad) throws SQLException {
        Articulo articulo = articuloDAO.findById(idArticulo);
        if (articulo == null) throw new SQLException("El articulo " + idArticulo + " no existe o esta inactivo");
        int nuevoStock = articulo.getStockActual() + cantidad;
        if (nuevoStock < 0) {
            throw new SQLException("Stock insuficiente de " + articulo.getNombre()
                    + " (disponible: " + articulo.getStockActual() + ")");
        }
        articulo.setStockActual(nuevoStock);
        articuloDAO.update(TransactionsManager.getConnection(), articulo);
    }
}
