package patitasarriba.bl.impl.mascota;

import patitasarriba.bl.BLException;
import patitasarriba.bl.MascotaBL;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.dao.MascotaDAO; // esto está en la rama feature/daos
import patitasarriba.dao.impl.mascota.MascotaDAOImpl; // esto está en la rama feature/daos

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class MascotaBLImpl implements MascotaBL {
    private final MascotaDAO mascotaDAO = new MascotaDAOImpl();

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
        try{
            mascotaDAO.insert(mascota);
        } catch (SQLException e) {
            throw new BLException("Error al insertar la mascota", e);
        }
    }

    @Override
    public void update(Mascota mascota) throws BLException {
        validar(mascota);
        try{
            mascotaDAO.update(mascota);
        } catch (SQLException e) {
            throw new BLException("Error al actualizar la mascota", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        try{
            mascotaDAO.delete(id);
        } catch (SQLException e) {
            throw new BLException("Error al eliminar la mascota con id: " + id, e);
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
