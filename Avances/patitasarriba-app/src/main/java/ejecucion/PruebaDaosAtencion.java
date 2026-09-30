package ejecucion;

import conexion.DBManager;
import patitasarriba.dao.impl.atencion.AtencionMedicaDAOImpl;
import patitasarriba.dao.impl.atencion.DiagnosticoDAOImpl;
import patitasarriba.dao.transacciones.TransactionsManager;
import patitasarriba.modelo.atencion.AtencionMedica;
import patitasarriba.modelo.atencion.AtencionTratamiento;
import patitasarriba.modelo.atencion.Diagnostico;
import patitasarriba.modelo.atencion.NivelGravedad;
import patitasarriba.modelo.atencion.Tratamiento;
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

// Prueba de extremo a extremo contra la BD real de los DAOs asignados a Leonardo:
// AtencionMedica, AtencionDiagnostico, AtencionTratamiento, InsumoUtilizado y Diagnostico.
// Sigue el patron de PruebaConexion.java: clase de ejecucion separada, no toca el
// demo en memoria de Principal.java.
//
// IMPORTANTE sobre los datos de apoyo (Cuenta, Cliente, Veterinario, Mascota, Cita,
// CategoriaArticulo, Articulo, Tratamiento): son FK obligatorias de ATENCION_MEDICA e
// INSUMO_UTILIZADO, pero NO son parte de esta tarea. En la BD compartida del equipo
// (mydb, ver db.properties) todavia no existen sus stored procedures (ni siquiera hay
// script para insertar_cuenta/insertar_cliente/insertar_veterinario en el repo, y
// insertar_tratamiento tampoco esta cargado), asi que aqui se siembran con INSERT
// directo (bypaseando esos DAOs) solo para poder montar la atencion medica de prueba.
// Lo unico que este archivo prueba de verdad, via sus DAOs y sus procedures reales,
// es la cadena AtencionMedicaDAOImpl -> AtencionTratamientoDAOImpl/AtencionDiagnosticoDAOImpl
// (package-private, orquestados internamente) -> InsumoUtilizadoDAOImpl, y DiagnosticoDAOImpl.
//
// NOTA sobre AtencionMedicaDAOImpl.findById()/.delete(): ambos reconstruyen Mascota, Cita
// y Tratamiento llamando a MascotaDAOImpl/CitaDAOImpl/TratamientoDAOImpl.findById(), cuyos
// procedures (buscar_mascota_por_id, buscar_cita_por_id, buscar_tratamiento_por_id) tampoco
// estan desplegados todavia en esta BD compartida. Por eso la limpieza de abajo NO depende
// de AtencionMedicaDAOImpl.delete() para borrar la cascada: usa los ids que ya quedaron en
// memoria tras el insert (nadie necesita volver a leerlos de la BD).
public class PruebaDaosAtencion {

