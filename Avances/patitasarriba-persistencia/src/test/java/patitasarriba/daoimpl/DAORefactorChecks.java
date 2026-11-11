package patitasarriba.daoimpl;

import conexion.DBManager;
import patitasarriba.dao.DAO;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.daoimpl.atencion.DiagnosticoDAOImpl;
import patitasarriba.daoimpl.horario.HorarioDAOImpl;
import patitasarriba.daoimpl.mascota.MascotaDAOImpl;
import patitasarriba.daoimpl.producto.CategoriaDAOImpl;
import patitasarriba.daoimpl.producto.ServicioDAOImpl;
import patitasarriba.daoimpl.usuario.ClienteDAOImpl;
import patitasarriba.daoimpl.usuario.CuentaDAOImpl;
import patitasarriba.modelo.Registro;
import patitasarriba.modelo.atencion.*;
import patitasarriba.modelo.cita.*;
import patitasarriba.modelo.boleta.*;
import patitasarriba.modelo.receta.*;
import patitasarriba.daoimpl.atencion.AtencionDAOImpl;
import patitasarriba.daoimpl.cita.CitaDAOImpl;
import patitasarriba.daoimpl.boleta.BoletaDAOImpl;
import patitasarriba.daoimpl.receta.RecetaDAOImpl;
import patitasarriba.daoimpl.producto.ArticuloDAOImpl;
import patitasarriba.daoimpl.usuario.AdministradorDAOImpl;
import patitasarriba.daoimpl.usuario.VeterinarioDAOImpl;
import patitasarriba.modelo.horario.*;
import patitasarriba.modelo.mascota.*;
import patitasarriba.modelo.producto.*;
import patitasarriba.modelo.usuario.*;

import java.lang.reflect.*;
import java.sql.*;
import java.time.*;
import java.util.*;

/** Pruebas JDBC sin base de datos real ni dependencias externas. Ejecutar main tras test-compile. */
public final class DAORefactorChecks {
    private static final FakeDatabase db = new FakeDatabase();
    private static int checks;

    public static void main(String[] args) throws Exception {
        Field instance = DBManager.class.getDeclaredField("instance");
        instance.setAccessible(true);
        Object original = instance.get(null);
        instance.set(null, db);
        try {
            crudContracts();
            mappingAndNotFound();
            sharedProductMapping();
            parameterFailureClosesStatement();
            sharedConnectionCommit();
            sharedConnectionFailureRollback();
            explicitConnectionWithoutId();
            allMasterDaosReadEmptyResults();
            compoundWritesKeepDetails();
            compoundFailureRollsBackOwnedTransaction();
            System.out.println("DAO checks passed: " + checks);
        } finally {
            if (TransactionsManager.activa()) TransactionsManager.rollback();
            instance.set(null, original);
        }
    }

    private static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    private static void crudContracts() throws Exception {
        Diagnostico diagnostico = new Diagnostico(0, true, "Alergia", "Descripcion");
        Categoria categoria = new Categoria();
        categoria.setNombre("Medicamentos"); categoria.setDescripcion("Descripcion");
        Servicio servicio = new Servicio();
        servicio.setNombre("Consulta"); servicio.setDescripcion("Descripcion");
        servicio.setPrecioBase(30); servicio.setTipo(TipoServicio.CONSULTA_MEDICA);
        Horario horario = new Horario();
        horario.setDiaSemana(DiaSemana.LUNES); horario.setHoraInicio(LocalTime.of(8, 0)); horario.setHoraFin(LocalTime.of(10, 0));
        Cuenta cuenta = new Cuenta();
        cuenta.setNombreUsuario("ana"); cuenta.setPassword("test-password");
        cuenta.setCorreo("ana@example.com"); cuenta.setFechaCreacion(LocalDate.of(2026, 1, 1));
        Cliente cliente = new Cliente();
        cliente.setCuenta(cuenta); cliente.setDni("12345678"); cliente.setNombres("Ana");
        cliente.setApellidoPaterno("Paz"); cliente.setApellidoMaterno("Ruiz"); cliente.setTelefono("999999999");
        Mascota mascota = new Mascota();
        mascota.setCliente(cliente); mascota.setNombre("Michi"); mascota.setSexo(SexoMascota.HEMBRA);
        mascota.setPeso(4); mascota.setFechaNacimiento(LocalDate.of(2020, 2, 3));
        mascota.setTipoMascota(TipoMascota.GATO); mascota.setFoto("foto.png");

        exercise(new DiagnosticoDAOImpl(), diagnostico, "insertar_diagnostico");
        exercise(new CategoriaDAOImpl(), categoria, "insertar_categoria");
        exercise(new ServicioDAOImpl(), servicio, "insertar_servicio");
        exercise(new HorarioDAOImpl(), horario, "insertar_horario");
        exercise(new CuentaDAOImpl(), cuenta, "insertar_cuenta");
        exercise(new ClienteDAOImpl(), cliente, "insertar_cliente");
        exercise(new MascotaDAOImpl(), mascota, "insertar_mascota");
        check(mascota.getId() == 42 && cuenta.getId() == 42, "Los IDs generados deben asignarse al modelo");
        checks++;
    }

