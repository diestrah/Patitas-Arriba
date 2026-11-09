package patitasarriba.bl.util;

import patitasarriba.bl.BLException;
import patitasarriba.dao.AdministradorDAO;
import patitasarriba.dao.ClienteDAO;
import patitasarriba.dao.HorarioDAO;
import patitasarriba.dao.VeterinarioDAO;
import patitasarriba.dao.impl.horario.HorarioDAOImpl;
import patitasarriba.dao.impl.usuario.AdministradorDAOImpl;
import patitasarriba.dao.impl.usuario.ClienteDAOImpl;
import patitasarriba.dao.impl.usuario.VeterinarioDAOImpl;
import patitasarriba.modelo.horario.Horario;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.usuario.Persona;
import patitasarriba.modelo.usuario.Personal;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;

// Validaciones comunes a clientes, veterinarios y administradores (RN-PE y RN-CU-08)
public final class ValidadorPersona {

    private ValidadorPersona() {
    }

    // RN-PE-02 a RN-PE-04: los mismos campos que la BD declara NOT NULL en las tres tablas
    public static void validarDatos(Persona persona, String tipo) throws BLException {
        if (persona == null) {
            throw new BLException("El " + tipo + " no puede ser nulo");
        }
        if (persona.getDni() == null || !persona.getDni().matches("\\d{8}")) {
            throw new BLException("El DNI debe contener exactamente 8 dígitos numéricos");
        }
        if (vacio(persona.getNombres())) {
            throw new BLException("El nombre del " + tipo + " es obligatorio");
        }
        if (vacio(persona.getApellidoPaterno())) {
            throw new BLException("El apellido paterno es obligatorio");
        }
        if (vacio(persona.getApellidoMaterno())) {
            throw new BLException("El apellido materno es obligatorio");
        }
        if (persona.getTelefono() == null || !persona.getTelefono().matches("\\d{9}")) {
            throw new BLException("El teléfono debe contener exactamente 9 dígitos numéricos");
        }
    }

    // RN-CU-08: una cuenta pertenece a una sola persona, sin importar su tipo.
    // La BD solo lo garantiza dentro de cada tabla (id_cuenta UNIQUE), por eso se revisan las tres.
    // Los finders por cuenta no filtran activo: una cuenta usada por alguien dado de baja tampoco se reutiliza.
    public static void validarCuentaLibre(Persona persona) throws BLException {
        int idCuenta = persona.getCuenta().getId();
        try {
            ClienteDAO clienteDAO = new ClienteDAOImpl();
            Cliente cliente = clienteDAO.findByCuenta(idCuenta);
            if (cliente != null && !(persona instanceof Cliente && cliente.getId() == persona.getId())) {
                throw new BLException("La cuenta ya pertenece al cliente con DNI " + cliente.getDni());
            }
            VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();
            Veterinario veterinario = veterinarioDAO.findByCuenta(idCuenta);
            if (veterinario != null && !(persona instanceof Veterinario && veterinario.getId() == persona.getId())) {
                throw new BLException("La cuenta ya pertenece al veterinario con DNI " + veterinario.getDni());
            }
            AdministradorDAO administradorDAO = new AdministradorDAOImpl();
            Administrador administrador = administradorDAO.findByCuenta(idCuenta);
            if (administrador != null && !(persona instanceof Administrador && administrador.getId() == persona.getId())) {
                throw new BLException("La cuenta ya pertenece al administrador con DNI " + administrador.getDni());
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar si la cuenta ya está asociada a otra persona", e);
        }
    }

    // RN-HO-05: los horarios asignados deben existir y estar activos
    public static void validarHorarios(Personal personal) throws BLException {
        try {
            HorarioDAO horarioDAO = new HorarioDAOImpl();
            for (Horario horario : personal.getHorarios()) {
                if (horarioDAO.findById(horario.getId()) == null) {
                    throw new BLException("El horario " + horario.getId() + " no existe o está dado de baja");
                }
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar los horarios asignados", e);
        }
    }

    private static boolean vacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }
}
