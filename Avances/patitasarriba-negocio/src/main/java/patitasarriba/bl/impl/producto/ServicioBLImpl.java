package patitasarriba.bl.impl.producto;

import patitasarriba.bl.BLException;
import patitasarriba.bl.ServicioBL;
import patitasarriba.dao.ServicioDAO;
import patitasarriba.dao.impl.producto.ServicioDAOImpl;
import patitasarriba.modelo.producto.Servicio;

import java.util.List;

public class ServicioBLImpl implements ServicioBL {
    private final ServicioDAO servicioDAO = new ServicioDAOImpl();
    @Override
    public Servicio findByName(String name) throws BLException {
        try {
            return servicioDAO.findByName(name);
        } catch (Exception e) {
            throw new BLException("No se pudo recuperar el servicio");
        }
    }

    @Override
    public List<Servicio> findAll() throws BLException {
        try {
            return servicioDAO.findAll();
        } catch (Exception e) {
            throw new BLException("No se pudieron listar los servicios");
        }
    }

    @Override
    public Servicio findById(Integer integer) throws BLException {
        try {
            return servicioDAO.findById(integer);
        } catch (Exception e) {
            throw new BLException("No se pudo recuperar el servicio");
        }
    }

    @Override
    public void insert(Servicio entidad) throws BLException {
        validar(entidad);
        try {
            servicioDAO.insert(entidad);
        } catch (Exception e) {
            throw new BLException("No se pudo insertar el servicio");
        }
    }

    @Override
    public void update(Servicio entidad) throws BLException {
        validar(entidad);
        try {
            servicioDAO.update(entidad);
        } catch (Exception e) {
            throw new BLException("No se pudo actualizar el servicio");
        }
    }

    @Override
    public void delete(Integer integer) throws BLException {
        try {
            servicioDAO.delete(integer);
        } catch (Exception e) {
            throw new BLException("No se pudo eliminar el servicio");
        }
    }

    private void validar(Servicio servicio) throws BLException {
        if (servicio.getNombre() == null) {
            throw new BLException("El nombre del servicio no puede ser nulo");
        }
        if (servicio.getTipo() == null ){
            throw new BLException("El servicio debe tener un tipo de servicio");
        }
        if (servicio.getPrecioBase() == 0.0) {
            throw new BLException("El precio base no puede ser 0");
        }
        if (servicio.getDescripcion() == null ){
            throw new BLException("El servicio debe contar con una descripción");
        }
        if (servicio.getDuracionEstimada() == 0){
            throw new BLException("La duración estimada del servicio no puede ser cero");
        }
    }
}
