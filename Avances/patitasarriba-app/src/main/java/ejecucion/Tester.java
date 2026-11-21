package ejecucion;

import patitasarriba.bl.atencion.AtencionBL;
import patitasarriba.bl.atencion.DiagnosticoBL;
import patitasarriba.bl.boleta.BoletaBL;
import patitasarriba.bl.cita.CitaBL;
import patitasarriba.bl.horario.HorarioBL;
import patitasarriba.bl.excepciones.BLException;
import patitasarriba.bl.excepciones.NoEncontradoException;
import patitasarriba.blimpl.atencion.AtencionBLImpl;
import patitasarriba.blimpl.atencion.DiagnosticoBLImpl;
import patitasarriba.blimpl.boleta.BoletaBLImpl;
import patitasarriba.blimpl.cita.CitaBLImpl;
import patitasarriba.blimpl.horario.HorarioBLImpl;
import patitasarriba.blimpl.mascota.MascotaBLImpl;
import patitasarriba.blimpl.producto.ArticuloBLImpl;
import patitasarriba.blimpl.producto.CategoriaBLImpl;
import patitasarriba.blimpl.producto.ServicioBLImpl;
import patitasarriba.blimpl.receta.RecetaBLImpl;
import patitasarriba.blimpl.correo.CorreoService;
import patitasarriba.blimpl.usuario.AccesoBLImpl;
import patitasarriba.blimpl.usuario.AdministradorBLImpl;
import patitasarriba.blimpl.usuario.ClienteBLImpl;
import patitasarriba.blimpl.usuario.CuentaBLImpl;
import patitasarriba.blimpl.usuario.VeterinarioBLImpl;
import patitasarriba.bl.mascota.MascotaBL;
import patitasarriba.bl.producto.ArticuloBL;
import patitasarriba.bl.producto.CategoriaBL;
import patitasarriba.bl.producto.ServicioBL;
import patitasarriba.bl.receta.RecetaBL;
import patitasarriba.bl.usuario.*;
import patitasarriba.modelo.atencion.*;
import patitasarriba.modelo.boleta.*;
import patitasarriba.modelo.cita.*;
import patitasarriba.modelo.horario.*;
import patitasarriba.modelo.mascota.*;
import patitasarriba.modelo.producto.*;
import patitasarriba.modelo.receta.*;
import patitasarriba.modelo.usuario.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Prueba de la capa de negocio (BL) contra la base real.
// 1) CRUD de catalogo, cliente y mascota (incluye login y baja de la cuenta con la persona).
// 2) Flujo clinico: cita programada -> cita de emergencia -> atencion -> receta -> cita completada
//    -> historial -> boleta -> bajas.
// Cada seccion cierra con sus reglas de negocio, que la BL debe rechazar. Los datos llevan un sufijo
// distinto en cada ejecucion. Los datos del CRUD se dan de baja al terminar; los del flujo clinico
// (cliente, veterinario, cita, atenciones, boleta y catalogo usado) quedan en la base porque son
// historial y las reglas no permiten darlos de baja.
public class Tester {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String SUFIJO = String.valueOf(System.currentTimeMillis() % 10_000_000);

    // Resultado de las reglas de la seccion en curso; se imprimen y se vacian al cerrar la seccion
    private static final List<String> reglas = new ArrayList<>();
    private static int fallas = 0;

    private static final CuentaBL cuentaBL = new CuentaBLImpl();
    private static final ClienteBL clienteBL = new ClienteBLImpl();
    private static final VeterinarioBL veterinarioBL = new VeterinarioBLImpl();
    private static final AdministradorBL administradorBL = new AdministradorBLImpl();
    private static final HorarioBL horarioBL = new HorarioBLImpl();
    private static final CategoriaBL categoriaBL = new CategoriaBLImpl();
    private static final ArticuloBL articuloBL = new ArticuloBLImpl();
    private static final ServicioBL servicioBL = new ServicioBLImpl();
    private static final DiagnosticoBL diagnosticoBL = new DiagnosticoBLImpl();
    private static final MascotaBL mascotaBL = new MascotaBLImpl();
    private static final CitaBL citaBL = new CitaBLImpl();
    private static final AtencionBL atencionBL = new AtencionBLImpl();
    private static final RecetaBL recetaBL = new RecetaBLImpl();
    private static final BoletaBL boletaBL = new BoletaBLImpl();

    // Los correos no se envian: se guardan en memoria para leer el enlace y probar el flujo completo
    private static final CorreoEnMemoria correos = new CorreoEnMemoria();
    private static final AccesoBL accesoBL = new AccesoBLImpl(correos, "http://localhost:5118");

    // Una busqueda que puede lanzar NoEncontradoException (findById) o devolver null
    private interface Busqueda {
        Object buscar() throws BLException;
    }

