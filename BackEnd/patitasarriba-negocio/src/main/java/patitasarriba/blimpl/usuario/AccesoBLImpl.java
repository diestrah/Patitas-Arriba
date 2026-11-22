package patitasarriba.blimpl.usuario;

import patitasarriba.bl.usuario.AccesoBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.ValidacionException;
import patitasarriba.blimpl.correo.CorreoService;
import patitasarriba.blimpl.correo.Correos;
import patitasarriba.blimpl.util.HashUtil;
import patitasarriba.blimpl.util.TokenUtil;
import patitasarriba.dao.usuario.AdministradorDAO;
import patitasarriba.dao.usuario.ClienteDAO;
import patitasarriba.dao.usuario.CuentaDAO;
import patitasarriba.dao.usuario.RegistroPendienteDAO;
import patitasarriba.dao.usuario.TokenRecuperacionDAO;
import patitasarriba.dao.usuario.VeterinarioDAO;
import patitasarriba.daoimpl.usuario.AdministradorDAOImpl;
import patitasarriba.daoimpl.usuario.ClienteDAOImpl;
import patitasarriba.daoimpl.usuario.CuentaDAOImpl;
import patitasarriba.daoimpl.usuario.RegistroPendienteDAOImpl;
import patitasarriba.daoimpl.usuario.TokenRecuperacionDAOImpl;
import patitasarriba.daoimpl.usuario.VeterinarioDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.usuario.Administrador;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.modelo.usuario.Persona;
import patitasarriba.modelo.usuario.RegistroPendiente;
import patitasarriba.modelo.usuario.TokenRecuperacion;
import patitasarriba.modelo.usuario.Veterinario;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AccesoBLImpl implements AccesoBL {
    private static final int VERIFICACION_HORAS = 24;
    private static final int RECUPERACION_MINUTOS = 30;
    private static final int ACTIVACION_HORAS = 48;
    private static final int PASSWORD_MINIMA = 8;
    private static final String ENLACE_INVALIDO = "El enlace no es válido o ya venció";

    private final CuentaDAO cuentaDAO = new CuentaDAOImpl();
    private final ClienteDAO clienteDAO = new ClienteDAOImpl();
    private final VeterinarioDAO veterinarioDAO = new VeterinarioDAOImpl();
    private final AdministradorDAO administradorDAO = new AdministradorDAOImpl();
    private final RegistroPendienteDAO registroPendienteDAO = new RegistroPendienteDAOImpl();
    private final TokenRecuperacionDAO tokenDAO = new TokenRecuperacionDAOImpl();

    // Se reutilizan las validaciones de cada BL: son las mismas reglas que al registrar desde su pantalla
    private final ClienteBLImpl clienteBL = new ClienteBLImpl();
    private final VeterinarioBLImpl veterinarioBL = new VeterinarioBLImpl();
    private final AdministradorBLImpl administradorBL = new AdministradorBLImpl();
    private final CuentaBLImpl cuentaBL = new CuentaBLImpl();

    private final CorreoService correo;
    private final String urlFront;

    // Correo y URL segun las variables de entorno (ver Correos y PATITAS_URL_FRONT)
    public AccesoBLImpl() {
        this(Correos.desdeEntorno(), urlDesdeEntorno());
    }

    public AccesoBLImpl(CorreoService correo, String urlFront) {
        this.correo = correo;
        this.urlFront = urlFront.trim().replaceAll("/+$", "");
    }

    // ---------------------------------------------------------------- auto-registro de clientes

    @Override
    public void solicitarRegistro(Cliente datos, String correoUsuario, String password) throws BLException {
        validarDatosCliente(datos);
        validarPassword(password);
        String correoLimpio = limpiarCorreo(correoUsuario);
        Cuenta cuenta = cuentaNueva(correoLimpio, password);
        clienteBL.validarUnicos(datos);

        LocalDateTime ahora = LocalDateTime.now();
        String token = TokenUtil.generar();
        RegistroPendiente registro = new RegistroPendiente();
        registro.setDni(datos.getDni());
        registro.setNombres(datos.getNombres().trim());
        registro.setApellidoPaterno(datos.getApellidoPaterno().trim());
        registro.setApellidoMaterno(datos.getApellidoMaterno().trim());
        registro.setTelefono(datos.getTelefono());
        registro.setCorreo(correoLimpio);
        registro.setNombreUsuario(cuenta.getNombreUsuario());
        registro.setPassword(cuenta.getPassword());
        registro.setTokenHash(HashUtil.generarHashSHA256(token));
        registro.setFechaCreacion(ahora);
        registro.setFechaVencimiento(ahora.plusHours(VERIFICACION_HORAS));

        try {
            // Un nuevo intento con el mismo correo reemplaza al anterior; de paso se limpian los vencidos
            registroPendienteDAO.deleteVencidos(ahora);
            registroPendienteDAO.deleteByCorreo(correoLimpio);
            registroPendienteDAO.insert(registro);
        } catch (SQLException e) {
            throw new BLException("No se pudo registrar la solicitud", e);
        }

        try {
            correo.enviar(correoLimpio, "Confirma tu correo - Patitas Arriba",
                    "Hola " + registro.getNombres() + ",\n\n"
                            + "Gracias por registrarte en Patitas Arriba. Para activar tu cuenta confirma tu correo "
                            + "en este enlace (vale " + VERIFICACION_HORAS + " horas):\n\n"
                            + urlFront + "/verificar-correo?token=" + token + "\n\n"
                            + "Si no fuiste tú, ignora este mensaje.");
        } catch (BLException e) {
            borrarPendiente(registro.getId());
            throw e;
        }
    }

    @Override
    public Cliente confirmarRegistro(String token) throws BLException {
        RegistroPendiente registro;
        try {
            if (token == null || token.isBlank()) throw new ValidacionException(ENLACE_INVALIDO);
            registro = registroPendienteDAO.findByTokenHash(HashUtil.generarHashSHA256(token.trim()));
            if (registro == null) throw new ValidacionException(ENLACE_INVALIDO);
            if (registro.getFechaVencimiento().isBefore(LocalDateTime.now())) {
                registroPendienteDAO.delete(registro.getId());
                throw new ValidacionException(ENLACE_INVALIDO);
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el enlace", e);
        }

        Cliente cliente = new Cliente();
        Cuenta cuenta = new Cuenta();
        try {
            cliente.setDni(registro.getDni());
            cliente.setNombres(registro.getNombres());
            cliente.setApellidoPaterno(registro.getApellidoPaterno());
            cliente.setApellidoMaterno(registro.getApellidoMaterno());
            cliente.setTelefono(registro.getTelefono());
            cuenta.setCorreo(registro.getCorreo());
            cuenta.setPassword(registro.getPassword());
            cuenta.setFechaCreacion(LocalDate.now());
            cuenta.setActivo(true);
            // Si mientras tanto alguien tomo ese usuario, se busca otro libre
            String usuario = registro.getNombreUsuario();
            if (cuentaDAO.findByNombreUsuario(usuario) != null) {
                usuario = nombreUsuarioDisponible(registro.getCorreo());
            }
            cuenta.setNombreUsuario(usuario);
        } catch (IllegalArgumentException e) {
            throw new ValidacionException("Los datos del registro no son válidos");
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el nombre de usuario", e);
        }

        try {
            // Se vuelve a comprobar: el DNI, el telefono o el correo pudieron ocuparse despues de la solicitud
            clienteBL.validarUnicos(cliente);
            cuentaBL.validarUnicos(cuenta);
        } catch (ValidacionException e) {
            borrarPendiente(registro.getId());
            throw e;
        }

        TransactionsManager.iniciar();
        try {
            cuentaDAO.insert(cuenta);
            cliente.setCuenta(cuenta);
            cliente.setActivo(true);
            clienteDAO.insert(cliente);
            registroPendienteDAO.delete(registro.getId());
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo completar el registro", e);
        }
        return cliente;
    }

    // ---------------------------------------------------------------- recuperar contrasena

    @Override
    public void solicitarRecuperacion(String correoUsuario) throws BLException {
        if (correoUsuario == null || correoUsuario.isBlank()) {
            throw new ValidacionException("Debe ingresar el correo");
        }
        Cuenta cuenta;
        Persona persona;
        try {
            cuenta = cuentaDAO.findByCorreo(correoUsuario);
            // Si no hay una cuenta activa de una persona activa no se hace nada ni se avisa: asi no se
            // puede averiguar que correos estan registrados
            if (cuenta == null || !cuenta.isActivo()) return;
            persona = personaDeLaCuenta(cuenta);
            if (persona == null || !persona.isActivo()) return;
        } catch (SQLException e) {
            throw new BLException("No se pudo procesar la solicitud", e);
        }

        String token = emitirToken(cuenta, LocalDateTime.now().plusMinutes(RECUPERACION_MINUTOS));
        try {
            correo.enviar(cuenta.getCorreo(), "Restablece tu contraseña - Patitas Arriba",
                    "Hola " + persona.getNombres() + ",\n\n"
                            + "Recibimos una solicitud para restablecer tu contraseña. Elige una nueva en este enlace "
                            + "(vale " + RECUPERACION_MINUTOS + " minutos y solo se puede usar una vez):\n\n"
                            + urlFront + "/restablecer-contrasena?token=" + token + "\n\n"
                            + "Si no fuiste tú, ignora este mensaje: tu contraseña no cambia.");
        } catch (BLException e) {
            // No se propaga: la respuesta debe ser igual exista o no el correo
            System.err.println("No se pudo enviar el correo de recuperación: " + e.getMessage());
        }
    }

    @Override
    public void restablecerContrasena(String token, String nuevaPassword) throws BLException {
        validarPassword(nuevaPassword);
        Cuenta cuenta;
        try {
            if (token == null || token.isBlank()) throw new ValidacionException(ENLACE_INVALIDO);
            TokenRecuperacion guardado = tokenDAO.findByTokenHash(HashUtil.generarHashSHA256(token.trim()));
            if (guardado == null || !guardado.estaVigente(LocalDateTime.now())) {
                throw new ValidacionException(ENLACE_INVALIDO);
            }
            cuenta = cuentaDAO.findById(guardado.getIdCuenta());
            if (cuenta == null) throw new ValidacionException(ENLACE_INVALIDO);
        } catch (SQLException e) {
            throw new BLException("No se pudo verificar el enlace", e);
        }

        cuenta.setPassword(HashUtil.generarHashSHA256(nuevaPassword));
        TransactionsManager.iniciar();
        try {
            cuentaDAO.update(cuenta);
            // Con la contrasena nueva el enlace (y cualquier otro pendiente de la cuenta) deja de servir
            tokenDAO.invalidarDeCuenta(cuenta.getId());
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo cambiar la contraseña", e);
        }
    }

    // ---------------------------------------------------------------- alta hecha por un administrador

    @Override
    public void registrarCliente(Cliente cliente, String correoUsuario) throws BLException {
        validarDatosCliente(cliente);
        clienteBL.validarUnicos(cliente);
        Cuenta cuenta = cuentaConClaveAleatoria(correoUsuario);
        crearPersonaConCuenta(cliente, cuenta);
        enviarActivacion(cuenta, cliente);
    }

    @Override
    public void registrarVeterinario(Veterinario veterinario, String correoUsuario) throws BLException {
        veterinarioBL.validar(veterinario);
        validarContacto(veterinario);
        veterinarioBL.validarUnicos(veterinario);
        Cuenta cuenta = cuentaConClaveAleatoria(correoUsuario);
        crearPersonaConCuenta(veterinario, cuenta);
        enviarActivacion(cuenta, veterinario);
    }

    @Override
    public void registrarAdministrador(Administrador administrador, String correoUsuario) throws BLException {
        administradorBL.validar(administrador);
        validarContacto(administrador);
        administradorBL.validarUnicos(administrador);
        Cuenta cuenta = cuentaConClaveAleatoria(correoUsuario);
        crearPersonaConCuenta(administrador, cuenta);
        enviarActivacion(cuenta, administrador);
    }

    // La persona y su cuenta se guardan juntas: si una falla, no se guarda ninguna (RN-GE-03)
    private void crearPersonaConCuenta(Persona persona, Cuenta cuenta) throws BLException {
        TransactionsManager.iniciar();
        try {
            cuentaDAO.insert(cuenta);
            persona.setCuenta(cuenta);
            persona.setActivo(true);
            if (persona instanceof Cliente cliente) {
                clienteDAO.insert(cliente);
            } else if (persona instanceof Veterinario veterinario) {
                veterinarioDAO.insert(veterinario);
            } else if (persona instanceof Administrador administrador) {
                administradorDAO.insert(administrador);
            } else {
                throw new IllegalArgumentException("Tipo de persona no soportado");
            }
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo registrar a la persona con su cuenta", e);
        }
    }

    // La cuenta nace con una clave aleatoria que nadie conoce; la persona elige la suya con el enlace del correo
    private void enviarActivacion(Cuenta cuenta, Persona persona) throws BLException {
        String token = emitirToken(cuenta, LocalDateTime.now().plusHours(ACTIVACION_HORAS));
        try {
            correo.enviar(cuenta.getCorreo(), "Activa tu cuenta - Patitas Arriba",
                    "Hola " + persona.getNombres() + ",\n\n"
                            + "Se creó tu cuenta en Patitas Arriba. Elige tu contraseña en este enlace "
                            + "(vale " + ACTIVACION_HORAS + " horas y solo se puede usar una vez):\n\n"
                            + urlFront + "/restablecer-contrasena?token=" + token);
        } catch (BLException e) {
            throw new BLException("La cuenta se creó, pero no se pudo enviar el correo de activación. "
                    + "La persona puede usar «Olvidé mi contraseña»", e);
        }
    }

    // ---------------------------------------------------------------- apoyo

    private void validarDatosCliente(Cliente cliente) throws BLException {
        clienteBL.validar(cliente);
        validarContacto(cliente);
    }

    // Lo que la base exige y los BL de cada persona todavia no revisan
    private void validarContacto(Persona persona) throws BLException {
        if (persona.getApellidoMaterno() == null || persona.getApellidoMaterno().trim().isEmpty()) {
            throw new ValidacionException("El apellido materno es obligatorio");
        }
        if (!esNombre(persona.getNombres())) {
            throw new ValidacionException("Los nombres solo pueden tener letras y espacios");
        }
        if (!esNombre(persona.getApellidoPaterno()) || !esNombre(persona.getApellidoMaterno())) {
            throw new ValidacionException("Los apellidos solo pueden tener letras y espacios");
        }
        if (persona.getTelefono() == null || !persona.getTelefono().matches("\\d{9}")) {
            throw new ValidacionException("El teléfono debe contener exactamente 9 dígitos numéricos");
        }
    }

    // Letras (con tildes y ñ) separadas por espacios, apostrofes o guiones: "Juan Carlos", "O'Connor", "Pérez-Gómez"
    private boolean esNombre(String texto) {
        return texto != null && texto.trim().matches("^\\p{L}+(?:[ '\\-]\\p{L}+)*$");
    }

    private void validarPassword(String password) throws BLException {
        if (password == null || password.length() < PASSWORD_MINIMA) {
            throw new ValidacionException("La contraseña debe tener al menos " + PASSWORD_MINIMA + " caracteres");
        }
    }

    private String limpiarCorreo(String correoUsuario) throws BLException {
        String limpio = correoUsuario == null ? "" : correoUsuario.trim();
        if (!limpio.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ValidacionException("Debe ingresar un correo electrónico válido");
        }
        return limpio;
    }

    // Cuenta lista para insertar: el usuario se arma con el correo (ya no se usa para ingresar, pero la base lo exige unico)
    private Cuenta cuentaNueva(String correoLimpio, String passwordPlana) throws BLException {
        Cuenta cuenta = new Cuenta();
        cuenta.setCorreo(correoLimpio);
        cuenta.setNombreUsuario(nombreUsuarioDisponible(correoLimpio));
        cuenta.setPassword(HashUtil.generarHashSHA256(passwordPlana));
        cuenta.setFechaCreacion(LocalDate.now());
        cuenta.setActivo(true);
        cuentaBL.validarUnicos(cuenta);
        return cuenta;
    }

    private Cuenta cuentaConClaveAleatoria(String correoUsuario) throws BLException {
        return cuentaNueva(limpiarCorreo(correoUsuario), TokenUtil.generar());
    }

    // Parte del correo antes de la @; si ya existe (tambien dada de baja) se agrega un numero
    private String nombreUsuarioDisponible(String correoLimpio) throws BLException {
        String base = correoLimpio.substring(0, correoLimpio.indexOf('@')).toLowerCase().replaceAll("[^a-z0-9._-]", "");
        if (base.isEmpty()) base = "usuario";
        if (base.length() > 40) base = base.substring(0, 40);
        try {
            String candidato = base;
            for (int i = 2; i <= 99; i++) {
                if (cuentaDAO.findByNombreUsuario(candidato) == null) return candidato;
                candidato = base + i;
            }
        } catch (SQLException e) {
            throw new BLException("No se pudo generar el nombre de usuario", e);
        }
        throw new ValidacionException("No se pudo generar un nombre de usuario disponible");
    }

    private Persona personaDeLaCuenta(Cuenta cuenta) throws SQLException {
        Persona persona = clienteDAO.findByCuenta(cuenta.getId());
        if (persona == null) persona = veterinarioDAO.findByCuenta(cuenta.getId());
        if (persona == null) persona = administradorDAO.findByCuenta(cuenta.getId());
        return persona;
    }

    // Crea el token (anulando los anteriores de la cuenta) y devuelve el valor en claro para el enlace
    private String emitirToken(Cuenta cuenta, LocalDateTime vencimiento) throws BLException {
        String token = TokenUtil.generar();
        TokenRecuperacion guardado = new TokenRecuperacion();
        guardado.setIdCuenta(cuenta.getId());
        guardado.setTokenHash(HashUtil.generarHashSHA256(token));
        guardado.setFechaCreacion(LocalDateTime.now());
        guardado.setFechaVencimiento(vencimiento);
        TransactionsManager.iniciar();
        try {
            tokenDAO.invalidarDeCuenta(cuenta.getId());
            tokenDAO.insert(guardado);
            TransactionsManager.commit();
        } catch (SQLException | RuntimeException e) {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            throw new BLException("No se pudo generar el enlace", e);
        }
        return token;
    }

    private void borrarPendiente(int id) {
        try {
            registroPendienteDAO.delete(id);
        } catch (SQLException ignorada) {
            // Si no se pudo borrar, vence solo y se limpia en la siguiente solicitud
        }
    }

    private static String urlDesdeEntorno() {
        String url = System.getenv("PATITAS_URL_FRONT");
        return url == null || url.isBlank() ? "http://localhost:5118" : url;
    }
}