    public static void main(String[] args) throws SQLException {
        long ts = System.currentTimeMillis();

        Cuenta cuentaCliente = null;
        Cuenta cuentaVet = null;
        Cliente cliente = null;
        Veterinario veterinario = null;
        Mascota mascota = null;
        Cita cita = null;
        CategoriaArticulo categoria = null;
        Articulo articulo = null;
        Tratamiento tratamiento = null;
        Diagnostico diagnostico = null;
        AtencionMedica atencionMedica = null;
        AtencionTratamiento atencionTratamiento = null;

        try {
            DiagnosticoDAOImpl diagnosticoDAO = new DiagnosticoDAOImpl();
            AtencionMedicaDAOImpl atencionMedicaDAO = new AtencionMedicaDAOImpl();

            // ---------- Datos de apoyo (fuera del alcance de la tarea, solo prerequisitos FK) ----------
            cuentaCliente = new Cuenta(0, true, "pass123", "cliente" + ts + "@test.com",
                    LocalDate.now(), "cliente" + ts);
            insertarCuenta(cuentaCliente);

            cliente = new Cliente(0, true, "Juan", "Perez", "Gomez", "987654321",
                    String.format("%08d", ts % 100000000), cuentaCliente, new ArrayList<>());
            insertarCliente(cliente);

            cuentaVet = new Cuenta(0, true, "vet123", "vet" + ts + "@test.com",
                    LocalDate.now(), "vet" + ts);
            insertarCuenta(cuentaVet);

            veterinario = new Veterinario(0, true, "Carlos", "Ramirez", "Soto", "955444333",
                    String.format("%08d", (ts + 1) % 100000000), cuentaVet, new ArrayList<>(), "CMVP-" + ts);
            insertarVeterinario(veterinario);

            mascota = new Mascota(0, true, "Firulais", SexoMascota.MACHO, 8.5,
                    LocalDate.of(2022, 4, 10), TipoMascota.PERRO, "Labrador", cliente);
            insertarMascota(mascota);

            cita = new Cita(0, true, LocalDateTime.now().plusDays(1), EstadoCita.AGENDADA,
                    mascota, veterinario, new ArrayList<>());
            insertarCita(cita);

            categoria = new CategoriaArticulo(0, true, "Farmacologia", "Medicamentos de prueba");
            insertarCategoriaArticulo(categoria);

            articulo = new Articulo(0, true, "Amoxicilina 250mg", 25.0, "Antibiotico", 40, 10,
                    "VetPharma", categoria);
            insertarArticulo(articulo);

            tratamiento = new Tratamiento(0, true, "Aplicacion de antibiotico " + ts, "Inyeccion IM");
            insertarTratamiento(tratamiento);

            diagnostico = new Diagnostico(0, true, "Otitis " + ts, "Infeccion en el oido");
            diagnosticoDAO.insert(diagnostico);

            // ---------- Lo que se esta probando de verdad ----------
            atencionMedica = new AtencionMedica(0, true, LocalDateTime.now(), "Dolor en el oido", 8.5,
                    "Revision en curso", mascota, cita, new ArrayList<>(), new ArrayList<>());

            atencionTratamiento = atencionMedica.agregarTratamiento(0, true, tratamiento);
            atencionTratamiento.agregarInsumo(0, true, articulo, 14);
            atencionMedica.agregarDiagnostico(0, true, NivelGravedad.LEVE, "Otitis en oido derecho", diagnostico);

            TransactionsManager.iniciar();
            try {
                atencionMedicaDAO.insert(atencionMedica);
                TransactionsManager.commit();
            } catch (SQLException e) {
                TransactionsManager.rollback();
                throw e;
            }

            System.out.println("Insertada AtencionMedica id=" + atencionMedica.getId());

            AtencionMedica leida = atencionMedicaDAO.findById(atencionMedica.getId());
            System.out.println("Leida de vuelta: " + leida);

            if (leida.getTratamientos().size() != 1 || leida.getDiagnosticos().size() != 1) {
                throw new AssertionError("No se recuperaron los tratamientos/diagnosticos esperados");
            }
            if (leida.getTratamientos().get(0).getInsumosUtilizados().size() != 1) {
                throw new AssertionError("No se recupero el insumo utilizado esperado");
            }

            System.out.println("OK: AtencionMedicaDAO, AtencionDiagnostico/AtencionTratamiento (internos), "
                    + "InsumoUtilizado (interno) y DiagnosticoDAO insertan y leen correctamente contra la BD real.");
        } finally {
            limpiar(atencionMedica, atencionTratamiento, diagnostico, tratamiento, cita, articulo, categoria,
                    mascota, veterinario, cuentaVet, cliente, cuentaCliente);
        }
    }

    // ---------- Siembra de datos de apoyo (INSERT directo, sin pasar por DAOs/procedures ajenos) ----------

