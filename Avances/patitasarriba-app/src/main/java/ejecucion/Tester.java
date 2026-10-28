package ejecucion;

import patitasarriba.bl.*;
import patitasarriba.bl.impl.atencion.AtencionBLImpl;
import patitasarriba.bl.impl.atencion.DiagnosticoBLImpl;
import patitasarriba.bl.impl.boleta.BoletaBLImpl;
import patitasarriba.bl.impl.cita.CitaBLImpl;
import patitasarriba.bl.impl.horario.HorarioBLImpl;
import patitasarriba.bl.impl.mascota.MascotaBLImpl;
import patitasarriba.bl.impl.producto.ArticuloBLImpl;
import patitasarriba.bl.impl.producto.CategoriaBLImpl;
import patitasarriba.bl.impl.producto.ServicioBLImpl;
import patitasarriba.bl.impl.receta.RecetaBLImpl;
import patitasarriba.bl.impl.usuario.AdministradorBLImpl;
import patitasarriba.bl.impl.usuario.ClienteBLImpl;
import patitasarriba.bl.impl.usuario.CuentaBLImpl;
import patitasarriba.bl.impl.usuario.VeterinarioBLImpl;
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

// Prueba de la capa de negocio (BL) contra la base real. Formato del Lab 6:
// por entidad INSERT / FIND / UPDATE / LISTADO / DELETE y su verificacion; luego el flujo clinico
// (cita -> atencion -> receta -> boleta). Cada seccion cierra con sus reglas de negocio, que la BL debe rechazar.
// Los datos llevan un sufijo distinto en cada ejecucion y se dan de baja al terminar.
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

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println(" TESTER - PATITAS ARRIBA");
        System.out.println("============================================================");
        try {
            probarCategoria();
            probarArticulo();
            probarServicio();
            probarClienteYMascota();
            probarFlujoClinico();
        } catch (BLException | RuntimeException ex) {
            fallas++;
            System.out.println();
            System.out.println("La prueba se detuvo por un error: " + causa(ex));
        }
        System.out.println();
        System.out.println(fallas == 0 ? "Sin fallas." : "FALLAS: " + fallas);
    }

    // ---------------------------------------------------------------- CATEGORIA

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
        verificarEliminado(categoria.getId(), categoriaBL.findById(categoria.getId()) == null);
    }

    // ---------------------------------------------------------------- ARTICULO

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

        double precioAnterior = articulo.getPrecioBase();
        int stockAnterior = articulo.getStockActual();
        articulo.setPrecioBase(49.90);
        articulo.setStockActual(18);
        articuloBL.update(articulo);
        Articulo actualizado = articuloBL.findById(articulo.getId());
        bloque("UPDATE");
        campo("precio", String.format("S/ %.2f -> S/ %.2f", precioAnterior, actualizado.getPrecioBase()));
        campo("stock", stockAnterior + " -> " + actualizado.getStockActual());

        listarArticulos(articuloBL.findAll());

        try {
            categoriaBL.delete(categoria.getId());
            noRechazada("Eliminar la categoria 'Apoyo' (tiene articulos)");
        } catch (BLException ex) {
            rechazada("Eliminar la categoria 'Apoyo' (tiene articulos)", ex);
        }
        mostrarReglas();

        articuloBL.delete(articulo.getId());
        categoriaBL.delete(categoria.getId());
        bloque("DELETE");
        campo("id eliminado", articulo.getId());
        bloque("VERIFICACION DELETE");
        verificarEliminado(articulo.getId(), articuloBL.findById(articulo.getId()) == null);
    }

    // ---------------------------------------------------------------- SERVICIO

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

        servicioBL.delete(servicio.getId());
        bloque("DELETE");
        campo("id eliminado", servicio.getId());
        bloque("VERIFICACION DELETE");
        verificarEliminado(servicio.getId(), servicioBL.findById(servicio.getId()) == null);
    }

    // ---------------------------------------------------------------- CLIENTE Y MASCOTA

    private static void probarClienteYMascota() throws BLException {
        tituloSeccion("CLIENTE Y MASCOTA");

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

        String telefonoAnterior = cliente.getTelefono();
        cliente.setTelefono("98" + String.format("%07d", Long.parseLong(SUFIJO) % 10_000_000));
        clienteBL.update(cliente);
        double pesoAnterior = mascota.getPeso();
        mascota = mascotaBL.findById(mascota.getId());
        mascota.setPeso(13.2);
        mascotaBL.update(mascota);
        bloque("UPDATE");
        campo("telefono", telefonoAnterior + " -> " + clienteBL.findById(cliente.getId()).getTelefono());
        campo("peso", pesoAnterior + " kg -> " + mascotaBL.findById(mascota.getId()).getPeso() + " kg");

        listarMascotas(mascotaBL.findAll());

        // Reglas de negocio: se usa otra cuenta para que solo falle el dato que se quiere probar
        Cuenta cuentaAparte = nuevaCuenta("dup");
        cuentaBL.insert(cuentaAparte);

        try {
            Cliente repetido = new Cliente();
            completarPersona(repetido, cuentaAparte, "1");
            clienteBL.insert(repetido);
            noRechazada("Cliente con DNI repetido '" + cliente.getDni() + "'");
        } catch (BLException ex) {
            rechazada("Cliente con DNI repetido '" + cliente.getDni() + "'", ex);
        }
        try {
            Cliente invalido = new Cliente();
            completarPersona(invalido, cuentaAparte, "7");
            invalido.setDni("ABC12345");
            clienteBL.insert(invalido);
            noRechazada("Cliente con DNI 'ABC12345'");
        } catch (BLException ex) {
            rechazada("Cliente con DNI 'ABC12345'", ex);
        }
        mostrarReglas();

        mascotaBL.delete(mascota.getId());
        clienteBL.delete(cliente.getId());
        cuentaBL.delete(cuenta.getId());
        cuentaBL.delete(cuentaAparte.getId());
        bloque("DELETE");
        campo("id eliminado", "mascota " + mascota.getId() + " y cliente " + cliente.getId());
        bloque("VERIFICACION DELETE");
        verificarEliminado(cliente.getId(), mascotaBL.findById(mascota.getId()) == null
                && clienteBL.findById(cliente.getId()) == null);
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

        Mascota mascota = nuevaMascota(cliente, "Luna", 4.2);
        mascotaBL.insert(mascota);

        Categoria categoria = nuevaCategoria("Flujo");
        Articulo vacuna = nuevoArticulo("Vacuna antirrabica", 35, 10, categoria);
        Articulo shampoo = nuevoArticulo("Shampoo 500 ml", 25, 5, categoria);
        articuloBL.insert(vacuna);
        articuloBL.insert(shampoo);

        Servicio consulta = nuevoServicio("Consulta", 80, TipoServicio.CONSULTA_MEDICA);
        Servicio bano = nuevoServicio("Bano y corte", 40, TipoServicio.ESTETICA);
        servicioBL.insert(consulta);
        servicioBL.insert(bano);

        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setNombreEnfermedad("Otitis " + SUFIJO);
        diagnostico.setDescripcion("Inflamacion del oido");
        diagnosticoBL.insert(diagnostico);

        // ------------------------------------------------------------ CITA
        tituloSeccion("CITA");

        Cita cita = new Cita();
        cita.setFechaHora(LocalDate.now().plusDays(1).atTime(10, 0));
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setMascota(mascota);
        cita.agregarDetalle(0, true, "Revisar el oido", consulta, veterinario);
        cita.agregarDetalle(0, true, null, bano, veterinario);
        citaBL.insert(cita);
        bloque("INSERT");
        campo("id", cita.getId());
        campo("fecha", FORMATO_FECHA.format(cita.getFechaHora()));
        campo("estado", cita.getEstado());
        campo("mascota", mascota.getNombre());
        campo("servicios", "Consulta, Bano y corte");
        campo("veterinario", veterinario.getNombres() + " " + veterinario.getApellidoPaterno());

        try {
            citaBL.insert(citaNueva(mascota, LocalDateTime.now().minusDays(1), consulta, veterinario));
            noRechazada("Cita con fecha de ayer");
        } catch (BLException ex) {
            rechazada("Cita con fecha de ayer", ex);
        }
        try {
            citaBL.insert(citaNueva(mascota, LocalDate.now().plusDays(1).atTime(22, 0), consulta, veterinario));
            noRechazada("Cita a las 22:00 (fuera del horario del veterinario)");
        } catch (BLException ex) {
            rechazada("Cita a las 22:00 (fuera del horario del veterinario)", ex);
        }
        try {
            Cita sinAdmin = citaBL.findById(cita.getId());
            sinAdmin.setEstado(EstadoCita.APROBADO);
            sinAdmin.setAdministrador(null);
            citaBL.update(sinAdmin);
            noRechazada("Cita APROBADA sin administrador");
        } catch (BLException ex) {
            rechazada("Cita APROBADA sin administrador", ex);
        }

        Cita aprobada = citaBL.findById(cita.getId());
        aprobada.setEstado(EstadoCita.APROBADO);
        aprobada.setAdministrador(administrador);
        citaBL.update(aprobada);
        bloque("UPDATE (aprobar)");
        campo("estado", "PENDIENTE -> " + citaBL.findById(cita.getId()).getEstado());
        campo("administrador", administrador.getNombres() + " " + administrador.getApellidoPaterno());

        DetalleCita detalleConsulta = buscarDetalle(citaBL.findById(cita.getId()), consulta.getNombre());
        DetalleCita detalleBano = buscarDetalle(citaBL.findById(cita.getId()), bano.getNombre());

        try {
            Cita incompleta = citaBL.findById(cita.getId());
            incompleta.setEstado(EstadoCita.COMPLETADO);
            citaBL.update(incompleta);
            noRechazada("Completar la cita sin atender sus servicios");
        } catch (BLException ex) {
            rechazada("Completar la cita sin atender sus servicios", ex);
        }
        mostrarReglas();

        // ------------------------------------------------------------ ATENCION
        tituloSeccion("ATENCION");

        // Estas dos reglas se prueban antes de registrar las atenciones (el servicio aun esta libre)
        try {
            Atencion conDiagnostico = new Atencion();
            conDiagnostico.setDetalleCita(detalleBano);
            conDiagnostico.setFechaHora(LocalDateTime.now().withNano(0));
            conDiagnostico.agregarDiagnostico(true, diagnostico, NivelGravedad.LEVE, "no corresponde");
            atencionBL.insert(conDiagnostico);
            noRechazada("Diagnostico en el servicio estetico 'Bano y corte'");
        } catch (BLException ex) {
            rechazada("Diagnostico en el servicio estetico 'Bano y corte'", ex);
        }
        try {
            Atencion sinMotivo = new Atencion();
            sinMotivo.setDetalleCita(detalleConsulta);
            sinMotivo.setFechaHora(LocalDateTime.now().withNano(0));
            atencionBL.insert(sinMotivo);
            noRechazada("Atencion de 'Consulta' sin motivo de consulta");
        } catch (BLException ex) {
            rechazada("Atencion de 'Consulta' sin motivo de consulta", ex);
        }

        Atencion atencionConsulta = new Atencion();
        atencionConsulta.setDetalleCita(detalleConsulta);
        atencionConsulta.setFechaHora(LocalDateTime.now().withNano(0));
        atencionConsulta.setMotivoConsulta("Se rasca la oreja");
        atencionConsulta.setObservaciones("Otitis leve");
        atencionConsulta.agregarInsumoUtilizado(0, true, vacuna, 1, true);
        atencionConsulta.agregarDiagnostico(true, diagnostico, NivelGravedad.LEVE, "Oido izquierdo");
        atencionBL.insert(atencionConsulta);
        int stockVacuna = articuloBL.findById(vacuna.getId()).getStockActual();
        bloque("INSERT (consulta)");
        campo("id", atencionConsulta.getId());
        campo("motivo", atencionConsulta.getMotivoConsulta());
        campo("diagnostico", diagnostico.getNombreEnfermedad() + " (LEVE)");
        campo("insumo", "Vacuna antirrabica x1 (cobrable)");
        campo("stock vacuna", "10 -> " + stockVacuna);
        verificar("la vacuna descuenta stock", stockVacuna == 9);

        Atencion atencionBano = new Atencion();
        atencionBano.setDetalleCita(detalleBano);
        atencionBano.setFechaHora(LocalDateTime.now().withNano(0));
        atencionBano.agregarInsumoUtilizado(0, true, shampoo, 1, false);
        atencionBL.insert(atencionBano);
        bloque("INSERT (bano)");
        campo("id", atencionBano.getId());
        campo("insumo", "Shampoo 500 ml x1 (no cobrable)");
        campo("stock shampoo", "5 -> " + articuloBL.findById(shampoo.getId()).getStockActual());

        try {
            Atencion repetida = new Atencion();
            repetida.setDetalleCita(detalleConsulta);
            repetida.setFechaHora(LocalDateTime.now().withNano(0));
            repetida.setMotivoConsulta("Repetida");
            atencionBL.insert(repetida);
            noRechazada("Segunda atencion del servicio 'Consulta'");
        } catch (BLException ex) {
            rechazada("Segunda atencion del servicio 'Consulta'", ex);
        }
        mostrarReglas();

        // ------------------------------------------------------------ RECETA
        tituloSeccion("RECETA");

        Receta receta = new Receta();
        receta.setAtencion(atencionBL.findById(atencionConsulta.getId()));
        receta.setFechaEmision(LocalDate.now());
        receta.setIndicacionesGenerales("Limpiar el oido cada dia");
        receta.agregarDetalle(0, true, vacuna, "1 dosis", "cada 24 h", 7, 1);
        recetaBL.insert(receta);
        bloque("INSERT");
        campo("id", receta.getId());
        campo("medicamento", "Vacuna antirrabica | 1 dosis | cada 24 h | 7 dias");

        try {
            Receta paraBano = new Receta();
            paraBano.setAtencion(atencionBL.findById(atencionBano.getId()));
            paraBano.setFechaEmision(LocalDate.now());
            paraBano.setIndicacionesGenerales("no corresponde");
            paraBano.agregarDetalle(0, true, vacuna, "1", "1", 1, 1);
            recetaBL.insert(paraBano);
            noRechazada("Receta en el servicio estetico 'Bano y corte'");
        } catch (BLException ex) {
            rechazada("Receta en el servicio estetico 'Bano y corte'", ex);
        }
        mostrarReglas();

        // ------------------------------------------------------------ CITA COMPLETADA
        tituloSeccion("CITA COMPLETADA");

        Cita completada = citaBL.findById(cita.getId());
        completada.setEstado(EstadoCita.COMPLETADO);
        citaBL.update(completada);
        bloque("UPDATE");
        campo("estado", "APROBADO -> " + citaBL.findById(cita.getId()).getEstado());

        // ------------------------------------------------------------ BOLETA
        tituloSeccion("BOLETA");

        Atencion atencionGuardada = atencionBL.findById(atencionConsulta.getId());
        Boleta boleta = new Boleta();
        boleta.setCliente(cliente);
        boleta.setFecha(LocalDateTime.now().withNano(0));
        boleta.setMetodoPago(MetodoPago.YAPE);
        boleta.setSerieCorrelativo(nuevaSerie(0));
        boleta.agregarDetalleServicio(0, true, 1, 80, atencionGuardada.getDetalleCita());
        boleta.agregarDetalleServicio(0, true, 1, 40, detalleBano);
        boleta.agregarDetalleArticulo(0, true, 1, 35, vacuna, atencionGuardada.getInsumosUtilizados().get(0));
        boleta.agregarDetalleArticulo(0, true, 1, 25, shampoo, null);
        boletaBL.insert(boleta);

        Boleta guardada = boletaBL.findById(boleta.getId());
        bloque("INSERT");
        campo("serie", guardada.getSerieCorrelativo());
        campo("cliente", cliente.getNombres() + " " + cliente.getApellidoPaterno());
        campo("metodo de pago", guardada.getMetodoPago());
        for (DetalleBoleta detalle : guardada.getDetalles()) {
            campo("linea", String.format("%-34s S/ %7.2f", describirLinea(detalle), detalle.getSubtotal()));
        }
        campo("total", String.format("S/ %.2f", guardada.getTotal()));
        int stockShampoo = articuloBL.findById(shampoo.getId()).getStockActual();
        campo("stock shampoo", "4 -> " + stockShampoo + " (venta de caja)");
        verificar("el total es la suma de las lineas (180.00)", Math.abs(guardada.getTotal() - 180) < 0.01);
        verificar("stock shampoo = 3", stockShampoo == 3);

        try {
            Boleta otra = nuevaBoleta(cliente, nuevaSerie(1));
            otra.agregarDetalleServicio(0, true, 1, 80, detalleConsulta);
            boletaBL.insert(otra);
            noRechazada("Facturar de nuevo el servicio 'Consulta'");
        } catch (BLException ex) {
            rechazada("Facturar de nuevo el servicio 'Consulta'", ex);
        }
        try {
            Boleta otra = nuevaBoleta(cliente, nuevaSerie(2));
            otra.agregarDetalleArticulo(0, true, 99, 25, shampoo, null);
            boletaBL.insert(otra);
            noRechazada("Vender 99 unidades de Shampoo (stock 3)");
        } catch (BLException ex) {
            rechazada("Vender 99 unidades de Shampoo (stock 3)", ex);
        }
        mostrarReglas();

        // ------------------------------------------------------------ BAJAS
        tituloSeccion("BAJAS (de hijos a padres)");

        try {
            mascotaBL.delete(mascota.getId());
            noRechazada("Eliminar la mascota 'Luna' (tiene citas)");
        } catch (BLException ex) {
            rechazada("Eliminar la mascota 'Luna' (tiene citas)", ex);
        }
        try {
            servicioBL.delete(consulta.getId());
            noRechazada("Eliminar el servicio 'Consulta' (esta en una cita)");
        } catch (BLException ex) {
            rechazada("Eliminar el servicio 'Consulta' (esta en una cita)", ex);
        }
        try {
            citaBL.delete(cita.getId());
            noRechazada("Eliminar la cita (tiene atenciones)");
        } catch (BLException ex) {
            rechazada("Eliminar la cita (tiene atenciones)", ex);
        }
        mostrarReglas("REGLAS DE NEGOCIO (no eliminar con dependientes)");
        System.out.println();

        boletaBL.delete(boleta.getId());
        baja("boleta", boleta.getId(), boletaBL.findById(boleta.getId()) == null);
        recetaBL.delete(receta.getId());
        baja("receta", receta.getId(), recetaBL.findById(receta.getId()) == null);
        atencionBL.delete(atencionConsulta.getId());
        atencionBL.delete(atencionBano.getId());
        baja("atencion", atencionConsulta.getId(), atencionBL.findById(atencionConsulta.getId()) == null);
        citaBL.delete(cita.getId());
        baja("cita", cita.getId(), citaBL.findById(cita.getId()) == null);
        mascotaBL.delete(mascota.getId());
        baja("mascota", mascota.getId(), mascotaBL.findById(mascota.getId()) == null);
        clienteBL.delete(cliente.getId());
        baja("cliente", cliente.getId(), clienteBL.findById(cliente.getId()) == null);
        veterinarioBL.delete(veterinario.getId());
        baja("veterinario", veterinario.getId(), veterinarioBL.findById(veterinario.getId()) == null);
        servicioBL.delete(consulta.getId());
        servicioBL.delete(bano.getId());
        baja("servicio", consulta.getId(), servicioBL.findById(consulta.getId()) == null);
        diagnosticoBL.delete(diagnostico.getId());
        baja("diagnostico", diagnostico.getId(), diagnosticoBL.findById(diagnostico.getId()) == null);
        articuloBL.delete(vacuna.getId());
        articuloBL.delete(shampoo.getId());
        baja("articulo", vacuna.getId(), articuloBL.findById(vacuna.getId()) == null);
        categoriaBL.delete(categoria.getId());
        baja("categoria", categoria.getId(), categoriaBL.findById(categoria.getId()) == null);
        for (Horario horario : horarios) {
            horarioBL.delete(horario.getId());
        }
        cuentaBL.delete(cuentaCliente.getId());
        cuentaBL.delete(cuentaVet.getId());
        baja("cuenta", cuentaCliente.getId(), cuentaBL.findById(cuentaCliente.getId()) == null);
    }

    // ---------------------------------------------------------------- Impresion

    private static void tituloSeccion(String nombre) {
        System.out.println();
        System.out.println();
        System.out.println("============================================================");
        System.out.println(" " + nombre);
        System.out.println("============================================================");
        System.out.println();
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

    private static void verificarEliminado(int id, boolean quedoNull) {
        if (quedoNull) {
            System.out.println("  buscar id " + id + " -> no encontrada (null). Se elimino correctamente.");
        } else {
            fallas++;
            System.out.println("  buscar id " + id + " -> SIGUE EXISTIENDO (no se elimino bien)");
        }
    }

    private static void baja(String nombre, int id, boolean quedoNull) {
        if (!quedoNull) {
            fallas++;
        }
        System.out.println("  " + nombre + " (id " + id + ") -> " + (quedoNull ? "eliminado" : "SIGUE EXISTIENDO"));
    }

    // Reglas de negocio: la BL debe rechazar el caso. La linea se guarda y se imprime al cerrar la seccion
    private static void rechazada(String caso, Exception ex) {
        reglas.add("  " + caso + " -> Rechazado: " + ex.getMessage());
    }

    private static void noRechazada(String caso) {
        fallas++;
        reglas.add("  " + caso + ": NO se rechazo (falla la regla)");
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
        return origen + articulo.getArticulo().getNombre();
    }

    // ---------------------------------------------------------------- Datos de prueba

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

    private static Cuenta nuevaCuenta(String prefijo) {
        Cuenta cuenta = new Cuenta();
        cuenta.setNombreUsuario(prefijo + SUFIJO);
        cuenta.setCorreo(prefijo + SUFIJO + "@mail.com");
        cuenta.setPassword("Clave" + SUFIJO);
        cuenta.setFechaCreacion(LocalDate.now());
        return cuenta;
    }

    // El digito distingue a cada persona: asi el DNI y el telefono son unicos en cada ejecucion
    private static void completarPersona(Persona persona, Cuenta cuenta, String digito) {
        String numero = String.format("%07d", Long.parseLong(SUFIJO) % 10_000_000);
        persona.setCuenta(cuenta);
        persona.setDni(digito + numero);
        persona.setTelefono("9" + digito + numero);
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

    private static Cita citaNueva(Mascota mascota, LocalDateTime fecha, Servicio servicio, Veterinario veterinario) {
        Cita cita = new Cita();
        cita.setFechaHora(fecha);
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setMascota(mascota);
        cita.agregarDetalle(0, true, null, servicio, veterinario);
        return cita;
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
        boleta.setFecha(LocalDateTime.now().withNano(0));
        boleta.setMetodoPago(MetodoPago.EFECTIVO);
        boleta.setSerieCorrelativo(serie);
        return boleta;
    }

    private static String nuevaSerie(int desplazamiento) {
        return String.format("B001-%06d", (Long.parseLong(SUFIJO) + desplazamiento) % 1_000_000);
    }
}
