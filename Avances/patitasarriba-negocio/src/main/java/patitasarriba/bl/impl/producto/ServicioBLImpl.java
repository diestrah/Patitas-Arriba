package patitasarriba.bl.impl.producto;

import patitasarriba.bl.BLException;
import patitasarriba.bl.ServicioBL;
import patitasarriba.dao.ServicioDAO;
import patitasarriba.dao.impl.producto.ServicioDAOImpl;
import patitasarriba.modelo.producto.Servicio;

import java.sql.SQLException;
import java.util.List;

public class ServicioBLImpl implements ServicioBL {
    private final ServicioDAO servicioDAO = new ServicioDAOImpl();
    @Override
    public Servicio findByName(String name) throws BLException {
        try {
            return servicioDAO.findByName(name);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el servicio", e);
        }
    }

    @Override
    public List<Servicio> findAll() throws BLException {
        try {
            return servicioDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudieron listar los servicios", e);
        }
    }

    @Override
    public Servicio findById(Integer integer) throws BLException {
        try {
            return servicioDAO.findById(integer);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el servicio", e);
        }
    }

    @Override
    public void insert(Servicio entidad) throws BLException {
        validar(entidad);
        try {
            servicioDAO.insert(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo insertar el servicio", e);
        }
    }

    @Override
    public void update(Servicio entidad) throws BLException {
        validar(entidad);
        try {
            servicioDAO.update(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el servicio", e);
        }
    }

    @Override
    public void delete(Integer integer) throws BLException {
        try {
            servicioDAO.delete(integer);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar el servicio", e);
        }
    }

    private void validar(Servicio servicio) throws BLException {
        if (servicio == null) {
            throw new BLException("El servicio no puede ser nulo");
        }
        if (servicio.getNombre() == null || servicio.getNombre().isBlank()) {
            throw new BLException("El nombre del servicio es obligatorio");
        }
        if (servicio.getTipo() == null ){
            throw new BLException("El servicio debe tener un tipo de servicio");
        }
        if (servicio.getPrecioBase() <= 0) {
            throw new BLException("El precio base debe ser mayor a 0");
        }
        if (servicio.getDescripcion() == null || servicio.getDescripcion().isBlank()){
            throw new BLException("El servicio debe contar con una descripción");
        }
        if (servicio.getDuracionEstimada() != null && servicio.getDuracionEstimada() <= 0){
            throw new BLException("La duración estimada debe ser mayor a 0");
        }
    }
}
