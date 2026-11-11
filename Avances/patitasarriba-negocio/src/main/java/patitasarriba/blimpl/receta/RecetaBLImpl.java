package patitasarriba.blimpl.receta;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.receta.RecetaBL;
import patitasarriba.dao.producto.ArticuloDAO;
import patitasarriba.dao.atencion.AtencionDAO;
import patitasarriba.dao.receta.RecetaDAO;
import patitasarriba.daoimpl.atencion.AtencionDAOImpl;
import patitasarriba.daoimpl.producto.ArticuloDAOImpl;
import patitasarriba.daoimpl.receta.RecetaDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.producto.TipoServicio;
import patitasarriba.modelo.receta.Receta;
import patitasarriba.modelo.receta.DetalleReceta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RecetaBLImpl implements RecetaBL {
    private final RecetaDAO recetaDAO = new RecetaDAOImpl();
    private final AtencionDAO atencionDAO = new AtencionDAOImpl();
    private final ArticuloDAO articuloDAO = new ArticuloDAOImpl();
    @Override
    public List<Receta> findAll() throws BLException {
        try {
            return recetaDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las recetas", e);
        }
    }

    @Override
    public Receta findById(Integer id) throws BLException {
        try {
            Receta receta = recetaDAO.findById(id);
            if(receta==null){
                throw new NoEncontradoException("No se pudo encontrar la receta con el id="+id);
            }
            return receta;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar la receta", e);
        }
    }

    @Override
    public List<Receta> findByAtencion(Integer idAtencion) throws BLException {
        if (idAtencion == null) throw new ValidacionException("El id de la atención no puede ser nulo");
        List<Receta> recetas = new ArrayList<>();
        for (Receta receta : findAll()) {
            if (receta.getAtencion().getId() == idAtencion) recetas.add(receta);
        }
        return recetas;
    }

    @Override
    public void insert(Receta entidad) throws BLException {
        validar(entidad);
        validarExistentes(entidad);
        validarServicioMedico (entidad);

        // activo es el borrado logico: todo registro nuevo se crea activo,
        // aunque el cliente mande "activo": false (para desactivar esta el delete)
        entidad.setActivo(true);
        entidad.getDetalles().forEach(d -> d.setActivo(true));
        TransactionsManager.iniciar();
        try {
            recetaDAO.insert(entidad);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo insertar la receta", e);
        }
    }

    @Override
    public void update(Receta entidad) throws BLException {
        validar(entidad);
        validarExiste(entidad.getId());
        validarExistentes(entidad);
        validarServicioMedico (entidad);
        // activo es el borrado logico: modificar nunca da de baja, aunque el cliente mande
        // "activo": false (para dar de baja esta el delete)
        entidad.setActivo(true);
        entidad.getDetalles().forEach(d -> d.setActivo(true));

        TransactionsManager.iniciar();
        try {
            recetaDAO.update(entidad);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar la receta", e);
        }
    }

    @Override
    public void delete(Integer integer) throws BLException {
        validarExiste(integer);
        TransactionsManager.iniciar();
        try {
            recetaDAO.delete(integer);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar la receta", e);
        }
    }

    private void validar(Receta receta) throws BLException {
        if (receta == null) {
            throw new ValidacionException("La receta no puede ser nula");
        }
        if (receta.getFechaEmision() == null){
            throw new ValidacionException("La receta debe tener una fecha");
        }
        if (receta.getAtencion() == null){
            throw new ValidacionException("La receta debe estar asociada a una atención médica");
        }
        if (receta.getDetalles() == null || receta.getDetalles().isEmpty()) {
            throw new ValidacionException("La receta debe tener al menos un detalle");
        }
        if (receta.getIndicacionesGenerales() == null || receta.getIndicacionesGenerales().isBlank()) {
            throw new ValidacionException("La receta debe tener indicaciones");
        }
        for (DetalleReceta detalle : receta.getDetalles()) {
            if (detalle == null || detalle.getArticulo() == null
                    || detalle.getDosis() == null || detalle.getDosis().isBlank()
                    || detalle.getFrecuencia() == null || detalle.getFrecuencia().isBlank()
                    || detalle.getDuracionDias() <= 0 || detalle.getCantidadTotal() <= 0) {
                throw new ValidacionException("Cada detalle de receta debe tener articulo, dosis, frecuencia, duración y cantidad válidos");
            }
        }
    }
    private void validarExistentes(Receta receta) throws BLException {
        try {
            if (atencionDAO.findById(receta.getAtencion().getId()) == null) {
                throw new ValidacionException("La atención no existe o fue anulada");
            }
            for (DetalleReceta detalle : receta.getDetalles()) {
                if (articuloDAO.findById(detalle.getArticulo().getId()) == null) {
                    throw new ValidacionException("El artículo " + detalle.getArticulo().getNombre()
                            + " no existe o está dado de baja");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la atención o los artículos de la receta", e);
        }
    }

    private void validarServicioMedico(Receta receta) throws BLException {
        try {
            Atencion atencion = atencionDAO.findById(receta.getAtencion().getId());
            if (atencion.getDetalleCita().getServicio().getTipo() == TipoServicio.ESTETICA) {
                throw new ValidacionException("No se pueden emitir recetas en servicios estéticos");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el tipo de servicio de la atención", e);
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (recetaDAO.findById(id) == null) {
                throw new NoEncontradoException("No existe una receta con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la receta", e);
        }
    }
}
