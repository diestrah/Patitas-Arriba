package patitasarriba.bl.impl.producto;

import patitasarriba.bl.BLException;
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
        validarNombreUnico(entidad);
        try {
            servicioDAO.insert(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo insertar el servicio", e);
        }
    }

    @Override
    public void update(Servicio entidad) throws BLException {
        validar(entidad);
        validarNombreUnico(entidad);
        try {
            servicioDAO.update(entidad);
        } catch (SQLException e) {
            throw new BLException("No se pudo actualizar el servicio", e);
        }
    }

    @Override
    public void delete(Integer integer) throws BLException {
        validarSinCitas (integer);
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

    // Antes esta validacion traia TODAS las citas (con todos sus detalles) via citaDAO.findAll()
    // solo para recorrerlas en Java buscando una coincidencia por id_servicio. Ahora se delega el
    // filtro a la base de datos (existeDetalleConServicio -> listar_detalle_cita_por_servicio),
    // que como mucho devuelve las filas que coinciden y el DAO solo revisa si hay alguna,
    // sin mapear objetos DetalleCita completos.
    private void validarSinCitas(int idServicio) throws BLException {
        try {
            if (detalleCitaDAO.existeDetalleConServicio(idServicio)) {
                throw new BLException("No se puede eliminar el servicio: está asignado a una cita");
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
                throw new BLException("Ya existe un servicio" + (existente.isActivo() ? "" : " dado de baja")
                        + " con el nombre '" + servicio.getNombre() + "'");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el nombre del servicio", e);
        }
    }

}