    private static void insertarCuenta(Cuenta cuenta) throws SQLException {
        String sql = "INSERT INTO CUENTA (ACTIVO, PASSWORD, CORREO, FECHA_CREACION, NOMBRE_USUARIO) VALUES (?,?,?,?,?)";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, cuenta.isActivo());
            ps.setString(2, cuenta.getPassword());
            ps.setString(3, cuenta.getCorreo());
            ps.setDate(4, java.sql.Date.valueOf(cuenta.getFechaCreacion()));
            ps.setString(5, cuenta.getNombreUsuario());
            ps.executeUpdate();
            cuenta.setId(claveGenerada(ps));
        }
    }

    private static void insertarCliente(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO CLIENTE (ID_CUENTA, ACTIVO, DNI, NOMBRES, APELLIDO_PATERNO, APELLIDO_MATERNO, TELEFONO) "
                + "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cliente.getCuenta().getId());
            ps.setBoolean(2, cliente.isActivo());
            ps.setString(3, cliente.getDni());
            ps.setString(4, cliente.getNombres());
            ps.setString(5, cliente.getApellidoPaterno());
            ps.setString(6, cliente.getApellidoMaterno());
            ps.setString(7, cliente.getTelefono());
            ps.executeUpdate();
            cliente.setId(claveGenerada(ps));
        }
    }

    private static void insertarVeterinario(Veterinario veterinario) throws SQLException {
        String sql = "INSERT INTO VETERINARIO (ID_CUENTA, ACTIVO, DNI, NOMBRES, APELLIDO_PATERNO, APELLIDO_MATERNO, "
                + "TELEFONO, NUMERO_COLEGIATURA) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, veterinario.getCuenta().getId());
            ps.setBoolean(2, veterinario.isActivo());
            ps.setString(3, veterinario.getDni());
            ps.setString(4, veterinario.getNombres());
            ps.setString(5, veterinario.getApellidoPaterno());
            ps.setString(6, veterinario.getApellidoMaterno());
            ps.setString(7, veterinario.getTelefono());
            ps.setString(8, veterinario.getNumeroColegiatura());
            ps.executeUpdate();
            veterinario.setId(claveGenerada(ps));
        }
    }

    private static void insertarMascota(Mascota mascota) throws SQLException {
        String sql = "INSERT INTO MASCOTA (ID_CLIENTE, ACTIVO, NOMBRE, SEXO, PESO, FECHA_NACIMIENTO, TIPO_MASCOTA, RAZA) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mascota.getCliente().getId());
            ps.setBoolean(2, mascota.isActivo());
            ps.setString(3, mascota.getNombre());
            ps.setString(4, mascota.getSexo() == SexoMascota.MACHO ? "M" : "H");
            ps.setDouble(5, mascota.getPeso());
            ps.setDate(6, java.sql.Date.valueOf(mascota.getFechaNacimiento()));
            ps.setString(7, mascota.getTipoMascota().name());
            ps.setString(8, mascota.getRaza());
            ps.executeUpdate();
            mascota.setId(claveGenerada(ps));
        }
    }

    private static void insertarCita(Cita cita) throws SQLException {
        String sql = "INSERT INTO CITA (ID_MASCOTA, ID_VETERINARIO, ACTIVO, FECHA_HORA, ESTADO) "
                + "VALUES (?,?,?,?,?)";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cita.getMascota().getId());
            ps.setInt(2, cita.getVeterinario().getId());
            ps.setBoolean(3, cita.isActivo());
            ps.setObject(4, cita.getFechaHora());
            ps.setString(5, cita.getEstado().name());
            ps.executeUpdate();
            cita.setId(claveGenerada(ps));
        }
    }

    private static void insertarCategoriaArticulo(CategoriaArticulo categoria) throws SQLException {
        String sql = "INSERT INTO CATEGORIA_ARTICULO (ACTIVO, NOMBRE, DESCRIPCION) VALUES (?,?,?)";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, categoria.isActivo());
            ps.setString(2, categoria.getNombre());
            ps.setString(3, categoria.getDescripcion());
            ps.executeUpdate();
            categoria.setId(claveGenerada(ps));
        }
    }

    private static void insertarArticulo(Articulo articulo) throws SQLException {
        String sql = "INSERT INTO ARTICULO (ID_CATEGORIA_ARTICULO, ACTIVO, NOMBRE, PRECIO_BASE, DESCRIPCION, "
                + "STOCK_ACTUAL, STOCK_MINIMO, MARCA) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, articulo.getCategoria().getId());
            ps.setBoolean(2, articulo.isActivo());
            ps.setString(3, articulo.getNombre());
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(articulo.getPrecioBase()));
            ps.setString(5, articulo.getDescripcion());
            ps.setInt(6, articulo.getStockActual());
            ps.setInt(7, articulo.getStockMinimo());
            ps.setString(8, articulo.getMarca());
            ps.executeUpdate();
            articulo.setId(claveGenerada(ps));
        }
    }

    private static void insertarTratamiento(Tratamiento tratamiento) throws SQLException {
        String sql = "INSERT INTO TRATAMIENTO (ACTIVO, NOMBRE_PROCEDIMIENTO, DESCRIPCION) VALUES (?,?,?)";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, tratamiento.isActivo());
            ps.setString(2, tratamiento.getNombreProcedimiento());
            ps.setString(3, tratamiento.getDescripcion());
            ps.executeUpdate();
            tratamiento.setId(claveGenerada(ps));
        }
    }

    private static int claveGenerada(Statement st) throws SQLException {
        try (ResultSet rs = st.getGeneratedKeys()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    // Deja la BD compartida como estaba: borra todo lo insertado, hijos antes que padres.
    // No depende de AtencionMedicaDAOImpl.delete() (su cascada interna necesita
    // buscar_tratamiento_por_id, que no esta desplegado): usa los ids que ya quedaron
    // en memoria tras el insert, asi que la limpieza no puede fallar por eso.
    private static void limpiar(AtencionMedica atencionMedica, AtencionTratamiento atencionTratamiento,
                                 Diagnostico diagnostico, Tratamiento tratamiento, Cita cita, Articulo articulo,
                                 CategoriaArticulo categoria, Mascota mascota, Veterinario veterinario,
                                 Cuenta cuentaVet, Cliente cliente, Cuenta cuentaCliente) {
        if (atencionTratamiento != null && !atencionTratamiento.getInsumosUtilizados().isEmpty()) {
            eliminarDirecto("INSUMO_UTILIZADO", "ID_INSUMO_UTILIZADO",
                    atencionTratamiento.getInsumosUtilizados().get(0).getId());
        }
        eliminarDirecto("ATENCION_TRATAMIENTO", "ID_ATENCION_TRATAMIENTO",
                atencionTratamiento == null ? 0 : atencionTratamiento.getId());
        if (atencionMedica != null && !atencionMedica.getDiagnosticos().isEmpty()) {
            eliminarDirecto("ATENCION_DIAGNOSTICO", "ID_ATENCION_DIAGNOSTICO",
                    atencionMedica.getDiagnosticos().get(0).getId());
        }
        eliminarDirecto("ATENCION_MEDICA", "ID_ATENCION_MEDICA", atencionMedica == null ? 0 : atencionMedica.getId());
        eliminarDirecto("DIAGNOSTICO", "ID_DIAGNOSTICO", diagnostico == null ? 0 : diagnostico.getId());
        eliminarDirecto("TRATAMIENTO", "ID_TRATAMIENTO", tratamiento == null ? 0 : tratamiento.getId());
        eliminarDirecto("CITA", "ID_CITA", cita == null ? 0 : cita.getId());
        eliminarDirecto("ARTICULO", "ID_ARTICULO", articulo == null ? 0 : articulo.getId());
        eliminarDirecto("CATEGORIA_ARTICULO", "ID_CATEGORIA_ARTICULO", categoria == null ? 0 : categoria.getId());
        eliminarDirecto("MASCOTA", "ID_MASCOTA", mascota == null ? 0 : mascota.getId());
        eliminarDirecto("VETERINARIO", "ID_VETERINARIO", veterinario == null ? 0 : veterinario.getId());
        eliminarDirecto("CUENTA", "ID_CUENTA", cuentaVet == null ? 0 : cuentaVet.getId());
        eliminarDirecto("CLIENTE", "ID_CLIENTE", cliente == null ? 0 : cliente.getId());
        eliminarDirecto("CUENTA", "ID_CUENTA", cuentaCliente == null ? 0 : cuentaCliente.getId());
    }

    // Cada tabla se limpia de forma independiente: si una falla, las demas igual se intentan.
    private static void eliminarDirecto(String tabla, String columnaId, int id) {
        if (id == 0) {
            return;
        }
        String sql = "DELETE FROM " + tabla + " WHERE " + columnaId + " = ?";
        try (Connection conn = DBManager.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("No se pudo limpiar " + tabla + " (" + columnaId + "=" + id + "): " + e.getMessage());
        }
    }
}
