package patitasarriba.bl.impl.atencion;

import patitasarriba.bl.BLException;
import patitasarriba.bl.TratamientoBL;
import patitasarriba.dao.TratamientoDAO;
import patitasarriba.dao.impl.atencion.TratamientoDAOImpl;
import patitasarriba.modelo.atencion.Tratamiento;

import java.sql.SQLException;
import java.util.List;

public class TratamientoBLImpl implements TratamientoBL {
    private final TratamientoDAO tratamientoDAO = new TratamientoDAOImpl();

    @Override
    public Tratamiento findByName(String nombre) throws BLException {
        if (nombre == null || nombre.isBlank()) {
            throw new BLException("El nombre del tratamiento no puede ser nulo o vacío");
        }
        try {
            return tratamientoDAO.findByName(nombre);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el tratamiento", e);
        }
    }

    @Override
    public List<Tratamiento> findAll() throws BLException {
        try {
            return tratamientoDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los tratamientos");
        }
    }

    @Override
    public Tratamiento findById(Integer integer) throws BLException {
        try {
            return tratamientoDAO.findById(integer);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el tratamiento", e);
        }
    }

    @Override
    public void insert(Tratamiento entidad) throws BLException {
        validarNombre(entidad);
        try {
            tratamientoDAO.insert(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo insertar el tratamiento", e);
        }
    }

    @Override
    public void update(Tratamiento entidad) throws BLException {
        validarNombre(entidad);
        validarExiste(entidad.getId());
        try {
            tratamientoDAO.update(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el tratamiento", e);
        }
    }

    @Override
    public void delete(Integer integer) throws BLException {
        try {
            tratamientoDAO.delete(integer);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar el tratamiento", e);
        }
    }

    private void validarNombre(Tratamiento tratamiento) throws BLException {
        if (tratamiento == null) {
            throw new BLException("El tratamiento no puede ser nulo");
        }
        if (tratamiento.getNombreProcedimiento() == null
                || tratamiento.getNombreProcedimiento().isBlank()) {
            throw new BLException("El nombre no puede ser nulo");
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (tratamientoDAO.findById(id) == null) {
                throw new BLException("No existe un articulo con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia del tratamiento", e);
        }
    }
}
