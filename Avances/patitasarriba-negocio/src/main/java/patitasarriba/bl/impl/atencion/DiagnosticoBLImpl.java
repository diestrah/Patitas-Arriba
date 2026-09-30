package patitasarriba.bl.impl.atencion;

import patitasarriba.bl.BLException;
import patitasarriba.bl.DiagnosticoBL;
import patitasarriba.dao.DiagnosticoDAO;
import patitasarriba.dao.impl.atencion.DiagnosticoDAOImpl;
import patitasarriba.modelo.atencion.Diagnostico;

import java.sql.SQLException;
import java.util.List;

public class DiagnosticoBLImpl implements DiagnosticoBL {
    private final DiagnosticoDAO diagnosticoDAO = new DiagnosticoDAOImpl();

    @Override
    public Diagnostico findByNombreEnfermedad(String nombreEnfermedad) throws BLException {
        if (nombreEnfermedad == null || nombreEnfermedad.isBlank()) {
            throw new BLException("El nombre de la enfermedad no puede ser nulo o vacío");
        }
        try {
            return diagnosticoDAO.findByNombreEnfermedad(nombreEnfermedad);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el diagnostico", e);
        }
    }

    @Override
    public List<Diagnostico> findAll() throws BLException {
        try {
            return diagnosticoDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los diagnosticos");
        }
    }

    @Override
    public Diagnostico findById(Integer integer) throws BLException {
        try {
            return diagnosticoDAO.findById(integer);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el diagnostico", e);
        }
    }

    @Override
    public void insert(Diagnostico entidad) throws BLException {
        validarNombreEnfermedad(entidad);
        try {
            diagnosticoDAO.insert(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo insertar el diagnostico", e);
        }
    }

    @Override
    public void update(Diagnostico entidad) throws BLException {
        validarNombreEnfermedad(entidad);
        validarExiste(entidad.getId());
        try {
            diagnosticoDAO.update(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el diagnostico", e);
        }
    }

    @Override
    public void delete(Integer integer) throws BLException {
        try {
            diagnosticoDAO.delete(integer);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar el diagnostico", e);
        }
    }

    private void validarNombreEnfermedad(Diagnostico diagnostico) throws BLException {
        if (diagnostico == null) {
            throw new BLException("El diagnostico no puede ser nulo");
        }
        if (diagnostico.getNombreEnfermedad() == null
                || diagnostico.getNombreEnfermedad().isBlank()) {
            throw new BLException("El nombre de la enfermedad no puede ser nulo o vacío");
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (diagnosticoDAO.findById(id) == null) {
                throw new BLException("No existe un diagnostico con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia del diagnostico", e);
        }
    }
}