    private static <T> void exercise(DAO<T, Integer> dao, T model, String insertName) throws Exception {
        int start = db.connections.size();
        dao.insert(model);
        FakeConnection inserted = db.connections.get(start);
        FakeStatement insert = inserted.commands.getFirst();
        check(insert.sql.contains(insertName + "("), "Procedimiento de insercion incorrecto");
        check(insert.parameters.containsKey("p_activo"), "Debe conservarse el estado activo");
        check(insert.outParameters.contains("p_id"), "Debe registrarse el parametro OUT p_id");
        if (model instanceof Mascota) check("foto.png".equals(insert.parameters.get("p_foto")), "Debe conservarse la foto");
        dao.update(model); dao.delete(42);
        check(dao.findById(999) == null, "Un ID inexistente debe devolver null");
        check(dao.findAll().isEmpty(), "Una consulta vacia debe devolver una lista vacia");
        for (FakeConnection conn : db.connections.subList(start, db.connections.size())) {
            check(conn.closed, "Debe cerrarse cada conexion propia");
            for (FakeStatement cmd : conn.commands) {
                check(cmd.executed, "El comando debe ejecutarse, no solo prepararse");
                check(cmd.closed, "Debe cerrarse el comando");
                if (cmd.result != null) check(cmd.result.closed, "Debe cerrarse el ResultSet");
            }
        }
    }

    private static void mappingAndNotFound() throws Exception {
        db.row = Map.of("activo", false, "id_diagnostico", 7, "nombre_enfermedad", "Alergia", "descripcion", "Control");
        DiagnosticoDAOImpl dao = new DiagnosticoDAOImpl();
        Diagnostico d = dao.findById(7);
        check(d.getId() == 7 && !d.isActivo() && d.getNombreEnfermedad().equals("Alergia"), "Mapeo completo, incluido ACTIVO");
        check(dao.findAll().size() == 1, "El listado debe usar mapear");
        check(dao.findByNombreEnfermedad("Alergia").getId() == 7, "Debe conservarse la consulta especifica");
        db.row = Map.of();
        check(dao.findById(99) == null, "No debe mapearse un ResultSet sin fila");
        checks++;
    }

    private static void sharedProductMapping() throws Exception {
        db.row = Map.of("activo", true, "id_servicio", 7, "id_articulo", 8,
            "nombre", "Producto de prueba", "descripcion", "Descripcion", "precio_base", 30d,
            "tipo_servicio", "CONSULTA_MEDICA", "stock_actual", 10, "stock_minimo", 2);
        db.queryRows.put("{call listar_categorias_por_articulo(?)}", Map.of());
        try {
            Servicio service = new ServicioDAOImpl().findById(7);
            check(service.getNombre().equals("Producto de prueba") && service.getPrecioBase() == 30, "Campos comunes del servicio");
            check(service.getDuracionEstimada() == null && service.getTipo() == TipoServicio.CONSULTA_MEDICA, "Duracion opcional y tipo especifico");
            Articulo article = new ArticuloDAOImpl().findById(8);
            check(article.getNombre().equals(service.getNombre()) && article.getPrecioBase() == 30, "Campos comunes del articulo");
            check(article.getStockActual() == 10 && article.getStockMinimo() == 2 && article.getCategorias().isEmpty(), "Campos especificos del articulo");
        } finally {
            db.row = Map.of(); db.queryRows.clear();
        }
        checks++;
    }

