package patitasarriba.bl.impl.producto;

import patitasarriba.bl.BLException;
import patitasarriba.bl.NoEncontradoException;
import patitasarriba.bl.ServicioBL;
import patitasarriba.bl.ValidacionException;
import patitasarriba.dao.ServicioDAO;
import patitasarriba.dao.impl.cita.DetalleCitaDAOImpl;
import patitasarriba.dao.impl.producto.ServicioDAOImpl;
import patitasarriba.modelo.producto.Servicio;

import java.sql.SQLException;
import java.util.List;

public class ServicioBLImpl implements ServicioBL {
    private final ServicioDAO servicioDAO = new ServicioDAOImpl();
    private final DetalleCitaDAOImpl detalleCitaDAO = new DetalleCitaDAOImpl();

    @Override
    public Servicio findByName(String name) throws BLException {
        if(name==null || name.isBlank()){
            throw new ValidacionException("El nombre no es valido");
        }
        try {
            // El DAO no filtra activo (lo necesita la validacion de unicos); la consulta solo
            // devuelve registros activos, igual que findById
            Servicio encontrado = servicioDAO.findByName(name);
            return encontrado != null && encontrado.isActivo() ? encontrado : null;
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
            Servicio servicio = servicioDAO.findById(integer);
            if (servicio == null) {
                throw new NoEncontradoException("No existe un servicio con id " + integer);
            }
            return servicio;
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el servicio", e);
        }
    }

    @Override
    public void insert(Servicio entidad) throws BLException {
        validar(entidad);
        validarNombreUnico(entidad);
        // activo es el borrado logico: todo registro nuevo se crea activo,
        // aunque el cliente mande "activo": false (para desactivar esta el delete)
        entidad.setActivo(true);
        try {
            servicioDAO.insert(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo insertar el servicio", e);
        }
    }

    @Override
    public void update(Servicio entidad) throws BLException {
        validar(entidad);
        validarExiste(entidad.getId());
        validarNombreUnico(entidad);
        // activo es el borrado logico: modificar nunca da de baja, aunque el cliente mande
        // "activo": false (para dar de baja esta el delete)
        entidad.setActivo(true);
        try {
            servicioDAO.update(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el servicio", e);
        }
    }

    @Override
    public void delete(Integer integer) throws BLException {
        validarExiste(integer);
        validarSinCitas (integer);
        try {
            servicioDAO.delete(integer);
        } catch (SQLException e) {
            throw new BLException("No se pudo eliminar el servicio", e);
        }
    }

    private void validar(Servicio servicio) throws BLException {
        if (servicio == null) {
            throw new ValidacionException("El servicio no puede ser nulo");
        }
        if (servicio.getNombre() == null || servicio.getNombre().isBlank()) {
            throw new ValidacionException("El nombre del servicio es obligatorio");
        }
        if (servicio.getTipo() == null ){
            throw new ValidacionException("El servicio debe tener un tipo de servicio");
        }
        if (servicio.getPrecioBase() <= 0) {
            throw new ValidacionException("El precio base debe ser mayor a 0");
        }
        if (servicio.getDescripcion() == null || servicio.getDescripcion().isBlank()){
            throw new ValidacionException("El servicio debe contar con una descripción");
        }
        if (servicio.getDuracionEstimada() != null && servicio.getDuracionEstimada() <= 0){
            throw new ValidacionException("La duración estimada debe ser mayor a 0");
        }
    }

    // Antes esta validacion traia TODAS las citas (con todos sus detalles) via citaDAO.findAll()
    // solo para recorrerlas en Java buscando una coincidencia por id_servicio. Ahora se delega el
    // filtro a la base de datos (existeDetalleConServicio -> listar_detalle_cita_por_servicio),
    // que como mucho devuelve las filas que coinciden y el DAO solo revisa si hay alguna,
    // sin mapear objetos DetalleCita completos.
    private void validarSinCitas(int idServicio) throws BLException {
        try {
            if (detalleCitaDAO.existeDetalleConServicio(idServicio)) {
                throw new ValidacionException("No se puede eliminar el servicio: está asignado a una cita");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las citas del servicio", e);
        }
    }

    // nombre es UNIQUE en la base. buscar_servicio_por_nombre no filtra activo:
    // detecta tambien servicios dados de baja
    private void validarNombreUnico(Servicio servicio) throws BLException {
        try {
            Servicio existente = servicioDAO.findByName(servicio.getNombre().trim());
            if (existente != null && existente.getId() != servicio.getId()) {
                throw new ValidacionException("Ya existe un servicio" + (existente.isActivo() ? "" : " dado de baja")
                        + " con el nombre '" + servicio.getNombre() + "'");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el nombre del servicio", e);
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (servicioDAO.findById(id) == null) {
                throw new NoEncontradoException("No existe un servicio con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia del servicio", e);
        }
    }
}
