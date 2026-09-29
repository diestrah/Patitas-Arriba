package patitasarriba.bl.impl.usuario;

import patitasarriba.bl.BLException;
import patitasarriba.bl.VeterinarioBL;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;
import java.util.List;

public class VeterinarioBLImpl implements VeterinarioBL {
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();

    @Override
    public List<Veterinario> findAll() throws BLException {
        try {
            return veterinarioDAO.findAll();
        } catch (SQLException e) {
            throw new BLException("No se pudo listar los veterinarios", e);
        }
    }

    @Override
    public Veterinario findById(Integer id) throws BLException {
        try {
            return veterinarioDAO.findById(id);
        } catch (SQLException e) {
            throw new BLException("No se pudo recuperar el veterinario", e);
        }
    }

    @Override
    public Veterinario findByDni(String dni) throws BLException {
        try {
            return veterinarioDAO.findByDni(dni);
        } catch (SQLException e) {
            throw new BLException("Error al buscar el veterinario por DNI", e);
        }
    }

    @Override
    public void insert(Veterinario vet) throws BLException {
        validar(vet);

        TransactionsManager.iniciar();
        try {
            veterinarioDAO.insert(vet);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo registrar el veterinario", e);
        }
    }

    @Override
    public void update(Veterinario vet) throws BLException {
        validar(vet);

        TransactionsManager.iniciar();
        try {
            veterinarioDAO.update(vet);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo actualizar el veterinario", e);
        }
    }

    @Override
    public void delete(Integer id) throws BLException {
        TransactionsManager.iniciar();
        try {
            veterinarioDAO.delete(id);
            TransactionsManager.commit();
        } catch (SQLException e) {
            TransactionsManager.rollback();
            throw new BLException("No se pudo eliminar el veterinario", e);
        }
    }

    private void validar(Veterinario vet) throws BLException {
        if (vet == null) {
            throw new BLException("El veterinario no puede ser nulo");
        }
        if (vet.getDni() == null || !vet.getDni().matches("\\d{8}")) {
            throw new BLException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        if (vet.getNombres() == null || vet.getNombres().trim().isEmpty()) {
            throw new BLException("El nombre del veterinario es obligatorio");
        }
        if (vet.getNumeroColegiatura() == null || vet.getNumeroColegiatura().trim().isEmpty()) {
            throw new BLException("El número de colegiatura es obligatorio");
        }
    }
}
