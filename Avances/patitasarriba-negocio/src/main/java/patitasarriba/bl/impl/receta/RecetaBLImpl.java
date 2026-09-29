package patitasarriba.bl.impl.receta;

import patitasarriba.bl.BLException;
import patitasarriba.bl.RecetaBL;
import patitasarriba.dao.RecetaDAO;
import patitasarriba.dao.impl.receta.RecetaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.receta.Receta;

import java.sql.SQLException;
import java.util.List;

public class RecetaBLImpl implements RecetaBL {
    private final RecetaDAO recetaDAO = new RecetaDAOImpl();

    @Override
    public List<Receta> findAll() throws BLException {
        try {
            return recetaDAO.findAll();
        } catch (Exception e) {
            throw new BLException("No se pudo listar las recetas");
        }
    }

    @Override
    public Receta findById(Integer integer) throws BLException {
        try {
            return recetaDAO.findById(integer);
        } catch (Exception e) {
            throw new BLException("No se pudo recuperar la receta");
        }
    }

    @Override
    public void insert(Receta entidad) throws BLException {
        validar(entidad);
        TransactionsManager.iniciar();
        try {
            recetaDAO.insert(entidad);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo insertar la receta");
        }
    }

    @Override
    public void update(Receta entidad) throws BLException {
        validar(entidad);

        TransactionsManager.iniciar();
        try {
            recetaDAO.update(entidad);
            TransactionsManager.commit();
        } catch (Exception e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la receta");
        }
    }

    @Override
    public void delete(Integer integer) throws BLException {
        TransactionsManager.iniciar();
        try {
            recetaDAO.delete(integer);
            TransactionsManager.commit();
        } catch (Exception e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la receta");
        }
    }

    private void validar(Receta receta) throws BLException {
        if (receta.getFechaEmision() == null){
            throw new BLException("La receta debe tener una fecha");
        }
        if (receta.getAtencionMedica() == null){
            throw new BLException("La receta debe estar asociada a una atención médica");
        }
        if (receta.getDetalles().isEmpty()) {
            throw new BLException("La receta debe tener al menos un detalle");
        }
        if (receta.getIndicacionesGenerales() == null) {
            throw new BLException("La receta debe tener indicaciones");
        }
    }
}
