package ejecucion;

import patitasarriba.bl.ArticuloBL;
import patitasarriba.bl.BLException;
import patitasarriba.bl.CategoriaArticuloBL;
import patitasarriba.bl.CitaBL;
import patitasarriba.bl.ClienteBL;
import patitasarriba.bl.CuentaBL;
import patitasarriba.bl.MascotaBL;
import patitasarriba.bl.VeterinarioBL;
import patitasarriba.bl.impl.cita.CitaBLImpl;
import patitasarriba.bl.impl.mascota.MascotaBLImpl;
import patitasarriba.bl.impl.producto.ArticuloBLImpl;
import patitasarriba.bl.impl.producto.CategoriaArticuloBLImpl;
import patitasarriba.bl.impl.usuario.ClienteBLImpl;
import patitasarriba.bl.impl.usuario.CuentaBLImpl;
import patitasarriba.bl.impl.usuario.VeterinarioBLImpl;
import patitasarriba.modelo.cita.Cita;
import patitasarriba.modelo.cita.EstadoCita;
import patitasarriba.modelo.mascota.Mascota;
import patitasarriba.modelo.mascota.SexoMascota;
import patitasarriba.modelo.mascota.TipoMascota;
import patitasarriba.modelo.producto.Articulo;
import patitasarriba.modelo.producto.CategoriaArticulo;
import patitasarriba.modelo.usuario.Cliente;
import patitasarriba.modelo.usuario.Cuenta;
import patitasarriba.modelo.usuario.Veterinario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Clase Tester: demuestra, para al menos 4 entidades,
// que insertar/modificar/eliminar/listar/buscarPorId funcionan de verdad
// contra la base de datos, pasando siempre por la capa de logica de negocio (BL).
public class Tester {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    //Para generar códigos para cada módulo de manera aleatoria (Erasmo lab2-algoritmia vibes)
    private static final Random RANDOM = new Random();
    private static long dniSeq = 70_000_000L + (System.currentTimeMillis() % 9_000_000L);

    private static final CategoriaArticuloBL categoriaArticuloBL = new CategoriaArticuloBLImpl();
    private static final ArticuloBL articuloBL = new ArticuloBLImpl();
    private static final CuentaBL cuentaBL = new CuentaBLImpl();
    private static final ClienteBL clienteBL = new ClienteBLImpl();
    private static final VeterinarioBL veterinarioBL = new VeterinarioBLImpl();
    private static final MascotaBL mascotaBL = new MascotaBLImpl();
    private static final CitaBL citaBL = new CitaBLImpl();

    // Ejecuta todas las pruebas en orden
    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println(" TESTER - PATITAS ARRIBA");
        System.out.println("============================================================");

