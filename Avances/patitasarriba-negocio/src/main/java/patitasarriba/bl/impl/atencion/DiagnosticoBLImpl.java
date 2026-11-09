package patitasarriba.bl.impl.atencion;

import patitasarriba.bl.util.Activos;
import patitasarriba.bl.BLException;
import patitasarriba.bl.DiagnosticoBL;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ValidacionException;
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
            throw new ValidacionException("El nombre de la enfermedad no puede ser nulo o vacío");
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
    public Diagnostico findById(Integer id) throws BLException {
        try {
            Diagnostico diagnostico = diagnosticoDAO.findById(id);
            if(diagnostico==null){
                throw new NoEncontradoException("No se encontro el diagnostico con id :"+id);
            }
            return diagnostico;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el diagnostico", e);
        }
    }

    @Override
    public void insert(Diagnostico entidad) throws BLException {
        Activos.marcar(entidad);
        validarNombreEnfermedad(entidad);
        validarNombreUnico(entidad);
        try {
            diagnosticoDAO.insert(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo insertar el diagnostico", e);
        }
    }

    @Override
    public void update(Diagnostico entidad) throws BLException {
        Activos.marcar(entidad);
        validarNombreEnfermedad(entidad);
        validarExiste(entidad.getId());
        validarNombreUnico(entidad);
        try {
            diagnosticoDAO.update(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el diagnostico", e);
        }
    }


    @Override
    public void delete(Integer integer) throws BLException {
        // Se puede dar de baja aunque este en atenciones: el historial se sigue leyendo,
        // pero ya no se puede usar en atenciones nuevas
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
        // descripcion es NOT NULL en la BD
        if (diagnostico.getDescripcion() == null || diagnostico.getDescripcion().isBlank()) {
            throw new BLException("La descripción del diagnóstico es obligatoria");
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

    // nombre_enfermedad es UNIQUE en la base. buscar_diagnostico_por_nombre_enfermedad no filtra activo:
    // detecta tambien diagnosticos dados de baja
    private void validarNombreUnico(Diagnostico diagnostico) throws BLException {
        try {
            Diagnostico existente = diagnosticoDAO.findByNombreEnfermedad(diagnostico.getNombreEnfermedad().trim());
            if (existente != null && existente.getId() != diagnostico.getId()) {
                throw new BLException("Ya existe un diagnostico" + (existente.isActivo() ? "" : " dado de baja")
                        + " llamado '" + diagnostico.getNombreEnfermedad() + "'");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el nombre del diagnostico", e);
        }
    }
}
