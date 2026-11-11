package patitasarriba.bl.impl.mascota;

import patitasarriba.bl.BLException;
import patitasarriba.bl.ValidacionException;
import patitasarriba.bl.MascotaBL;
import patitasarriba.bl.NoEncontradoException;
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

    // 200 KB de imagen en base64 son unos 270 000 caracteres
    private static final int MAX_CARACTERES_FOTO = 280_000;

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
            Mascota mascota= mascotaDAO.findById(id);
            if(mascota ==null){
                throw new NoEncontradoException("No se pudo encontrar la mascota con id :"+id);
            }
            return mascota;
        } catch (SQLException e) {
            throw new BLException("Error al obtener la mascota con id: " + id, e);
        }
    }

    @Override
    public List<Mascota> findByCliente(Integer idCliente) throws BLException {
        if (idCliente == null) throw new ValidacionException("El id del cliente no puede ser nulo");
        try {
            return mascotaDAO.listarPorCliente(idCliente);
        } catch (SQLException e) {
            throw new BLException("No se pudo listar las mascotas del cliente", e);
        }
    }

    @Override
    public void insert(Mascota mascota) throws BLException {
        validar(mascota);
        validarClienteActivo(mascota);
        // activo es el borrado logico: todo registro nuevo se crea activo,
        // aunque el cliente mande "activo": false (para desactivar esta el delete)
        mascota.setActivo(true);
        try{
            mascotaDAO.insert(mascota);
        } catch (SQLException e) {
            throw new BLException("Error al insertar la mascota", e);
        }
    }

    @Override
    public void update(Mascota mascota) throws BLException {
        validar(mascota);
        validarExiste(mascota.getId());
        validarClienteActivo(mascota);
        // activo es el borrado logico: modificar nunca da de baja, aunque el cliente mande
        // "activo": false (para dar de baja esta el delete)
        mascota.setActivo(true);
        try{
            mascotaDAO.update(mascota);
        } catch (SQLException e) {
            throw new BLException("Error al actualizar la mascota", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        if (id == null) throw new ValidacionException("El id de la mascota no puede ser nulo");
        validarExiste(id);
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
                throw new ValidacionException("El cliente no existe o está dado de baja");
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
                    throw new ValidacionException("No se puede eliminar la mascota: tiene la cita " + cita.getId()
                            + " registrada (" + cita.getEstado() + ")");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar las citas de la mascota", e);
        }
    }

    private void validar(Mascota mascota) throws BLException {
        if (mascota == null) {
            throw new ValidacionException("La mascota no puede ser nula");
        }
        if (mascota.getNombre() == null || mascota.getNombre().isBlank()) {
            throw new ValidacionException("El nombre de la mascota es obligatorio");
        }
        if (mascota.getSexo() == null || mascota.getTipoMascota() == null) {
            throw new ValidacionException("La mascota debe tener sexo y tipo");
        }
        if (mascota.getCliente() == null) {
            throw new ValidacionException("La mascota debe tener un cliente asociado");
        }
        // El peso es opcional (lo puede omitir el cliente al registrar): 0 significa que no se registró (RN-MA-02)
        if (mascota.getPeso() < 0) {
            throw new ValidacionException("El peso de la mascota no puede ser negativo");
        }
        validarFoto(mascota.getFoto());
        if (mascota.getFechaNacimiento() != null
                && mascota.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new ValidacionException("La fecha de nacimiento no puede estar en el futuro");
        }
    }

    // RN-MA-07: la foto es opcional; si viene, es una imagen JPG, PNG o WEBP en base64 de menos de 200 KB
    private void validarFoto(String foto) throws BLException {
        if (foto == null) {
            return;
        }
        boolean formatoValido = foto.startsWith("data:image/jpeg;base64,")
                || foto.startsWith("data:image/png;base64,")
                || foto.startsWith("data:image/webp;base64,");
        if (!formatoValido) {
            throw new ValidacionException("La foto debe ser una imagen JPG, PNG o WEBP");
        }
        if (foto.length() > MAX_CARACTERES_FOTO) {
            throw new ValidacionException("La foto es muy grande: debe pesar menos de 200 KB");
        }
    }

    private void validarExiste(int id) throws BLException {
        try {
            if (mascotaDAO.findById(id) == null) {
                throw new NoEncontradoException("No existe una mascota con id " + id);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar la existencia de la mascota", e);
        }
    }
}