    private static void parameterFailureClosesStatement() throws Exception {
        db.failBind = true;
        db.failClose = true;
        try {
            new DiagnosticoDAOImpl().findById(1);
            throw new AssertionError("Debe propagarse SQLException");
        } catch (SQLException expected) {
            check(expected.getMessage().equals("bind failure"), "Debe conservarse la excepcion original");
            check(expected.getSuppressed().length == 1, "Conservar el error secundario de cierre sin ocultar el original");
        }
        FakeConnection conn = db.connections.getLast();
        check(conn.closed && conn.commands.getFirst().closed, "Cerrar recursos aunque falle la preparacion");
        checks++;
    }

    private static void sharedConnectionCommit() throws Exception {
        int before = db.connections.size();
        TransactionsManager.iniciar();
        FakeConnection shared = db.connections.getLast();
        DiagnosticoDAOImpl dao = new DiagnosticoDAOImpl();
        dao.findAll(); dao.findByNombreEnfermedad("Alergia");
        dao.insert(new Diagnostico(0, true, "Alergia", "Control"));
        new CategoriaDAOImpl().findByArticuloId(3);
        check(db.connections.size() == before + 1, "Todos los DAO deben usar la misma conexion transaccional");
        check(!shared.closed && shared.commits == 0, "El DAO no cierra ni confirma una conexion prestada");
        TransactionsManager.commit();
        check(shared.closed && shared.commits == 1 && !TransactionsManager.activa(), "Commit y liberacion por el propietario");
        checks++;
    }

    private static void sharedConnectionFailureRollback() throws Exception {
        TransactionsManager.iniciar();
        FakeConnection shared = db.connections.getLast();
        db.failExecute = true;
        try {
            new DiagnosticoDAOImpl().delete(1);
            throw new AssertionError("Debe propagarse el fallo de SQL");
        } catch (SQLException expected) {
            check(!shared.closed, "El fallo del DAO no debe cerrar la transaccion ajena");
            TransactionsManager.rollback();
        }
        check(shared.rollbacks == 1 && shared.closed, "El llamador puede revertir y cerrar la transaccion");
        checks++;
    }

    private static void explicitConnectionWithoutId() throws Exception {
        FakeConnection external = new FakeConnection();
        NoIdDAO dao = new NoIdDAO();
        NoId model = new NoId();
        dao.crear(model, external.proxy); dao.actualizar(model, external.proxy); dao.eliminar(3, external.proxy);
        check(model.processed, "Una entidad sin id puede procesar su insercion");
        check(!external.closed && external.commits == 0 && external.rollbacks == 0, "No administrar la conexion explicita");
        check(external.commands.stream().allMatch(c -> c.closed && c.executed), "Cerrar solo los comandos prestados");
        checks++;
    }

    private static void allMasterDaosReadEmptyResults() throws Exception {
        List<DAO<?, Integer>> daos = List.of(new DiagnosticoDAOImpl(), new CategoriaDAOImpl(),
            new ServicioDAOImpl(), new HorarioDAOImpl(), new MascotaDAOImpl(), new CuentaDAOImpl(),
            new ClienteDAOImpl(), new ArticuloDAOImpl(), new AtencionDAOImpl(), new BoletaDAOImpl(),
            new CitaDAOImpl(), new RecetaDAOImpl(), new AdministradorDAOImpl(), new VeterinarioDAOImpl());
        for (DAO<?, Integer> dao : daos) {
            int start = db.connections.size();
            check(dao.findAll().isEmpty(), "Listado vacio: " + dao.getClass().getSimpleName());
            check(dao.findById(999) == null, "ID inexistente: " + dao.getClass().getSimpleName());
            check(db.connections.subList(start, db.connections.size()).stream().allMatch(c -> c.closed), "Lecturas sin conexiones abiertas");
        }
        checks++;
    }