    // Un caso de regla de negocio: la BL debe rechazarlo lanzando BLException
    private interface Caso {
        void ejecutar() throws BLException;
    }

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println(" TESTER - PATITAS ARRIBA");
        System.out.println("============================================================");
        try {
            probarCategoria();
            probarArticulo();
            probarServicio();
            probarClienteYMascota();
            probarAcceso();
            probarFlujoClinico();
        } catch (BLException | RuntimeException ex) {
            fallas++;
            System.out.println();
            System.out.println("La prueba se detuvo por un error: " + causa(ex));
        }
        System.out.println();
        System.out.println(fallas == 0 ? "Sin fallas." : "FALLAS: " + fallas);
    }

    // ---------------------------------------------------------------- CATEGORIA (RF08)

    private static void probarCategoria() throws BLException {
        tituloSeccion("CATEGORIA");

        Categoria categoria = nuevaCategoria("Vacunas");
        bloque("INSERT");
        campo("id", categoria.getId());
        campo("nombre", categoria.getNombre());

        bloque("FIND");
        campo("nombre", categoriaBL.findById(categoria.getId()).getNombre());

        String descripcionAnterior = categoria.getDescripcion();
        categoria.setDescripcion("Vacunas y biologicos");
        categoriaBL.update(categoria);
        bloque("UPDATE");
        campo("descripcion", descripcionAnterior + " -> " + categoriaBL.findById(categoria.getId()).getDescripcion());

        listarCategorias(categoriaBL.findAll());

        categoriaBL.delete(categoria.getId());
        bloque("DELETE");
        campo("id eliminado", categoria.getId());
        bloque("VERIFICACION DELETE");
        verificarEliminado(categoria.getId(), () -> categoriaBL.findById(categoria.getId()));
    }

    // ---------------------------------------------------------------- ARTICULO (RF08)

    private static void probarArticulo() throws BLException {
        tituloSeccion("ARTICULO");

        Categoria categoria = nuevaCategoria("Apoyo");
        Articulo articulo = nuevoArticulo("Alimento premium", 45.90, 20, categoria);
        articuloBL.insert(articulo);
        bloque("INSERT");
        campo("id", articulo.getId());
        campo("nombre", articulo.getNombre());
        campo("categoria", categoria.getNombre());

        bloque("FIND");
        campo("nombre", articuloBL.findById(articulo.getId()).getNombre());

        // El administrador actualiza el stock modificando el articulo
        double precioAnterior = articulo.getPrecioBase();
        int stockAnterior = articulo.getStockActual();
        articulo.setPrecioBase(49.90);
        articulo.setStockActual(18);
        articuloBL.update(articulo);
        Articulo actualizado = articuloBL.findById(articulo.getId());
        bloque("UPDATE");
        campo("precio", String.format("S/ %.2f -> S/ %.2f", precioAnterior, actualizado.getPrecioBase()));
        campo("stock", stockAnterior + " -> " + actualizado.getStockActual());
        verificar("stock actualizado a 18", actualizado.getStockActual() == 18);

        listarArticulos(articuloBL.findAll());

        regla("Eliminar la categoria 'Apoyo' (tiene articulos)", () -> categoriaBL.delete(categoria.getId()));
        // El stock negativo ya lo impide el modelo (Articulo.setStockActual); el precio 0 lo rechaza la BL
        regla("Articulo con precio 0", () -> articuloBL.insert(nuevoArticulo("Gratis", 0, 5, categoria)));
        mostrarReglas();

        articuloBL.delete(articulo.getId());
        categoriaBL.delete(categoria.getId());
        bloque("DELETE");
        campo("id eliminado", articulo.getId());
        bloque("VERIFICACION DELETE");
        verificarEliminado(articulo.getId(), () -> articuloBL.findById(articulo.getId()));
    }

    // ---------------------------------------------------------------- SERVICIO (RF07)

    private static void probarServicio() throws BLException {
        tituloSeccion("SERVICIO");

        Servicio servicio = nuevoServicio("Consulta general", 80, TipoServicio.CONSULTA_MEDICA);
        servicioBL.insert(servicio);
        bloque("INSERT");
        campo("id", servicio.getId());
        campo("nombre", servicio.getNombre());
        campo("tipo", servicio.getTipo());

        bloque("FIND");
        campo("nombre", servicioBL.findById(servicio.getId()).getNombre());

        double precioAnterior = servicio.getPrecioBase();
        servicio.setPrecioBase(90);
        servicioBL.update(servicio);
        bloque("UPDATE");
        campo("precio", String.format("S/ %.2f -> S/ %.2f", precioAnterior, servicioBL.findById(servicio.getId()).getPrecioBase()));

        listarServicios(servicioBL.findAll());

        regla("Servicio con el mismo nombre", () -> {
            Servicio repetido = nuevoServicio("Consulta general", 50, TipoServicio.CONSULTA_MEDICA);
            servicioBL.insert(repetido);
        });
        mostrarReglas();

        servicioBL.delete(servicio.getId());
        bloque("DELETE");
        campo("id eliminado", servicio.getId());
        bloque("VERIFICACION DELETE");
        verificarEliminado(servicio.getId(), () -> servicioBL.findById(servicio.getId()));
    }

    // ---------------------------------------------------------------- CLIENTE, MASCOTA Y LOGIN (RF01, RF02, RF03)

    private static void probarClienteYMascota() throws BLException {
        tituloSeccion("CLIENTE, MASCOTA Y LOGIN");

        Cuenta cuenta = nuevaCuenta("cli");
        cuentaBL.insert(cuenta);
        Cliente cliente = new Cliente();
        completarPersona(cliente, cuenta, "1");
        clienteBL.insert(cliente);
        Mascota mascota = nuevaMascota(cliente, "Firulais", 12.5);
        mascotaBL.insert(mascota);
        bloque("INSERT");
        campo("cliente", cliente.getId() + " | DNI " + cliente.getDni());
        campo("mascota", mascota.getId() + " | " + mascota.getNombre() + " | " + mascota.getTipoMascota());

        bloque("FIND");
        campo("cliente por DNI", clienteBL.findByDni(cliente.getDni()).getNombres());
        List<Mascota> suyas = mascotaBL.findByCliente(cliente.getId());
        campo("sus mascotas", suyas.size());
        verificar("el cliente ve su mascota", suyas.size() == 1 && suyas.get(0).getId() == mascota.getId());

        bloque("LOGIN");
        Persona persona = cuentaBL.login(cuenta.getCorreo(), clave());
        campo("correo", cuenta.getCorreo());
        campo("rol", persona.getClass().getSimpleName() + " (id " + persona.getId() + ")");
        verificar("el login devuelve al cliente", persona instanceof Cliente && persona.getId() == cliente.getId());

        String telefonoAnterior = cliente.getTelefono();
        cliente.setTelefono("98" + numero());
        clienteBL.update(cliente);
        double pesoAnterior = mascota.getPeso();
        mascota = mascotaBL.findById(mascota.getId());
        mascota.setPeso(13.2);
        mascotaBL.update(mascota);
        bloque("UPDATE");
        campo("telefono", telefonoAnterior + " -> " + clienteBL.findById(cliente.getId()).getTelefono());
        campo("peso", pesoAnterior + " kg -> " + mascotaBL.findById(mascota.getId()).getPeso() + " kg");

        // Foto opcional (portal del cliente): se guarda como texto base64 y se lee igual
        String foto = "data:image/jpeg;base64,/9j/4AAQSkZJRg==";
        mascota.setFoto(foto);
        mascotaBL.update(mascota);
        campo("foto", mascotaBL.findById(mascota.getId()).getFoto() == null ? "sin foto" : "guardada");
        verificar("la foto se guarda y se lee igual", foto.equals(mascotaBL.findById(mascota.getId()).getFoto()));
        // El peso es opcional: 0 significa que no se registró (RN-MA-02)
        Mascota sinPeso = nuevaMascota(cliente, "Sinpeso", 0);
        mascotaBL.insert(sinPeso);
        verificar("una mascota sin peso (0) se acepta", mascotaBL.findById(sinPeso.getId()).getPeso() == 0);
        mascotaBL.delete(sinPeso.getId());

        listarMascotas(mascotaBL.findAll());

        // Reglas de negocio: se usa otra cuenta para que solo falle el dato que se quiere probar
        Cuenta cuentaAparte = nuevaCuenta("dup");
        cuentaBL.insert(cuentaAparte);

        regla("Login con contraseña incorrecta", () -> cuentaBL.login(cuenta.getCorreo(), "otra"));
        regla("Login con el nombre de usuario en vez del correo", () -> cuentaBL.login(cuenta.getNombreUsuario(), clave()));
        regla("Cliente con DNI repetido '" + cliente.getDni() + "'", () -> {
            Cliente repetido = new Cliente();
            completarPersona(repetido, cuentaAparte, "1");
            clienteBL.insert(repetido);
        });
        regla("Cliente con DNI 'ABC12345'", () -> {
            Cliente invalido = new Cliente();
            completarPersona(invalido, cuentaAparte, "7");
            invalido.setDni("ABC12345");
            clienteBL.insert(invalido);
        });
        Cliente dueno = cliente;
        regla("Mascota con foto que no es una imagen", () -> {
            Mascota conFotoMala = nuevaMascota(dueno, "Fotomala", 5.0);
            conFotoMala.setFoto("no es una imagen");
            mascotaBL.insert(conFotoMala);
        });
        regla("Eliminar la cuenta de un cliente activo", () -> cuentaBL.delete(cuenta.getId()));
        mostrarReglas();

        int idMascota = mascota.getId();
        mascotaBL.delete(idMascota);
        clienteBL.delete(cliente.getId());
        cuentaBL.delete(cuentaAparte.getId());
        bloque("DELETE");
        campo("id eliminado", "mascota " + idMascota + " y cliente " + cliente.getId());
        bloque("VERIFICACION DELETE");
        verificarEliminado(idMascota, () -> mascotaBL.findById(idMascota));
        verificarEliminado(cliente.getId(), () -> clienteBL.findById(cliente.getId()));
        // La cuenta se da de baja junto con el cliente
        verificarEliminado(cuenta.getId(), () -> cuentaBL.findById(cuenta.getId()));

        regla("Login del cliente dado de baja", () -> cuentaBL.login(cuenta.getCorreo(), clave()));
        mostrarReglas();
    }

    // ---------------------------------------------------------------- FLUJO CLINICO

    private static void probarFlujoClinico() throws BLException {
        // Datos de apoyo: no se muestran
        Cuenta cuentaCliente = nuevaCuenta("cli2");
        Cuenta cuentaVet = nuevaCuenta("vet");
        cuentaBL.insert(cuentaCliente);
        cuentaBL.insert(cuentaVet);

        List<Horario> horarios = new ArrayList<>();
        for (DiaSemana dia : DiaSemana.values()) {
            Horario horario = new Horario(0, true, dia, LocalTime.of(8, 0), LocalTime.of(18, 0));
            horarioBL.insert(horario);
            horarios.add(horario);
        }

        Cliente cliente = new Cliente();
        completarPersona(cliente, cuentaCliente, "4");
        clienteBL.insert(cliente);

        Veterinario veterinario = new Veterinario();
        completarPersona(veterinario, cuentaVet, "5");
        veterinario.setNumeroColegiatura("CMV-" + SUFIJO);
        veterinario.setHorarios(horarios);
        veterinarioBL.insert(veterinario);

        Administrador administrador = administradorExistente();

        Mascota luna = nuevaMascota(cliente, "Luna", 4.2);
        Mascota toby = nuevaMascota(cliente, "Toby", 6.0);
        mascotaBL.insert(luna);
        mascotaBL.insert(toby);

        Categoria categoria = nuevaCategoria("Flujo");
        Articulo vacuna = nuevoArticulo("Vacuna antirrabica", 35, 10, categoria);
        Articulo shampoo = nuevoArticulo("Shampoo 500 ml", 25, 5, categoria);
        articuloBL.insert(vacuna);
        articuloBL.insert(shampoo);

        // 30 minutos cada uno: consulta + bano ocupan 60 minutos al mismo veterinario
        Servicio consulta = nuevoServicio("Consulta", 80, TipoServicio.CONSULTA_MEDICA);
        Servicio bano = nuevoServicio("Bano y corte", 40, TipoServicio.ESTETICA);
        servicioBL.insert(consulta);
        servicioBL.insert(bano);

        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setNombreEnfermedad("Otitis " + SUFIJO);
        diagnostico.setDescripcion("Inflamacion del oido");
        diagnosticoBL.insert(diagnostico);

        // ------------------------------------------------------------ LOGIN (RF01)
        tituloSeccion("LOGIN DEL VETERINARIO");

        Persona personaVet = cuentaBL.login(cuentaVet.getCorreo(), clave());
        bloque("LOGIN");
        campo("usuario", cuentaVet.getNombreUsuario());
        campo("rol", personaVet.getClass().getSimpleName() + " (id " + personaVet.getId() + ")");
        verificar("el login devuelve al veterinario", personaVet instanceof Veterinario);

        // ------------------------------------------------------------ CITA PROGRAMADA (RF10, RF11, RF13)
        tituloSeccion("CITA PROGRAMADA");

        LocalDate manana = LocalDate.now().plusDays(1);
        Cita programada = new Cita();
        programada.setFechaHora(manana.atTime(10, 0));
        programada.setEstado(EstadoCita.PENDIENTE);
        programada.setMascota(luna);
        programada.agregarDetalle(0, true, "Revisar el oido", consulta, veterinario);
        programada.agregarDetalle(0, true, null, bano, veterinario);
        citaBL.insert(programada);
        bloque("INSERT");
        campo("id", programada.getId());
        campo("fecha", FORMATO_FECHA.format(programada.getFechaHora()));
        campo("estado", programada.getEstado());
        campo("servicios", "Consulta + Bano y corte (60 min, mismo veterinario)");

        regla("Cita con fecha de ayer",
                () -> citaBL.insert(citaNueva(luna, LocalDateTime.now().minusDays(1), false, veterinario, consulta)));
        regla("Cita a las 17:45 de 60 min (el horario termina a las 18:00)",
                () -> citaBL.insert(citaNueva(luna, manana.atTime(17, 45), false, veterinario, consulta, bano)));
        regla("Cita a las 10:30 con el mismo veterinario (se cruza con 10:00-11:00)",
                () -> citaBL.insert(citaNueva(toby, manana.atTime(10, 30), false, veterinario, consulta)));
        regla("Cambiar la mascota de la cita", () -> {
            Cita cambio = citaBL.findById(programada.getId());
            cambio.setMascota(toby);
            citaBL.update(cambio);
        });
        regla("Convertir la cita en emergencia", () -> {
            Cita cambio = citaBL.findById(programada.getId());
            cambio.setEsEmergencia(true);
            citaBL.update(cambio);
        });
        regla("Aprobar la cita sin administrador", () -> {
            Cita cambio = citaBL.findById(programada.getId());
            cambio.setEstado(EstadoCita.APROBADO);
            cambio.setAdministrador(null);
            citaBL.update(cambio);
        });
        mostrarReglas();

        Cita reprogramada = citaBL.findById(programada.getId());
        reprogramada.setFechaHora(manana.atTime(11, 0));
        citaBL.update(reprogramada);
        Cita aprobada = citaBL.findById(programada.getId());
        aprobada.setEstado(EstadoCita.APROBADO);
        aprobada.setAdministrador(administrador);
        citaBL.update(aprobada);
        bloque("UPDATE (reprogramar y aprobar)");
        Cita leida = citaBL.findById(programada.getId());
        campo("fecha", "10:00 -> " + leida.getFechaHora().toLocalTime());
        campo("estado", "PENDIENTE -> " + leida.getEstado());
        campo("administrador", administrador.getNombres() + " " + administrador.getApellidoPaterno());

        bloque("CONSULTAS");
        List<Cita> delCliente = citaBL.findByCliente(cliente.getId());
        List<Cita> agenda = citaBL.findByFecha(manana, veterinario.getId());
        campo("del cliente", delCliente.size() + " cita(s)");
        campo("agenda vet", agenda.size() + " cita(s) el " + manana);
        verificar("la agenda del veterinario incluye la cita",
                agenda.stream().anyMatch(c -> c.getId() == programada.getId()));

        DetalleCita detalleFuturo = buscarDetalle(leida, consulta.getNombre());
        regla("Atender ahora un servicio de la cita de manana", () -> {
            Atencion antes = new Atencion();
            antes.setDetalleCita(detalleFuturo);
            antes.setFechaHora(ahora());
            antes.setMotivoConsulta("Antes de tiempo");
            atencionBL.insert(antes);
        });
        mostrarReglas();

        Cita cancelada = citaBL.findById(programada.getId());
        cancelada.setEstado(EstadoCita.CANCELADO);
        citaBL.update(cancelada);
        bloque("UPDATE (cancelar)");
        campo("estado", "APROBADO -> " + citaBL.findById(programada.getId()).getEstado());
        regla("Volver a aprobar una cita CANCELADO (estado final)", () -> {
            Cita cambio = citaBL.findById(programada.getId());
            cambio.setEstado(EstadoCita.APROBADO);
            cambio.setAdministrador(administrador);
            citaBL.update(cambio);
        });
        mostrarReglas();
        citaBL.delete(programada.getId());
        bloque("DELETE (sin atenciones ni cobros)");
        verificarEliminado(programada.getId(), () -> citaBL.findById(programada.getId()));

        // ------------------------------------------------------------ CITA DE EMERGENCIA
        tituloSeccion("CITA DE EMERGENCIA");

        regla("Emergencia registrada con 3 horas de antiguedad",
                () -> citaBL.insert(citaNueva(luna, ahora().minusHours(3), true, veterinario, consulta)));

        // Llego hace 30 minutos: se atiende de inmediato y el administrador la aprueba despues
        Cita cita = citaNueva(luna, ahora().minusMinutes(30), true, veterinario, consulta, bano);
        citaBL.insert(cita);
        bloque("INSERT");
        campo("id", cita.getId());
        campo("fecha", FORMATO_FECHA.format(cita.getFechaHora()));
        campo("estado", cita.getEstado() + " (emergencia)");
        mostrarReglas();

        DetalleCita detalleConsulta = buscarDetalle(citaBL.findById(cita.getId()), consulta.getNombre());
        DetalleCita detalleBano = buscarDetalle(citaBL.findById(cita.getId()), bano.getNombre());

        // ------------------------------------------------------------ ATENCION (RF14)
        tituloSeccion("ATENCION");

        regla("Diagnostico en el servicio estetico 'Bano y corte'", () -> {
            Atencion conDiagnostico = nuevaAtencion(detalleBano, null);
            conDiagnostico.agregarDiagnostico(true, diagnostico, NivelGravedad.LEVE, "no corresponde");
            atencionBL.insert(conDiagnostico);
        });
        regla("Atencion de 'Consulta' sin motivo de consulta",
                () -> atencionBL.insert(nuevaAtencion(detalleConsulta, null)));
        regla("Atencion con fecha futura", () -> {
            Atencion futura = nuevaAtencion(detalleConsulta, "Futura");
            futura.setFechaHora(ahora().plusHours(1));
            atencionBL.insert(futura);
        });

        Atencion atencionConsulta = nuevaAtencion(detalleConsulta, "Se rasca la oreja");
        atencionConsulta.setObservaciones("Otitis leve");
        atencionConsulta.agregarInsumoUtilizado(0, true, vacuna, 1, true);
        atencionConsulta.agregarDiagnostico(true, diagnostico, NivelGravedad.LEVE, "Oido izquierdo");
        atencionBL.insert(atencionConsulta);
        int stockVacuna = stock(vacuna);
        bloque("INSERT (consulta, cita PENDIENTE de emergencia)");
        campo("id", atencionConsulta.getId());
        campo("diagnostico", diagnostico.getNombreEnfermedad() + " (LEVE)");
        campo("insumo", "Vacuna antirrabica x1 (cobrable)");
        campo("stock vacuna", "10 -> " + stockVacuna);
        verificar("la vacuna descuenta stock (9)", stockVacuna == 9);

        Atencion atencionBano = nuevaAtencion(detalleBano, null);
        atencionBano.agregarInsumoUtilizado(0, true, shampoo, 1, false);
        atencionBL.insert(atencionBano);
        int stockShampoo = stock(shampoo);
        bloque("INSERT (bano)");
        campo("id", atencionBano.getId());
        campo("insumo", "Shampoo 500 ml x1 (no cobrable)");
        campo("stock shampoo", "5 -> " + stockShampoo);
        verificar("el shampoo descuenta stock (4)", stockShampoo == 4);

        regla("Segunda atencion del servicio 'Consulta'",
                () -> atencionBL.insert(nuevaAtencion(detalleConsulta, "Repetida")));
        regla("Rechazar la cita de emergencia que ya tiene atenciones", () -> {
            Cita rechazo = citaBL.findById(cita.getId());
            rechazo.setEstado(EstadoCita.RECHAZADO);
            rechazo.setAdministrador(administrador);
            citaBL.update(rechazo);
        });
        mostrarReglas();

        Cita emergenciaAprobada = citaBL.findById(cita.getId());
        emergenciaAprobada.setEstado(EstadoCita.APROBADO);
        emergenciaAprobada.setAdministrador(administrador);
        citaBL.update(emergenciaAprobada);
        bloque("UPDATE (aprobar la emergencia)");
        campo("estado", "PENDIENTE -> " + citaBL.findById(cita.getId()).getEstado());

        // ------------------------------------------------------------ RECETA (RF15, RF17)
        tituloSeccion("RECETA");

        Receta receta = new Receta();
        receta.setAtencion(atencionBL.findById(atencionConsulta.getId()));
        receta.setFechaEmision(LocalDate.now());
        receta.setIndicacionesGenerales("Limpiar el oido cada dia");
        receta.agregarDetalle(0, true, vacuna, "1 dosis", "cada 24 h", 7, 1);
        recetaBL.insert(receta);
        int stockTrasReceta = stock(vacuna);
        bloque("INSERT");
        campo("id", receta.getId());
        campo("medicamento", "Vacuna antirrabica | 1 dosis | cada 24 h | 7 dias");
        verificar("la receta no mueve stock", stockTrasReceta == 9);

        regla("Receta en el servicio estetico 'Bano y corte'", () -> {
            Receta paraBano = new Receta();
            paraBano.setAtencion(atencionBL.findById(atencionBano.getId()));
            paraBano.setFechaEmision(LocalDate.now());
            paraBano.setIndicacionesGenerales("no corresponde");
            paraBano.agregarDetalle(0, true, vacuna, "1", "1", 1, 1);
            recetaBL.insert(paraBano);
        });
        regla("Anular la atencion de 'Consulta' (tiene receta)", () -> atencionBL.delete(atencionConsulta.getId()));
        mostrarReglas();

        // ------------------------------------------------------------ MODIFICAR Y ANULAR ATENCIONES (RF14)
        tituloSeccion("MODIFICAR Y ANULAR ATENCIONES");

        Atencion modificada = atencionBL.findById(atencionConsulta.getId());
        modificada.getInsumosUtilizados().get(0).setCantidadUtilizada(2);
        atencionBL.update(modificada);
        stockVacuna = stock(vacuna);
        bloque("UPDATE (consulta: vacuna x1 -> x2)");
        campo("stock vacuna", "9 -> " + stockVacuna);
        verificar("se descuenta solo la diferencia (8)", stockVacuna == 8);

        atencionBL.delete(atencionBano.getId());
        stockShampoo = stock(shampoo);
        bloque("DELETE (anular la atencion del bano)");
        campo("stock shampoo", "4 -> " + stockShampoo);
        verificar("anular devuelve el insumo al stock (5)", stockShampoo == 5);
        verificarEliminado(atencionBano.getId(), () -> atencionBL.findById(atencionBano.getId()));

        regla("Volver a atender el bano (su atencion se anulo)",
                () -> atencionBL.insert(nuevaAtencion(detalleBano, null)));
        mostrarReglas();

        // ------------------------------------------------------------ CITA COMPLETADA (RF11)
        tituloSeccion("CITA COMPLETADA");

        // El bano tiene su atencion anulada: cuenta como no realizado y no impide completar
        Cita completada = citaBL.findById(cita.getId());
        completada.setEstado(EstadoCita.COMPLETADO);
        citaBL.update(completada);
        bloque("UPDATE");
        campo("estado", "APROBADO -> " + citaBL.findById(cita.getId()).getEstado());
        campo("servicios", "Consulta atendida, Bano anulado (no realizado)");

        regla("Modificar la atencion con la cita COMPLETADO", () -> {
            Atencion cerrada = atencionBL.findById(atencionConsulta.getId());
            cerrada.setObservaciones("cambio tardio");
            atencionBL.update(cerrada);
        });
        mostrarReglas();

        // ------------------------------------------------------------ HISTORIAL CLINICO (RF16)
        tituloSeccion("HISTORIAL CLINICO DE " + luna.getNombre().toUpperCase());

        List<Atencion> historial = atencionBL.findByMascota(luna.getId());
        for (Atencion atencion : historial) {
            bloque(FORMATO_FECHA.format(atencion.getFechaHora()) + " - "
                    + atencion.getDetalleCita().getServicio().getNombre());
            campo("motivo", atencion.getMotivoConsulta());
            for (AtencionDiagnostico ad : atencion.getDiagnosticos()) {
                campo("diagnostico", ad.getDiagnostico().getNombreEnfermedad() + " (" + ad.getNivelGravedad() + ")");
            }
            for (InsumoUtilizado insumo : atencion.getInsumosUtilizados()) {
                campo("insumo", insumo.getArticulo().getNombre() + " x" + insumo.getCantidadUtilizada());
            }
            for (Receta r : recetaBL.findByAtencion(atencion.getId())) {
                campo("receta", "id " + r.getId() + " | " + r.getIndicacionesGenerales());
            }
        }
        verificar("el historial tiene la atencion de la consulta (la anulada no aparece)",
                historial.size() == 1 && historial.get(0).getId() == atencionConsulta.getId());

        // ------------------------------------------------------------ BOLETA (RF18)
        tituloSeccion("BOLETA");

        regla("Cobrar el bano (su atencion se anulo)", () -> {
            Boleta otra = nuevaBoleta(cliente, nuevaSerie(1));
            otra.agregarDetalleServicio(0, true, 1, 40, detalleBano);
            boletaBL.insert(otra);
        });

        Atencion atencionGuardada = atencionBL.findById(atencionConsulta.getId());
        Boleta boleta = nuevaBoleta(cliente, nuevaSerie(0));
        boleta.setMetodoPago(MetodoPago.YAPE);
        boleta.agregarDetalleServicio(0, true, 1, 80, atencionGuardada.getDetalleCita());
        boleta.agregarDetalleArticulo(0, true, 2, 35, vacuna, atencionGuardada.getInsumosUtilizados().get(0));
        boleta.agregarDetalleArticulo(0, true, 1, 25, shampoo, null);
        boletaBL.insert(boleta);

        Boleta guardada = boletaBL.findById(boleta.getId());
        stockShampoo = stock(shampoo);
        bloque("INSERT");
        campo("serie", guardada.getSerieCorrelativo());
        campo("cliente", cliente.getNombres() + " " + cliente.getApellidoPaterno());
        campo("metodo de pago", guardada.getMetodoPago());
        for (DetalleBoleta detalle : guardada.getDetalles()) {
            campo("linea", String.format("%-34s S/ %7.2f", describirLinea(detalle), detalle.getSubtotal()));
        }
        campo("total", String.format("S/ %.2f", guardada.getTotal()));
        campo("stock shampoo", "5 -> " + stockShampoo + " (venta en caja)");
        verificar("el total es 80 + 2 x 35 + 25 = 175.00", Math.abs(guardada.getTotal() - 175) < 0.01);
        verificar("la venta en caja descuenta stock (4)", stockShampoo == 4);
        verificar("el insumo cobrado no vuelve a descontar (8)", stock(vacuna) == 8);

        regla("Facturar de nuevo el servicio 'Consulta'", () -> {
            Boleta otra = nuevaBoleta(cliente, nuevaSerie(2));
            otra.agregarDetalleServicio(0, true, 1, 80, detalleConsulta);
            boletaBL.insert(otra);
        });
        regla("Vender 99 unidades de Shampoo (stock 4)", () -> {
            Boleta otra = nuevaBoleta(cliente, nuevaSerie(3));
            otra.agregarDetalleArticulo(0, true, 99, 25, shampoo, null);
            boletaBL.insert(otra);
        });
        mostrarReglas();

        boletaBL.delete(boleta.getId());
        stockShampoo = stock(shampoo);
        bloque("DELETE (anular)");
        campo("stock shampoo", "4 -> " + stockShampoo);
        verificar("anular devuelve la venta en caja al stock (5)", stockShampoo == 5);
        verificar("anular no devuelve el insumo cobrado (8)", stock(vacuna) == 8);
        verificarEliminado(boleta.getId(), () -> boletaBL.findById(boleta.getId()));

        regla("Anular de nuevo la boleta", () -> boletaBL.delete(boleta.getId()));
        regla("Cobrar otra vez la consulta (su boleta se anulo)", () -> {
            Boleta otra = nuevaBoleta(cliente, nuevaSerie(4));
            otra.agregarDetalleServicio(0, true, 1, 80, detalleConsulta);
            boletaBL.insert(otra);
        });
        mostrarReglas();

        // ------------------------------------------------------------ BAJAS
        tituloSeccion("BAJAS");

        regla("Eliminar la mascota 'Luna' (tiene citas)", () -> mascotaBL.delete(luna.getId()));
        regla("Eliminar la cita (tiene servicios atendidos y cobrados)", () -> citaBL.delete(cita.getId()));
        regla("Eliminar el cliente (su mascota tiene citas)", () -> clienteBL.delete(cliente.getId()));
        regla("Eliminar el veterinario (esta en una cita)", () -> veterinarioBL.delete(veterinario.getId()));
        regla("Eliminar el servicio 'Consulta' (esta en una cita)", () -> servicioBL.delete(consulta.getId()));
        regla("Eliminar el articulo 'Vacuna' (se uso en una atencion)", () -> articuloBL.delete(vacuna.getId()));
        regla("Eliminar el diagnostico (esta en una atencion)", () -> diagnosticoBL.delete(diagnostico.getId()));
        mostrarReglas("REGLAS DE NEGOCIO (no eliminar con dependientes)");

        System.out.println();
        mascotaBL.delete(toby.getId());
        baja("mascota Toby (sin citas)", toby.getId(), () -> mascotaBL.findById(toby.getId()));
        System.out.println("  Los demas datos del flujo quedan en la base como historial clinico.");
    }

    // ---------------------------------------------------------------- ACCESO (RF01, RF02, RF04, RF05)

    private static void probarAcceso() throws BLException {
        tituloSeccion("ACCESO: REGISTRO, VERIFICACION Y RECUPERACION");

        // ------------------------------------------------------------ Auto-registro con verificacion de correo
        String correoNuevo = "registro" + SUFIJO + "@mail.com";
        Cliente datos = datosCliente("2");
        accesoBL.solicitarRegistro(datos, correoNuevo, clave());
        String tokenRegistro = tokenDelUltimoCorreo();

        bloque("REGISTRO DE UN CLIENTE");
        campo("correo enviado a", correos.ultimo()[0]);
        campo("asunto", correos.ultimo()[1]);
        verificar("el correo de confirmacion llega al correo registrado", correos.ultimo()[0].equals(correoNuevo));
        verificar("el correo trae un enlace con el token", tokenRegistro.length() == 64);
        verificar("antes de confirmar todavia no existe el cliente", clienteBL.findByDni(datos.getDni()) == null);

        regla("Ingresar antes de verificar el correo", () -> cuentaBL.login(correoNuevo, clave()));
        regla("Confirmar con un enlace inventado", () -> accesoBL.confirmarRegistro("0".repeat(64)));

        Cliente registrado = accesoBL.confirmarRegistro(tokenRegistro);
        campo("cliente creado", registrado.getNombres() + " (id " + registrado.getId() + ")");
        campo("correo", registrado.getCuenta().getCorreo());
        Persona personaRegistrada = cuentaBL.login(correoNuevo, clave());
        verificar("el cliente verificado puede ingresar", personaRegistrada instanceof Cliente
                && personaRegistrada.getId() == registrado.getId());
        verificar("el correo coincide con el de la cuenta", registrado.getCuenta().getCorreo().equals(correoNuevo));

        regla("Confirmar dos veces el mismo enlace", () -> accesoBL.confirmarRegistro(tokenRegistro));
        regla("Registrarse con un correo ya registrado",
                () -> accesoBL.solicitarRegistro(datosCliente("3"), correoNuevo, clave()));
        regla("Registrarse con un DNI ya registrado", () -> {
            Cliente repetido = datosCliente("3");
            repetido.setDni(datos.getDni());
            accesoBL.solicitarRegistro(repetido, "otro" + SUFIJO + "@mail.com", clave());
        });
        regla("Registrarse con una contraseña de menos de 8 caracteres",
                () -> accesoBL.solicitarRegistro(datosCliente("3"), "otro" + SUFIJO + "@mail.com", "abc"));
        regla("Registrarse con un correo sin formato valido",
                () -> accesoBL.solicitarRegistro(datosCliente("3"), "sin-arroba", clave()));
        regla("Registrarse con numeros en los nombres", () -> {
            Cliente conNumeros = datosCliente("3");
            conNumeros.setNombres("Juan3132");
            accesoBL.solicitarRegistro(conNumeros, "otro" + SUFIJO + "@mail.com", clave());
        });
        regla("Registrarse con un telefono de 8 digitos", () -> {
            Cliente corto = datosCliente("3");
            corto.setTelefono("12345678");
            accesoBL.solicitarRegistro(corto, "otro" + SUFIJO + "@mail.com", clave());
        });
        mostrarReglas();

        // ------------------------------------------------------------ Recuperar la contrasena
        int enviadosAntes = correos.cantidad();
        accesoBL.solicitarRecuperacion("noexiste" + SUFIJO + "@mail.com");
        bloque("RECUPERAR CONTRASEÑA");
        verificar("un correo que no esta registrado no genera ningun envio ni avisa", correos.cantidad() == enviadosAntes);

        accesoBL.solicitarRecuperacion(correoNuevo);
        campo("correo enviado a", correos.ultimo()[0]);
        verificar("el correo registrado recibe el enlace", correos.cantidad() == enviadosAntes + 1
                && correos.ultimo()[0].equals(correoNuevo));
        String primerToken = tokenDelUltimoCorreo();

        accesoBL.solicitarRecuperacion(correoNuevo);
        String segundoToken = tokenDelUltimoCorreo();
        regla("Usar el enlace anterior despues de pedir uno nuevo",
                () -> accesoBL.restablecerContrasena(primerToken, "Cambio" + SUFIJO));
        regla("Restablecer con una contraseña de menos de 8 caracteres",
                () -> accesoBL.restablecerContrasena(segundoToken, "corta"));
        regla("Restablecer con un enlace inventado",
                () -> accesoBL.restablecerContrasena("0".repeat(64), "Cambio" + SUFIJO));

        String claveNueva = "Nueva" + SUFIJO;
        accesoBL.restablecerContrasena(segundoToken, claveNueva);
        Persona conClaveNueva = cuentaBL.login(correoNuevo, claveNueva);
        verificar("ingresa con la contraseña nueva", conClaveNueva.getId() == registrado.getId());
        regla("Ingresar con la contraseña anterior", () -> cuentaBL.login(correoNuevo, clave()));
        regla("Usar el mismo enlace dos veces", () -> accesoBL.restablecerContrasena(segundoToken, "Otra" + SUFIJO));
        mostrarReglas();

        // ------------------------------------------------------------ Alta hecha por un administrador
        String correoMostrador = "mostrador" + SUFIJO + "@mail.com";
        Cliente enMostrador = datosCliente("8");
        accesoBL.registrarCliente(enMostrador, correoMostrador);
        String tokenActivacion = tokenDelUltimoCorreo();

        bloque("ALTA DE UN CLIENTE POR EL ADMINISTRADOR");
        campo("cliente", enMostrador.getNombres() + " (id " + enMostrador.getId() + ")");
        campo("asunto", correos.ultimo()[1]);
        verificar("la persona y su cuenta se crearon juntas", enMostrador.getId() > 0 && enMostrador.getCuenta().getId() > 0);
        regla("Ingresar antes de elegir la contraseña", () -> cuentaBL.login(correoMostrador, clave()));
        accesoBL.restablecerContrasena(tokenActivacion, clave());
        Persona personaMostrador = cuentaBL.login(correoMostrador, clave());
        verificar("elige su contraseña con el enlace y entra", personaMostrador instanceof Cliente
                && personaMostrador.getId() == enMostrador.getId());
        regla("Registrar a otro cliente con el mismo correo", () -> accesoBL.registrarCliente(datosCliente("3"), correoMostrador));
        regla("Registrar a un cliente con un correo sin formato valido",
                () -> accesoBL.registrarCliente(datosCliente("3"), "sin-arroba"));

        String correoVet = "vetacceso" + SUFIJO + "@mail.com";
        Veterinario nuevoVet = new Veterinario();
        completarDatos(nuevoVet, "9");
        nuevoVet.setNumeroColegiatura("CMV-A" + SUFIJO);
        accesoBL.registrarVeterinario(nuevoVet, correoVet);
        verificar("el veterinario se crea con su cuenta", nuevoVet.getId() > 0 && nuevoVet.getCuenta().getId() > 0);
        accesoBL.restablecerContrasena(tokenDelUltimoCorreo(), clave());
        verificar("el veterinario elige su contraseña y entra", cuentaBL.login(correoVet, clave()) instanceof Veterinario);
        regla("Registrar a un veterinario con la colegiatura repetida", () -> {
            Veterinario repetido = new Veterinario();
            completarDatos(repetido, "3");
            repetido.setNumeroColegiatura(nuevoVet.getNumeroColegiatura());
            accesoBL.registrarVeterinario(repetido, "vetdos" + SUFIJO + "@mail.com");
        });
        mostrarReglas();

        // ------------------------------------------------------------ Bajas: la persona y su cuenta
        bloque("BAJAS");
        clienteBL.delete(registrado.getId());
        clienteBL.delete(enMostrador.getId());
        veterinarioBL.delete(nuevoVet.getId());
        baja("cliente registrado por si mismo", registrado.getId(), () -> clienteBL.findById(registrado.getId()));
        baja("cliente registrado por el administrador", enMostrador.getId(), () -> clienteBL.findById(enMostrador.getId()));
        baja("veterinario registrado por el administrador", nuevoVet.getId(), () -> veterinarioBL.findById(nuevoVet.getId()));
        regla("Ingresar con la cuenta de un cliente dado de baja", () -> cuentaBL.login(correoNuevo, claveNueva));
        mostrarReglas();
    }

    // ---------------------------------------------------------------- Impresion

    private static void tituloSeccion(String nombre) {
        System.out.println();
        System.out.println();
        System.out.println("============================================================");
        System.out.println(" " + nombre);
        System.out.println("============================================================");
    }

    private static void bloque(String etiqueta) {
        System.out.println();
        System.out.println("[" + etiqueta + "]");
    }

    private static void campo(String etiqueta, Object valor) {
        System.out.printf("  %-14s: %s%n", etiqueta, valor);
    }

    // Una verificacion correcta no imprime nada; solo se informa la que falla
    private static void verificar(String que, boolean cumple) {
        if (!cumple) {
            fallas++;
            System.out.println("  FALLA: " + que);
        }
    }

    // findById lanza NoEncontradoException (o devuelve null) cuando el registro no existe o esta dado de baja
    private static boolean noExiste(Busqueda busqueda) throws BLException {
        try {
            return busqueda.buscar() == null;
        } catch (NoEncontradoException ex) {
            return true;
        }
    }

    private static void verificarEliminado(int id, Busqueda busqueda) throws BLException {
        if (noExiste(busqueda)) {
            System.out.println("  buscar id " + id + " -> no encontrado. Se dio de baja correctamente.");
        } else {
            fallas++;
            System.out.println("  buscar id " + id + " -> SIGUE EXISTIENDO (no se dio de baja)");
        }
    }

    private static void baja(String nombre, int id, Busqueda busqueda) throws BLException {
        boolean eliminado = noExiste(busqueda);
        if (!eliminado) {
            fallas++;
        }
        System.out.println("  " + nombre + " (id " + id + ") -> " + (eliminado ? "eliminado" : "SIGUE EXISTIENDO"));
    }

    // Regla de negocio: la BL debe rechazar el caso. La linea se guarda y se imprime al cerrar la seccion
    private static void regla(String caso, Caso accion) {
        try {
            accion.ejecutar();
            fallas++;
            reglas.add("  " + caso + ": NO se rechazo (falla la regla)");
        } catch (BLException ex) {
            reglas.add("  " + caso + " -> Rechazado: " + ex.getMessage());
        }
    }

    private static void mostrarReglas() {
        mostrarReglas("REGLAS DE NEGOCIO");
    }

    private static void mostrarReglas(String titulo) {
        if (reglas.isEmpty()) {
            return;
        }
        bloque(titulo);
        for (String linea : reglas) {
            System.out.println(linea);
        }
        reglas.clear();
    }

    private static String causa(Exception ex) {
        StringBuilder texto = new StringBuilder(ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage());
        Throwable causa = ex.getCause();
        while (causa != null) {
            texto.append(" <- ").append(causa.getMessage());
            causa = causa.getCause();
        }
        return texto.toString();
    }

    // Se muestran como maximo 5 filas: la base puede tener muchos registros
    private static void listarCategorias(List<Categoria> categorias) {
        bloque("LISTADO (" + categorias.size() + " activos en este momento)");
        int n = 1;
        for (Categoria categoria : categorias) {
            if (n > 5) {
                System.out.println("  ... y " + (categorias.size() - 5) + " mas");
                break;
            }
            System.out.printf("  %d) id=%-4d %s%n", n, categoria.getId(), categoria.getNombre());
            n++;
        }
    }

    private static void listarArticulos(List<Articulo> articulos) {
        bloque("LISTADO (" + articulos.size() + " activos en este momento)");
        int n = 1;
        for (Articulo articulo : articulos) {
            if (n > 5) {
                System.out.println("  ... y " + (articulos.size() - 5) + " mas");
                break;
            }
            System.out.printf("  %d) id=%-4d %-25s S/ %6.2f  stock=%d%n",
                    n, articulo.getId(), articulo.getNombre(), articulo.getPrecioBase(), articulo.getStockActual());
            n++;
        }
    }

    private static void listarServicios(List<Servicio> servicios) {
        bloque("LISTADO (" + servicios.size() + " activos en este momento)");
        int n = 1;
        for (Servicio servicio : servicios) {
            if (n > 5) {
                System.out.println("  ... y " + (servicios.size() - 5) + " mas");
                break;
            }
            System.out.printf("  %d) id=%-4d %-30s S/ %.2f%n", n, servicio.getId(), servicio.getNombre(), servicio.getPrecioBase());
            n++;
        }
    }

    private static void listarMascotas(List<Mascota> mascotas) {
        bloque("LISTADO (" + mascotas.size() + " activos en este momento)");
        int n = 1;
        for (Mascota mascota : mascotas) {
            if (n > 5) {
                System.out.println("  ... y " + (mascotas.size() - 5) + " mas");
                break;
            }
            System.out.printf("  %d) id=%-4d %-20s %-6s peso=%.1f%n",
                    n, mascota.getId(), mascota.getNombre(), mascota.getTipoMascota(), mascota.getPeso());
            n++;
        }
    }

    private static String describirLinea(DetalleBoleta detalle) {
        if (detalle instanceof DetalleBoletaServicio servicio) {
            return "servicio " + servicio.getDetalleCita().getServicio().getNombre();
        }
        DetalleBoletaArticulo articulo = (DetalleBoletaArticulo) detalle;
        String origen = articulo.getInsumoUtilizado() == null ? "venta " : "insumo ";
        return origen + articulo.getArticulo().getNombre() + " x" + articulo.getCantidad();
    }

    // ---------------------------------------------------------------- Datos de prueba

    private static LocalDateTime ahora() {
        return LocalDateTime.now().withSecond(0).withNano(0);
    }

    private static String numero() {
        return String.format("%07d", Long.parseLong(SUFIJO) % 10_000_000);
    }

    // Contrasena en texto plano de todas las cuentas de prueba (la BL guarda su hash)
    private static String clave() {
        return "Clave" + SUFIJO;
    }

    private static int stock(Articulo articulo) throws BLException {
        return articuloBL.findById(articulo.getId()).getStockActual();
    }

    private static Categoria nuevaCategoria(String prefijo) throws BLException {
        Categoria categoria = new Categoria();
        categoria.setNombre(prefijo + " " + SUFIJO);
        categoria.setDescripcion("Categoria de prueba");
        categoriaBL.insert(categoria);
        return categoria;
    }

    private static Articulo nuevoArticulo(String nombre, double precio, int stock, Categoria categoria) {
        Articulo articulo = new Articulo();
        articulo.setNombre(nombre);
        articulo.setDescripcion(nombre);
        articulo.setPrecioBase(precio);
        articulo.setStockActual(stock);
        articulo.setStockMinimo(1);
        articulo.agregarCategoria(categoria);
        return articulo;
    }

    private static Servicio nuevoServicio(String nombre, double precio, TipoServicio tipo) {
        Servicio servicio = new Servicio();
        servicio.setNombre(nombre + " " + SUFIJO);
        servicio.setDescripcion(nombre);
        servicio.setPrecioBase(precio);
        servicio.setDuracionEstimada(30);
        servicio.setTipo(tipo);
        return servicio;
    }

    // Guarda los correos en vez de enviarlos
    private static class CorreoEnMemoria implements CorreoService {
        private final List<String[]> enviados = new ArrayList<>();

        @Override
        public void enviar(String destino, String asunto, String cuerpo) {
            enviados.add(new String[]{destino, asunto, cuerpo});
        }

        int cantidad() {
            return enviados.size();
        }

        // {destino, asunto, cuerpo}
        String[] ultimo() {
            return enviados.get(enviados.size() - 1);
        }
    }

    // El token es lo que sigue a "token=" en el enlace del ultimo correo
    private static String tokenDelUltimoCorreo() {
        String cuerpo = correos.ultimo()[2];
        int inicio = cuerpo.indexOf("token=") + "token=".length();
        int fin = inicio;
        while (fin < cuerpo.length() && Character.isLetterOrDigit(cuerpo.charAt(fin))) {
            fin++;
        }
        return cuerpo.substring(inicio, fin);
    }

    // Datos de un cliente sin cuenta (la cuenta se crea al registrarse)
    private static Cliente datosCliente(String digito) {
        Cliente cliente = new Cliente();
        completarDatos(cliente, digito);
        return cliente;
    }

    // Igual que completarPersona pero sin cuenta; el digito distingue a cada persona
    private static void completarDatos(Persona persona, String digito) {
        persona.setDni(digito + numero());
        persona.setTelefono("9" + digito + numero());
        persona.setNombres("Nombre");
        persona.setApellidoPaterno("Paterno");
        persona.setApellidoMaterno("Materno");
    }

    private static Cuenta nuevaCuenta(String prefijo) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombreUsuario(prefijo + SUFIJO);
        cuenta.setCorreo(prefijo + SUFIJO + "@mail.com");
        cuenta.setPassword(clave());
        cuenta.setFechaCreacion(LocalDate.now());
        return cuenta;
    }

    // El digito distingue a cada persona: asi el DNI y el telefono son unicos en cada ejecucion
    private static void completarPersona(Persona persona, Cuenta cuenta, String digito) {
        persona.setCuenta(cuenta);
        persona.setDni(digito + numero());
        persona.setTelefono("9" + digito + numero());
        persona.setNombres("Nombre" + digito);
        persona.setApellidoPaterno("Paterno");
        persona.setApellidoMaterno("Materno");
    }

    private static Mascota nuevaMascota(Cliente dueno, String nombre, double peso) {
        Mascota mascota = new Mascota();
        mascota.setCliente(dueno);
        mascota.setNombre(nombre);
        mascota.setSexo(SexoMascota.MACHO);
        mascota.setPeso(peso);
        mascota.setFechaNacimiento(LocalDate.of(2022, 3, 1));
        mascota.setTipoMascota(TipoMascota.PERRO);
        mascota.setRaza("Mestizo");
        return mascota;
    }

    // Reutiliza un administrador existente: el sistema nunca debe quedarse sin uno
    private static Administrador administradorExistente() throws BLException {
        List<Administrador> administradores = administradorBL.findAll();
        if (!administradores.isEmpty()) {
            return administradores.get(0);
        }
        Cuenta cuenta = nuevaCuenta("adm");
        cuentaBL.insert(cuenta);
        Administrador administrador = new Administrador();
        completarPersona(administrador, cuenta, "6");
        Horario horario = new Horario(0, true, DiaSemana.DOMINGO, LocalTime.of(8, 0), LocalTime.of(12, 0));
        horarioBL.insert(horario);
        administrador.setHorarios(List.of(horario));
        administradorBL.insert(administrador);
        return administrador;
    }

    private static Cita citaNueva(Mascota mascota, LocalDateTime fecha, boolean emergencia,
                                  Veterinario veterinario, Servicio... servicios) {
        Cita cita = new Cita();
        cita.setFechaHora(fecha);
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setMascota(mascota);
        cita.setEsEmergencia(emergencia);
        for (Servicio servicio : servicios) {
            cita.agregarDetalle(0, true, null, servicio, veterinario);
        }
        return cita;
    }

    private static Atencion nuevaAtencion(DetalleCita detalle, String motivo) {
        Atencion atencion = new Atencion();
        atencion.setDetalleCita(detalle);
        atencion.setFechaHora(ahora());
        atencion.setMotivoConsulta(motivo);
        return atencion;
    }

    private static DetalleCita buscarDetalle(Cita cita, String nombreServicio) {
        for (DetalleCita detalle : cita.getDetalles()) {
            if (detalle.getServicio().getNombre().equals(nombreServicio)) {
                return detalle;
            }
        }
        throw new IllegalStateException("la cita no tiene el servicio " + nombreServicio);
    }

    private static Boleta nuevaBoleta(Cliente cliente, String serie) {
        Boleta boleta = new Boleta();
        boleta.setCliente(cliente);
        boleta.setFecha(ahora());
        boleta.setMetodoPago(MetodoPago.EFECTIVO);
        boleta.setSerieCorrelativo(serie);
        return boleta;
    }

    private static String nuevaSerie(int desplazamiento) {
        return String.format("B001-%06d", (Long.parseLong(SUFIJO) * 10 + desplazamiento) % 1_000_000);
    }
}