        try {
            probarCategoriaArticulo();
            probarArticulo();
            probarMascota();
            probarCita();
            probarReglasDeNegocio();
        } catch (BLException ex) {
            System.out.println("La prueba se detuvo por un error: " + ex.getMessage());
        }
    }

    // CRUD completo de CategoriaArticulo
    private static void probarCategoriaArticulo() throws BLException {
        tituloSeccion("CATEGORIA ARTICULO");

        // Codigos random: uno para la categoria que se prueba, otro para la de apoyo
        int codigo = nuevoCodigo();
        int codigoApoyo = nuevoCodigo();

        CategoriaArticulo categoria = new CategoriaArticulo();
        categoria.setNombre("Categoria de prueba " + codigo);
        categoria.setDescripcion("Categoria creada por el Tester");
        categoria.setActivo(true);
        categoriaArticuloBL.insert(categoria);
        bloque("INSERT");
        imprimirDetalle(categoria);

        categoria = categoriaArticuloBL.findById(categoria.getId());
        bloque("FIND", "(buscar por id, para confirmar que se guardo bien)");
        imprimirDetalle(categoria);

        String descripcionAnterior = categoria.getDescripcion();
        categoria.setDescripcion("Descripcion editada");
        categoriaArticuloBL.update(categoria);
        categoria = categoriaArticuloBL.findById(categoria.getId());
        bloque("UPDATE");
        campo("id", categoria.getId());
        campo("nombre", categoria.getNombre());
        campo("descripcion", descripcionAnterior + " -> " + categoria.getDescripcion());
        campo("activo", categoria.isActivo());

        CategoriaArticulo apoyo = new CategoriaArticulo();
        apoyo.setNombre("Categoria secundaria " + codigoApoyo);
        apoyo.setDescripcion("Solo para que el listado tenga 2 filas");
        apoyo.setActivo(true);
        categoriaArticuloBL.insert(apoyo);

        List<CategoriaArticulo> categorias = categoriaArticuloBL.findAll();
        bloque("LISTADO", "(" + categorias.size() + " categorias activas en este momento)");
        listarCategorias(categorias);

        int idEliminado = categoria.getId();
        categoriaArticuloBL.delete(idEliminado);
        bloque("DELETE");
        campo("id eliminado", idEliminado);

        bloque("VERIFICACION DELETE");
        verificarCategoriaEliminada(idEliminado);

        categoriaArticuloBL.delete(apoyo.getId());
        separador();
    }

    // CRUD completo de Articulo
    private static void probarArticulo() throws BLException {
        tituloSeccion("ARTICULO");

        // Codigos random: categoria de apoyo, articulo principal y articulo secundario
        int codigoCategoria = nuevoCodigo();
        int codigo = nuevoCodigo();
        int codigoApoyo = nuevoCodigo();

        CategoriaArticulo categoria = new CategoriaArticulo();
        categoria.setNombre("Categoria de apoyo " + codigoCategoria);
        categoria.setDescripcion("Solo para probar Articulo");
        categoria.setActivo(true);
        categoriaArticuloBL.insert(categoria);

        Articulo articulo = new Articulo();
        articulo.setNombre("Alimento premium " + codigo);
        articulo.setPrecioBase(45.90);
        articulo.setDescripcion("Alimento balanceado para perros adultos");
        articulo.setStockActual(20);
        articulo.setStockMinimo(5);
        articulo.setMarca("MarcaDemo");
        articulo.setCategoria(categoria);
        articulo.setActivo(true);
        articuloBL.insert(articulo);
        bloque("INSERT");
        imprimirDetalle(articulo);

        articulo = articuloBL.findById(articulo.getId());
        bloque("FIND", "(buscar por id, para confirmar que se guardo bien)");
        imprimirDetalle(articulo);

        double precioAnterior = articulo.getPrecioBase();
        int stockAnterior = articulo.getStockActual();
        articulo.setPrecioBase(49.90);
        articulo.setStockActual(18);
        articuloBL.update(articulo);
        articulo = articuloBL.findById(articulo.getId());
        bloque("UPDATE");
        campo("id", articulo.getId());
        campo("nombre", articulo.getNombre());
        campo("precio", String.format("S/ %.2f -> S/ %.2f", precioAnterior, articulo.getPrecioBase()));
        campo("stock", stockAnterior + " -> " + articulo.getStockActual());
        campo("marca", articulo.getMarca());
        campo("categoria", articulo.getCategoria().getNombre());
        campo("activo", articulo.isActivo());

        Articulo apoyo = new Articulo();
        apoyo.setNombre("Juguete demo " + codigoApoyo);
        apoyo.setPrecioBase(15.00);
        apoyo.setDescripcion("Pelota de goma resistente");
        apoyo.setStockActual(10);
        apoyo.setStockMinimo(2);
        apoyo.setMarca("MarcaDemo");
        apoyo.setCategoria(categoria);
        apoyo.setActivo(true);
        articuloBL.insert(apoyo);

        List<Articulo> articulos = articuloBL.findAll();
        bloque("LISTADO", "(" + articulos.size() + " articulos activos en este momento)");
        listarArticulos(articulos);

        int idEliminado = articulo.getId();
        articuloBL.delete(idEliminado);
        bloque("DELETE");
        campo("id eliminado", idEliminado);

        bloque("VERIFICACION DELETE");
        verificarArticuloEliminado(idEliminado);

        articuloBL.delete(apoyo.getId());
        categoriaArticuloBL.delete(categoria.getId());
        separador();
    }

    // CRUD completo de Mascota (crea Cuenta+Cliente de apoyo)
    private static void probarMascota() throws BLException {
        tituloSeccion("MASCOTA");

        // Codigos random: cuenta del cliente, mascota principal y mascota de apoyo
        int codigoCuenta = nuevoCodigo();
        int codigo = nuevoCodigo();
        int codigoApoyo = nuevoCodigo();

        Cuenta cuenta = new Cuenta();
        cuenta.setNombreUsuario("cliente.mascota." + codigoCuenta);
        cuenta.setPassword("demo123");
        cuenta.setCorreo("cliente.mascota." + codigoCuenta + "@softprog.pe");
        cuenta.setFechaCreacion(LocalDate.now());
        cuenta.setActivo(true);
        cuentaBL.insert(cuenta);

        Cliente cliente = new Cliente();
        cliente.setCuenta(cuenta);
        cliente.setDni(nuevoDni());
        cliente.setNombres("Ana");
        cliente.setApellidoPaterno("Prueba");
        cliente.setApellidoMaterno("Tester");
        cliente.setTelefono("999888777");
        clienteBL.insert(cliente);

        Mascota mascota = new Mascota();
        mascota.setNombre("Firulais " + codigo);
        mascota.setSexo(SexoMascota.MACHO);
        mascota.setPeso(12.5);
        mascota.setFechaNacimiento(LocalDate.of(2022, 3, 10));
        mascota.setTipoMascota(TipoMascota.PERRO);
        mascota.setRaza("Mestizo");
        mascota.setCliente(cliente);
        mascota.setActivo(true);
        mascotaBL.insert(mascota);
        bloque("INSERT");
        imprimirDetalle(mascota);

        mascota = mascotaBL.findById(mascota.getId());
        bloque("FIND", "(buscar por id, para confirmar que se guardo bien)");
        imprimirDetalle(mascota);

        double pesoAnterior = mascota.getPeso();
        String razaAnterior = mascota.getRaza();
        mascota.setCliente(cliente);
        mascota.setPeso(13.2);
        mascota.setRaza("Mestizo mediano");
        mascotaBL.update(mascota);
        mascota = mascotaBL.findById(mascota.getId());
        bloque("UPDATE");
        campo("id", mascota.getId());
        campo("nombre", mascota.getNombre());
        campo("tipo", mascota.getTipoMascota());
        campo("sexo", mascota.getSexo());
        campo("peso", pesoAnterior + " kg -> " + mascota.getPeso() + " kg");
        campo("raza", razaAnterior + " -> " + mascota.getRaza());
        campo("dueno (dni)", mascota.getCliente().getDni());
        campo("activo", mascota.isActivo());

        Mascota apoyo = new Mascota();
        apoyo.setNombre("Rocky " + codigoApoyo);
        apoyo.setSexo(SexoMascota.MACHO);
        apoyo.setPeso(8.0);
        apoyo.setFechaNacimiento(LocalDate.of(2023, 1, 5));
        apoyo.setTipoMascota(TipoMascota.PERRO);
        apoyo.setRaza("Bulldog");
        apoyo.setCliente(cliente);
        apoyo.setActivo(true);
        mascotaBL.insert(apoyo);

        List<Mascota> mascotas = mascotaBL.findAll();
        bloque("LISTADO", "(" + mascotas.size() + " mascotas activas en este momento)");
        listarMascotas(mascotas);

        int idEliminado = mascota.getId();
        mascotaBL.delete(idEliminado);
        bloque("DELETE");
        campo("id eliminado", idEliminado);

        bloque("VERIFICACION DELETE");
        verificarMascotaEliminada(idEliminado);

        mascotaBL.delete(apoyo.getId());
        clienteBL.delete(cliente.getId());
        cuentaBL.delete(cuenta.getId());
        separador();
    }

    // CRUD transaccional de Cita (crea Mascota+Veterinario de apoyo)
    private static void probarCita() throws BLException {
        tituloSeccion("CITA");

        // Codigos random: cuentas, mascotas y veterinario de esta prueba
        int codigoCuentaCliente = nuevoCodigo();
        int codigoMichi = nuevoCodigo();
        int codigoRex = nuevoCodigo();
        int codigoCuentaVet = nuevoCodigo();
        int codigoVet = nuevoCodigo();

        Cuenta cuentaCliente = new Cuenta();
        cuentaCliente.setNombreUsuario("cliente.cita." + codigoCuentaCliente);
        cuentaCliente.setPassword("demo123");
        cuentaCliente.setCorreo("cliente.cita." + codigoCuentaCliente + "@softprog.pe");
        cuentaCliente.setFechaCreacion(LocalDate.now());
        cuentaCliente.setActivo(true);
        cuentaBL.insert(cuentaCliente);

        Cliente cliente = new Cliente();
        cliente.setCuenta(cuentaCliente);
        cliente.setDni(nuevoDni());
        cliente.setNombres("Julio");
        cliente.setApellidoPaterno("Dueno");
        cliente.setApellidoMaterno("Tester");
        cliente.setTelefono("999111222");
        clienteBL.insert(cliente);

        Mascota michi = new Mascota();
        michi.setNombre("Michi " + codigoMichi);
        michi.setSexo(SexoMascota.HEMBRA);
        michi.setPeso(4.1);
        michi.setFechaNacimiento(LocalDate.of(2021, 7, 1));
        michi.setTipoMascota(TipoMascota.GATO);
        michi.setRaza("Siames");
        michi.setCliente(cliente);
        michi.setActivo(true);
        mascotaBL.insert(michi);

        Mascota rex = new Mascota();
        rex.setNombre("Rex " + codigoRex);
        rex.setSexo(SexoMascota.MACHO);
        rex.setPeso(9.5);
        rex.setFechaNacimiento(LocalDate.of(2020, 4, 15));
        rex.setTipoMascota(TipoMascota.PERRO);
        rex.setRaza("Labrador");
        rex.setCliente(cliente);
        rex.setActivo(true);
        mascotaBL.insert(rex);

        Cuenta cuentaVet = new Cuenta();
        cuentaVet.setNombreUsuario("vet.cita." + codigoCuentaVet);
        cuentaVet.setPassword("demo123");
        cuentaVet.setCorreo("vet.cita." + codigoCuentaVet + "@softprog.pe");
        cuentaVet.setFechaCreacion(LocalDate.now());
        cuentaVet.setActivo(true);
        cuentaBL.insert(cuentaVet);

        Veterinario veterinario = new Veterinario();
        veterinario.setCuenta(cuentaVet);
        veterinario.setDni(nuevoDni());
        veterinario.setNombres("Rosa");
        veterinario.setApellidoPaterno("Vet");
        veterinario.setApellidoMaterno("Tester");
        veterinario.setTelefono("999333444");
        veterinario.setNumeroColegiatura("CMVP-" + codigoVet);
        veterinarioBL.insert(veterinario);

        Cita cita = new Cita();
        cita.setFechaHora(LocalDateTime.now().plusDays(1));
        cita.setEstado(EstadoCita.AGENDADA);
        cita.setMascota(michi);
        cita.setVeterinario(veterinario);
        cita.setDetalles(new ArrayList<>());
        cita.setActivo(true);
        citaBL.insert(cita);
        bloque("INSERT");
        imprimirDetalle(cita);

        cita = citaBL.findById(cita.getId());
        bloque("FIND", "(buscar por id, para confirmar que se guardo bien)");
        imprimirDetalle(cita);

        String fechaAnterior = FORMATO_FECHA.format(cita.getFechaHora());
        EstadoCita estadoAnterior = cita.getEstado();
        cita.setEstado(EstadoCita.COMPLETADA);
        cita.setFechaHora(LocalDateTime.now().plusDays(2));
        citaBL.update(cita);
        cita = citaBL.findById(cita.getId());
        bloque("UPDATE");
        campo("id", cita.getId());
        campo("fecha", fechaAnterior + " -> " + FORMATO_FECHA.format(cita.getFechaHora()));
        campo("estado", estadoAnterior + " -> " + cita.getEstado());
        campo("mascota", cita.getMascota().getNombre());
        campo("veterinario", nombreVeterinario(cita.getVeterinario()));
        campo("activo", cita.isActivo());

        Cita citaApoyo = new Cita();
        citaApoyo.setFechaHora(LocalDateTime.now().plusDays(5));
        citaApoyo.setEstado(EstadoCita.AGENDADA);
        citaApoyo.setMascota(rex);
        citaApoyo.setVeterinario(veterinario);
        citaApoyo.setDetalles(new ArrayList<>());
        citaApoyo.setActivo(true);
        citaBL.insert(citaApoyo);

        List<Cita> citas = citaBL.findAll();
        bloque("LISTADO", "(" + citas.size() + " citas activas en este momento)");
        listarCitas(citas);

        int idEliminado = cita.getId();
        citaBL.delete(idEliminado);
        bloque("DELETE");
        campo("id eliminado", idEliminado);

        bloque("VERIFICACION DELETE");
        verificarCitaEliminada(idEliminado);

        citaBL.delete(citaApoyo.getId());
        mascotaBL.delete(michi.getId());
        mascotaBL.delete(rex.getId());
        clienteBL.delete(cliente.getId());
        cuentaBL.delete(cuentaCliente.getId());
        veterinarioBL.delete(veterinario.getId());
        cuentaBL.delete(cuentaVet.getId());
        separador();
    }

    // Casos que la BL debe rechazar. Los objetos de apoyo (categoria, cuenta)
    // NO se insertan en la BD: la validacion siempre los detiene antes de
    // llegar al DAO, asi que no hace falta que existan de verdad (igual que
    // hace el profesor con sus metodos empleadoDemo()/clienteDemo()).
    private static void probarReglasDeNegocio() {
        tituloSeccion("REGLAS DE NEGOCIO");

        // Codigos random para los datos invalidos de esta prueba
        int codigoArticulo = nuevoCodigo();
        int codigoCliente = nuevoCodigo();

        CategoriaArticulo categoria = new CategoriaArticulo();
        categoria.setNombre("Categoria regla " + codigoArticulo);
        categoria.setDescripcion("Solo para la regla de precio");
        categoria.setActivo(true);

        Articulo articulo = new Articulo();
        articulo.setNombre("Articulo con precio invalido " + codigoArticulo);
        articulo.setPrecioBase(0);
        articulo.setDescripcion("No deberia poder insertarse");
        articulo.setStockActual(1);
        articulo.setStockMinimo(0);
        articulo.setMarca("MarcaDemo");
        articulo.setCategoria(categoria);
        articulo.setActivo(true);
        try {
            articuloBL.insert(articulo);
            System.out.println("Articulo con precio 0: NO se rechazo (falla la regla)");
        } catch (BLException ex) {
            System.out.println("Articulo con precio 0 -> Rechazado: " + ex.getMessage());
        }

        Cuenta cuenta = new Cuenta();
        cuenta.setNombreUsuario("cliente.regla." + codigoCliente);
        cuenta.setPassword("demo123");
        cuenta.setCorreo("cliente.regla." + codigoCliente + "@softprog.pe");
        cuenta.setFechaCreacion(LocalDate.now());
        cuenta.setActivo(true);

        Cliente cliente = new Cliente();
        cliente.setCuenta(cuenta);
        cliente.setDni("123");
        cliente.setNombres("Invalido");
        cliente.setApellidoPaterno("Demo");
        cliente.setApellidoMaterno("Tester");
        cliente.setTelefono("999000000");
        try {
            clienteBL.insert(cliente);
            System.out.println("Cliente con DNI invalido: NO se rechazo (falla la regla)");
        } catch (BLException ex) {
            System.out.println("Cliente con DNI invalido -> Rechazado: " + ex.getMessage());
        }

        Cita cita = new Cita();
        cita.setFechaHora(LocalDateTime.now().minusDays(1));
        cita.setEstado(EstadoCita.AGENDADA);
        try {
            citaBL.insert(cita);
            System.out.println("Cita con fecha pasada: NO se rechazo (falla la regla)");
        } catch (BLException ex) {
            System.out.println("Cita con fecha pasada -> Rechazado: " + ex.getMessage());
        }
    }

    // Verificacion de borrado: confirma con un findById que ya no existe

    private static void verificarCategoriaEliminada(int id) {
        try {
            CategoriaArticulo verificar = categoriaArticuloBL.findById(id);
            imprimirResultadoVerificacion(id, verificar == null);
        } catch (BLException ex) {
            imprimirResultadoVerificacionConExcepcion(id, ex);
        }
    }

    private static void verificarArticuloEliminado(int id) {
        try {
            Articulo verificar = articuloBL.findById(id);
            imprimirResultadoVerificacion(id, verificar == null);
        } catch (BLException ex) {
            imprimirResultadoVerificacionConExcepcion(id, ex);
        }
    }

    private static void verificarMascotaEliminada(int id) {
        try {
            Mascota verificar = mascotaBL.findById(id);
            imprimirResultadoVerificacion(id, verificar == null);
        } catch (BLException ex) {
            imprimirResultadoVerificacionConExcepcion(id, ex);
        }
    }

    private static void verificarCitaEliminada(int id) {
        try {
            Cita verificar = citaBL.findById(id);
            imprimirResultadoVerificacion(id, verificar == null);
        } catch (BLException ex) {
            imprimirResultadoVerificacionConExcepcion(id, ex);
        }
    }

    private static void imprimirResultadoVerificacion(int id, boolean quedoNull) {
        if (quedoNull) {
            System.out.println("  buscar id " + id + " -> no encontrada (null). Se elimino correctamente.");
        } else {
            System.out.println("  buscar id " + id + " -> SIGUE EXISTIENDO (no se elimino bien)");
        }
    }

    private static void imprimirResultadoVerificacionConExcepcion(int id, BLException ex) {
        System.out.println("  buscar id " + id + " -> lanzo excepcion: " + ex.getMessage()
                + " (tambien confirma que ya no existe)");
    }


    // Metodo de generacion de datos

    private static String nuevoDni() {
        return Long.toString(++dniSeq);
    }

    // Genera un codigo random distinto en cada llamada, para que cada
    // entidad de prueba tenga un nombre unico (no todas el mismo sufijo)
    private static int nuevoCodigo() {
        return 100_000 + RANDOM.nextInt(900_000);
    }


    // Métodos de impresion

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

    private static void bloque(String etiqueta, String nota) {
        System.out.println();
        System.out.println("[" + etiqueta + "] " + nota);
    }

    private static void campo(String etiqueta, Object valor) {
        System.out.printf("  %-12s: %s%n", etiqueta, valor);
    }

    private static void separador() {
        System.out.println();
        System.out.println("------------------------------------------------------------");
    }

    private static void imprimirDetalle(CategoriaArticulo categoria) {
        campo("id", categoria.getId());
        campo("nombre", categoria.getNombre());
        campo("descripcion", categoria.getDescripcion());
        campo("activo", categoria.isActivo());
    }

    private static void imprimirDetalle(Articulo articulo) {
        campo("id", articulo.getId());
        campo("nombre", articulo.getNombre());
        campo("precio", String.format("S/ %.2f", articulo.getPrecioBase()));
        campo("stock", articulo.getStockActual());
        campo("marca", articulo.getMarca());
        campo("categoria", articulo.getCategoria().getNombre());
        campo("activo", articulo.isActivo());
    }

    private static void imprimirDetalle(Mascota mascota) {
        campo("id", mascota.getId());
        campo("nombre", mascota.getNombre());
        campo("tipo", mascota.getTipoMascota());
        campo("sexo", mascota.getSexo());
        campo("peso", mascota.getPeso() + " kg");
        campo("raza", mascota.getRaza());
        campo("dueno (dni)", mascota.getCliente().getDni());
        campo("activo", mascota.isActivo());
    }

    private static void imprimirDetalle(Cita cita) {
        campo("id", cita.getId());
        campo("fecha", FORMATO_FECHA.format(cita.getFechaHora()));
        campo("estado", cita.getEstado());
        campo("mascota", cita.getMascota().getNombre());
        campo("veterinario", nombreVeterinario(cita.getVeterinario()));
        campo("activo", cita.isActivo());
    }

    private static String nombreVeterinario(Veterinario veterinario) {
        return veterinario.getNombres() + " " + veterinario.getApellidoPaterno()
                + " (dni " + veterinario.getDni() + ")";
    }

    private static void listarCategorias(List<CategoriaArticulo> categorias) {
        int n = 1;
        for (CategoriaArticulo categoria : categorias) {
            System.out.printf("  %d) id=%-4d %-30s activo=%s%n",
                    n, categoria.getId(), categoria.getNombre(), categoria.isActivo());
            n++;
        }
    }

    private static void listarArticulos(List<Articulo> articulos) {
        int n = 1;
        for (Articulo articulo : articulos) {
            System.out.printf("  %d) id=%-4d %-25s S/ %6.2f  stock=%d%n",
                    n, articulo.getId(), articulo.getNombre(), articulo.getPrecioBase(), articulo.getStockActual());
            n++;
        }
    }

    private static void listarMascotas(List<Mascota> mascotas) {
        int n = 1;
        for (Mascota mascota : mascotas) {
            System.out.printf("  %d) id=%-4d %-20s %-6s peso=%.1f%n",
                    n, mascota.getId(), mascota.getNombre(), mascota.getTipoMascota(), mascota.getPeso());
            n++;
        }
    }

    private static void listarCitas(List<Cita> citas) {
        int n = 1;
        for (Cita cita : citas) {
            System.out.printf("  %d) id=%-4d %s  %-11s mascota=%s%n",
                    n, cita.getId(), FORMATO_FECHA.format(cita.getFechaHora()), cita.getEstado(), cita.getMascota().getNombre());
            n++;
        }
    }
}
