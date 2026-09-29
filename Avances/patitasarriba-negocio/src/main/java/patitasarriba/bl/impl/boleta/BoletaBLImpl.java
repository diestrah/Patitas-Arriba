package patitasarriba.bl.impl.boleta;

import patitasarriba.bl.BLException;
import patitasarriba.bl.BoletaBL;
import patitasarriba.dao.BoletaDAO;
import patitasarriba.dao.impl.boleta.BoletaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.boleta.Boleta;
import patitasarriba.modelo.boleta.DetalleBoleta;

import java.sql.SQLException;
import java.util.List;

public class BoletaBLImpl implements BoletaBL {
    private final BoletaDAO boletaDAO = new BoletaDAOImpl();

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
        validar(boleta);

        TransactionsManager.iniciar();
        try {
            boletaDAO.insert(boleta);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar la boleta", e);
        }
    }

    @Override
    public void update(Boleta boleta) throws BLException {
        validar(boleta);

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
            if (detalle.getCantidad() < 1) {
                throw new BLException("La cantidad de cada detalle debe ser al menos 1");
            }
            if (detalle.getProducto() == null) {
                throw new BLException("Cada detalle debe tener un producto asociado");
            }
            totalCalculado += detalle.getSubTotal();
        }

        boleta.setTotal(totalCalculado);
    }
}
