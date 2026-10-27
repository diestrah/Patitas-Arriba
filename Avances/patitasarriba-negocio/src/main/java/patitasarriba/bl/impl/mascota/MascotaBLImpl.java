package patitasarriba.bl.impl.mascota;

import patitasarriba.bl.BLException;
import patitasarriba.bl.MascotaBL;
import patitasarriba.dao.CitaDAO;
import patitasarriba.dao.ClienteDAO;
import patitasarriba.dao.impl.cita.CitaDAOImpl;
import patitasarriba.dao.impl.usuario.ClienteDAOImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.dao.MascotaDAO; // esto está en la rama feature/daos
import patitasarriba.dao.impl.mascota.MascotaDAOImpl; // esto está en la rama feature/daos

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MascotaBLImpl implements MascotaBL {
    private final MascotaDAO mascotaDAO = new MascotaDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final CitaDAO citaDAO = new CitaDAOImpl();

    @Override
    public List<Mascota> findAll() throws BLException {
        try{
            return mascotaDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("Error al obtener todas las mascotas", e);
        }
    }

    @Override
    public Mascota findById(Integer id) throws BLException {
        try{
            return mascotaDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("Error al obtener la mascota con id: " + id, e);
        }
    }

    @Override
    public void insert(Mascota mascota) throws BLException {
        validar(mascota);
        validarClienteActivo(mascota);
        try{
            mascotaDAO.insert(mascota);
        } catch (SQLException e) {
            throw new BLException("Error al insertar la mascota", e);
        }
    }

    @Override
    public void update(Mascota mascota) throws BLException {
        validar(mascota);
        validarClienteActivo(mascota);
        try{
            mascotaDAO.update(mascota);
        } catch (SQLException e) {
            throw new BLException("Error al actualizar la mascota", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        if (id == null) throw new BLException("El id de la mascota no puede ser nulo");
        validarSinCitas(id);
        try{
            mascotaDAO.delete(id);
        } catch (SQLException e) {
            throw new BLException("Error al eliminar la mascota con id: " + id, e);
        }
    }

    // Si el cliente esta dado de baja, MascotaDAO no podria leer la mascota despues
    // (buscar_cliente_por_id solo devuelve clientes activos)
    private void validarClienteActivo(Mascota mascota) throws BLException {
        try {
            if (clienteDAO.findById(mascota.getCliente().getId()) == null) {
                throw new BLException("El cliente no existe o está dado de baja");
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el cliente de la mascota", e);
        }
    }

    // Si la mascota tiene citas y se da de baja, CitaDAO ya no podria leer NINGUNA cita
// (buscar_mascota_por_id solo devuelve mascotas activas)
    private void validarSinCitas(Integer idMascota) throws BLException {
        try {
            for (Cita cita : citaDAO.findAll()) {
                if (cita.getMascota().getId() == idMascota) {
                    throw new BLException("No se puede eliminar la mascota: tiene la cita " + cita.getId()
                            + " registrada (" + cita.getEstado() + ")");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las citas de la mascota", e);
        }
    }

    private void validar(Mascota mascota) throws BLException {
        if (mascota == null) {
            throw new BLException("La mascota no puede ser nula");
        }
        if (mascota.getNombre() == null || mascota.getNombre().isBlank()) {
            throw new BLException("El nombre de la mascota es obligatorio");
        }
        if (mascota.getSexo() == null || mascota.getTipoMascota() == null) {
            throw new BLException("La mascota debe tener sexo y tipo");
        }
        if (mascota.getCliente() == null) {
            throw new BLException("La mascota debe tener un cliente asociado");
        }
        if (mascota.getPeso() <= 0) {
            throw new BLException("El peso de la mascota debe ser mayor a 0");
        }
        if (mascota.getFechaNacimiento() != null
                && mascota.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new BLException("La fecha de nacimiento no puede estar en el futuro");
        }
    }
}