    private static Cuenta cuenta() {
        Cuenta c = new Cuenta();
        c.setId(5); c.setCorreo("ana@example.com"); c.setNombreUsuario("ana");
        c.setPassword("test-password"); c.setFechaCreacion(LocalDate.of(2026, 1, 1));
        return c;
    }
    private static <T extends Persona> T persona(T p) {
        p.setId(5); p.setCuenta(cuenta()); p.setDni("12345678"); p.setNombres("Ana");
        p.setApellidoPaterno("Paz"); p.setApellidoMaterno("Ruiz"); p.setTelefono("999999999");
        return p;
    }
    private static Horario horario() {
        Horario h = new Horario(); h.setId(5); h.setDiaSemana(DiaSemana.LUNES);
        h.setHoraInicio(LocalTime.of(8, 0)); h.setHoraFin(LocalTime.of(10, 0)); return h;
    }
    private static Categoria categoria() {
        Categoria c = new Categoria(); c.setId(5); c.setNombre("Medicamentos"); c.setDescripcion("Descripcion"); return c;
    }
    private static Articulo articulo() {
        Articulo a = new Articulo() {
            @Override public List<Categoria> getCategorias() { return List.of(categoria()); }
        };
        a.setId(5); a.setNombre("Medicina"); a.setDescripcion("Descripcion"); a.setPrecioBase(20);
        a.setStockActual(10); a.setStockMinimo(1); return a;
    }
    private static void compoundWritesKeepDetails() throws Exception {
        Administrador admin = persona(new Administrador()); admin.setHorarios(List.of(horario()));
        assertCompound(() -> new AdministradorDAOImpl().insert(admin), false,
            "insertar_administrador", "insertar_horario_administrador");
        Veterinario vet = persona(new Veterinario()); vet.setNumeroColegiatura("CM123"); vet.setHorarios(List.of(horario()));
        assertCompound(() -> new VeterinarioDAOImpl().insert(vet), false,
            "insertar_veterinario", "insertar_horario_veterinario");
        assertCompound(() -> new ArticuloDAOImpl().insert(articulo()), false,
            "insertar_articulo", "eliminar_categorias_por_articulo", "insertar_articulo_categoria");

        Servicio service = new Servicio(); service.setId(5); service.setNombre("Consulta");
        service.setDescripcion("Descripcion"); service.setPrecioBase(30); service.setTipo(TipoServicio.CONSULTA_MEDICA);
        DetalleCita detail = new DetalleCita() {
            @Override public Servicio getServicio() { return service; }
            @Override public Veterinario getVeterinario() { return vet; }
        };
        Cita appointment = new Cita() {
            @Override public Mascota getMascota() { Mascota m = new Mascota(); m.setId(5); return m; }
            @Override public List<DetalleCita> getDetalles() { return List.of(detail); }
        };
        appointment.setFechaHora(LocalDateTime.of(2026, 11, 20, 10, 0)); appointment.setEstado(EstadoCita.PENDIENTE);
        assertCompound(() -> new CitaDAOImpl().insert(appointment), true, "insertar_cita", "insertar_detalle_cita");

        AtencionDiagnostico diagnosis = new AtencionDiagnostico() {
            @Override public Diagnostico getDiagnostico() { return new Diagnostico(5, true, "Alergia", "Descripcion"); }
        };
        diagnosis.setNivelGravedad(NivelGravedad.LEVE);
        InsumoUtilizado supply = new InsumoUtilizado() {
            @Override public Articulo getArticulo() { return articulo(); }
        };
        supply.setCantidadUtilizada(1); supply.setEsCobrable(true);
        Atencion care = new Atencion() {
            @Override public DetalleCita getDetalleCita() { return detail; }
            @Override public List<InsumoUtilizado> getInsumosUtilizados() { return List.of(supply); }
            @Override public List<AtencionDiagnostico> getDiagnosticos() { return List.of(diagnosis); }
        };
        care.setFechaHora(LocalDateTime.of(2026, 11, 20, 10, 0)); care.setMotivoConsulta("Control");
        assertCompound(() -> new AtencionDAOImpl().insert(care), true,
            "insertar_atencion", "insertar_insumo_utilizado", "insertar_atencion_diagnostico");

        DetalleReceta medication = new DetalleReceta() {
            @Override public Articulo getArticulo() { return articulo(); }
        };
        medication.setDosis("1 ml"); medication.setFrecuencia("Diaria"); medication.setDuracionDias(3); medication.setCantidadTotal(1);
        Receta prescription = new Receta() {
            @Override public Atencion getAtencion() { return care; }
            @Override public List<DetalleReceta> getDetalles() { return List.of(medication); }
        };
        prescription.setFechaEmision(LocalDate.of(2026, 11, 20)); prescription.setIndicacionesGenerales("Reposo");
        assertCompound(() -> new RecetaDAOImpl().insert(prescription), true, "insertar_receta", "insertar_detalle_receta");

        DetalleBoletaArticulo line = new DetalleBoletaArticulo() {
            @Override public Articulo getArticulo() { return articulo(); }
        };
        line.setCantidad(1); line.setPrecioUnitario(20);
        Boleta receipt = new Boleta() {
            @Override public Cliente getCliente() { return persona(new Cliente()); }
            @Override public List<DetalleBoleta> getDetalles() { return List.of(line); }
        };
        receipt.setFecha(LocalDateTime.of(2026, 11, 20, 10, 0)); receipt.setMetodoPago(MetodoPago.EFECTIVO);
        receipt.setSerieCorrelativo("B001-000001"); receipt.setTotal(20);
        assertCompound(() -> new BoletaDAOImpl().insert(receipt), true, "insertar_boleta", "insertar_detalle_boleta_art");
        checks++;
    }
    private interface SqlAction { void run() throws SQLException; }
    private static void assertCompound(SqlAction action, boolean callerOwns, String... expectedSql) throws Exception {
        int before = db.connections.size();
        if (callerOwns) TransactionsManager.iniciar();
        action.run();
        check(db.connections.size() == before + 1, "Cabecera y detalles usan una sola conexion");
        FakeConnection c = db.connections.getLast();
        int position = 0;
        for (FakeStatement statement : c.commands) {
            check(statement.executed && statement.closed, "Cada comando compuesto se ejecuta y cierra");
            if (position < expectedSql.length && statement.sql.contains(expectedSql[position] + "(")) position++;
            // Some original procedures contain whitespace before '('.
            else if (position < expectedSql.length && statement.sql.contains(expectedSql[position] + " (")) position++;
        }
        check(position == expectedSql.length, "Faltan pasos de la operacion compuesta: " + Arrays.toString(expectedSql));
        if (callerOwns) {
            check(!c.closed && c.commits == 0, "El DAO no confirma una transaccion ajena");
            TransactionsManager.rollback();
        } else check(c.closed && c.commits == 1 && !TransactionsManager.activa(), "El DAO confirma su transaccion propia");
    }
    private static void compoundFailureRollsBackOwnedTransaction() throws Exception {
        Administrador admin = persona(new Administrador()); admin.setHorarios(List.of(horario()));
        db.failSql = "insertar_horario_administrador";
        try {
            new AdministradorDAOImpl().insert(admin);
            throw new AssertionError("Debe fallar el guardado de horarios");
        } catch (SQLException expected) {
            FakeConnection c = db.connections.getLast();
            check(c.rollbacks == 1 && c.commits == 0 && c.closed && !TransactionsManager.activa(), "Revertir al fallar un detalle");
        }
        checks++;
    }

