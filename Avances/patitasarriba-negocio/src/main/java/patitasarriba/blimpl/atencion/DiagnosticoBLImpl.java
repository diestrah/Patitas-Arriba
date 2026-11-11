package patitasarriba.blimpl.atencion;

import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.atencion.DiagnosticoBL;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.dao.atencion.AtencionDAO;
import patitasarriba.dao.atencion.DiagnosticoDAO;
import patitasarriba.daoimpl.atencion.AtencionDAOImpl;
import patitasarriba.daoimpl.atencion.DiagnosticoDAOImpl;
import patitasarriba.modelo.atencion.Atencion;
import patitasarriba.modelo.atencion.AtencionDiagnostico;
import patitasarriba.modelo.atencion.Diagnostico;

import java.sql.SQLException;
import java.util.List;

public class DiagnosticoBLImpl implements DiagnosticoBL {
    private final DiagnosticoDAO diagnosticoDAO = new DiagnosticoDAOImpl();
    private final AtencionDAO atencionDAO = new AtencionDAOImpl();

    @Override
    public Diagnostico findByNombreEnfermedad(String nombreEnfermedad) throws BLException {
        if (nombreEnfermedad == null || nombreEnfermedad.isBlank()) {
            throw new ValidacionException("El nombre de la enfermedad no puede ser nulo o vacío");
        }
        try {
            // El DAO no filtra activo (lo necesita la validacion de unicos); la consulta solo
            // devuelve registros activos, igual que findById
            Diagnostico encontrado = diagnosticoDAO.findByNombreEnfermedad(nombreEnfermedad);
            return encontrado != null && encontrado.isActivo() ? encontrado : null;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el diagnostico", e);
        }
    }

    @Override
    public List<Diagnostico> findAll() throws BLException {
        try {
            return diagnosticoDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los diagnosticos", e);
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
        validarNombreEnfermedad(entidad);
        validarNombreUnico(entidad);
        // activo es el borrado logico: todo registro nuevo se crea activo,
        // aunque el cliente mande "activo": false (para desactivar esta el delete)
        entidad.setActivo(true);
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
        validarNombreUnico(entidad);
        // activo es el borrado logico: modificar nunca da de baja, aunque el cliente mande
        // "activo": false (para dar de baja esta el delete)
        entidad.setActivo(true);
        try {
            diagnosticoDAO.update(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el diagnostico", e);
        }
    }


    @Override
    public void delete(Integer integer) throws BLException {
        validarExiste(integer);
        try {
            for (Atencion atencion : atencionDAO.findAll()) {
                for (AtencionDiagnostico ad : atencion.getDiagnosticos()) {
                    if (ad.getDiagnostico().getId() == integer) {
                        throw new ValidacionException("No se puede eliminar: el diagnostico está asignado a la atención "
                                + atencion.getId());
                    }
                }
            }
            diagnosticoDAO.delete(integer);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar el diagnostico", e);
        }
    }
    private void validarNombreEnfermedad(Diagnostico diagnostico) throws BLException {
        if (diagnostico == null) {
            throw new ValidacionException("El diagnostico no puede ser nulo");
        }
        if (diagnostico.getNombreEnfermedad() == null
                || diagnostico.getNombreEnfermedad().isBlank()) {
            throw new ValidacionException("El nombre de la enfermedad no puede ser nulo o vacío");
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (diagnosticoDAO.findById(id) == null) {
                throw new NoEncontradoException("No existe un diagnostico con id " + id);
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
                throw new ValidacionException("Ya existe un diagnostico" + (existente.isActivo() ? "" : " dado de baja")
                        + " llamado '" + diagnostico.getNombreEnfermedad() + "'");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el nombre del diagnostico", e);
        }
    }
}