    private static class NoId extends Registro { boolean processed; }
    private static class NoIdDAO extends TransaccionDAOImpl<NoId> {
        protected CallableStatement comandoInsertar(Connection c, NoId m) throws SQLException { return c.prepareCall("{call relacion()}"); }
        protected CallableStatement comandoActualizar(Connection c, NoId m) throws SQLException { return c.prepareCall("{call relacion()}"); }
        protected CallableStatement comandoEliminar(Connection c, Integer id) throws SQLException { return c.prepareCall("{call relacion()}"); }
        protected CallableStatement comandoBuscar(Connection c, Integer id) throws SQLException { return c.prepareCall("{call relacion()}"); }
        protected CallableStatement comandoListar(Connection c) throws SQLException { return c.prepareCall("{call relacion()}"); }
        protected NoId mapear(ResultSet r) { return new NoId(); }
        protected void procesarResultadoInsertar(CallableStatement c, NoId m) { m.processed = true; }
    }

    private static class FakeDatabase extends DBManager {
        final List<FakeConnection> connections = new ArrayList<>();
        Map<String, Object> row = Map.of();
        boolean failBind, failExecute, failClose;
        final Map<String, Map<String, Object>> queryRows = new HashMap<>();
        String failSql;
        @Override public Connection getConnection() {
            FakeConnection c = new FakeConnection(); connections.add(c); return c.proxy;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T proxy(Class<T> type, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler);
    }
    private static Object defaultValue(Class<?> type) {
        if (!type.isPrimitive() || type == void.class) return null;
        if (type == boolean.class) return false;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == double.class) return 0d;
        if (type == float.class) return 0f;
        if (type == short.class) return (short) 0;
        if (type == byte.class) return (byte) 0;
        return '\0';
    }
    private static class FakeConnection implements InvocationHandler {
        final Connection proxy = proxy(Connection.class, this);
        final List<FakeStatement> commands = new ArrayList<>();
        boolean closed; int commits, rollbacks;
        public Object invoke(Object obj, Method method, Object[] args) {
            return switch (method.getName()) {
                case "prepareCall" -> { FakeStatement c = new FakeStatement((String) args[0]); commands.add(c); yield c.proxy; }
                case "close" -> { closed = true; yield null; }
                case "isClosed" -> closed;
                case "commit" -> { commits++; yield null; }
                case "rollback" -> { rollbacks++; yield null; }
                default -> defaultValue(method.getReturnType());
            };
        }
    }
    private static class FakeStatement implements InvocationHandler {
        final CallableStatement proxy = proxy(CallableStatement.class, this);
        final String sql;
        final Map<Object, Object> parameters = new HashMap<>();
        final Set<Object> outParameters = new HashSet<>();
        boolean executed, closed;
        FakeResult result;
        FakeStatement(String sql) { this.sql = sql; }
        public Object invoke(Object obj, Method method, Object[] args) throws SQLException {
            String name = method.getName();
            if (name.startsWith("set") && args != null && args.length >= 2) {
                if (db.failBind) { db.failBind = false; throw new SQLException("bind failure"); }
                parameters.put(args[0], args[1]); return null;
            }
            return switch (name) {
                case "registerOutParameter" -> { outParameters.add(args[0]); yield null; }
                case "execute", "executeUpdate", "executeQuery" -> {
                    executed = true;
                    if (db.failSql != null && sql.contains(db.failSql)) {
                        db.failSql = null; throw new SQLException("detail failure");
                    }
                    if (db.failExecute) { db.failExecute = false; throw new SQLException("execute failure"); }
                    if (name.equals("executeQuery")) { result = new FakeResult(db.queryRows.getOrDefault(sql, db.row)); yield result.proxy; }
                    yield name.equals("execute") ? false : 1;
                }
                case "getInt" -> 42;
                case "close" -> {
                    closed = true;
                    if (db.failClose) { db.failClose = false; throw new SQLException("close failure"); }
                    yield null;
                }
                default -> defaultValue(method.getReturnType());
            };
        }
    }
    private static class FakeResult implements InvocationHandler {
        final ResultSet proxy = proxy(ResultSet.class, this);
        final Map<String, Object> row;
        boolean visited, closed, lastNull;
        FakeResult(Map<String, Object> row) { this.row = row; }
        public Object invoke(Object obj, Method method, Object[] args) {
            return switch (method.getName()) {
                case "next" -> { boolean next = !visited && !row.isEmpty(); visited = true; yield next; }
                case "getInt", "getString", "getBoolean", "getDouble" -> {
                    Object value = row.get(((String) args[0]).toLowerCase(Locale.ROOT));
                    lastNull = value == null;
                    yield value == null ? defaultValue(method.getReturnType()) : value;
                }
                case "wasNull" -> lastNull;
                case "close" -> { closed = true; yield null; }
                default -> defaultValue(method.getReturnType());
            };
        }
    }
}
