using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // SOLO PARA EL LAB 9 (pantallas sin funcionalidad):
    // datos de ejemplo escritos a mano para que las tablas y formularios muestren información.
    // No hay conexión a la base de datos ni a los servicios web. En los siguientes laboratorios
    // esta clase se reemplaza por la capa de Servicios que consume el backend en Java.
    // Cada método devuelve una lista nueva para que los cambios en una pantalla no afecten a otra.
    public static class DatosEjemplo {

        private static DateTime Hoy => DateTime.Today;

        // ---------------------------------------------------------------- Cuentas de usuario
        public static List<CuentaUsuarioViewModel> CuentasUsuario() => [
            new() { Id = 1, NombreUsuario = "diego.lopez", Correo = "diego.lopez@patasarriba.pe", Rol = RolUsuario.ADMINISTRADOR, NombrePersona = "Diego López Reyes" },
            new() { Id = 2, NombreUsuario = "gabriela.rios", Correo = "gabriela.rios@patasarriba.pe", Rol = RolUsuario.ADMINISTRADOR, NombrePersona = "Gabriela Ríos Campos" },
            new() { Id = 3, NombreUsuario = "carla.vega", Correo = "carla.vega@patasarriba.pe", Rol = RolUsuario.VETERINARIO, NombrePersona = "Carla Vega Soto" },
            new() { Id = 4, NombreUsuario = "miguel.castro", Correo = "miguel.castro@patasarriba.pe", Rol = RolUsuario.VETERINARIO, NombrePersona = "Miguel Ángel Castro Ruiz" },
            new() { Id = 5, NombreUsuario = "patricia.leon", Correo = "patricia.leon@patasarriba.pe", Rol = RolUsuario.VETERINARIO, NombrePersona = "Patricia León Flores" },
            new() { Id = 6, NombreUsuario = "ana.torres", Correo = "ana.torres@gmail.com", Rol = RolUsuario.CLIENTE, NombrePersona = "Ana Torres Quispe" },
            new() { Id = 7, NombreUsuario = "luis.ramirez", Correo = "luis.ramirez@hotmail.com", Rol = RolUsuario.CLIENTE, NombrePersona = "Luis Ramírez Paredes" },
            new() { Id = 8, NombreUsuario = "maria.salas", Correo = "mfsalas@gmail.com", Rol = RolUsuario.CLIENTE, NombrePersona = "María Fernanda Salas Cáceres" },
            new() { Id = 9, NombreUsuario = "jorge.huaman", Correo = "jorge.huaman@gmail.com", Rol = RolUsuario.CLIENTE, NombrePersona = "Jorge Huamán Rojas" },
            new() { Id = 10, NombreUsuario = "rosa.mendoza", Correo = "rosa.mendoza@yahoo.com", Rol = RolUsuario.CLIENTE, NombrePersona = "Rosa Mendoza Vílchez" }
        ];

        // ---------------------------------------------------------------- Personas
        public static List<ClienteViewModel> Clientes() => [
            new() { Id = 1, IdCuenta = 6, NombreUsuario = "ana.torres", Dni = "45879632", Nombres = "Ana", ApellidoPaterno = "Torres", ApellidoMaterno = "Quispe", Telefono = "987654321" },
            new() { Id = 2, IdCuenta = 7, NombreUsuario = "luis.ramirez", Dni = "41236598", Nombres = "Luis", ApellidoPaterno = "Ramírez", ApellidoMaterno = "Paredes", Telefono = "912345678" },
            new() { Id = 3, IdCuenta = 8, NombreUsuario = "maria.salas", Dni = "70214563", Nombres = "María Fernanda", ApellidoPaterno = "Salas", ApellidoMaterno = "Cáceres", Telefono = "956321478" },
            new() { Id = 4, IdCuenta = 9, NombreUsuario = "jorge.huaman", Dni = "46987123", Nombres = "Jorge", ApellidoPaterno = "Huamán", ApellidoMaterno = "Rojas", Telefono = "998877665" },
            new() { Id = 5, IdCuenta = 10, NombreUsuario = "rosa.mendoza", Dni = "43219876", Nombres = "Rosa", ApellidoPaterno = "Mendoza", ApellidoMaterno = "Vílchez", Telefono = "944556677" }
        ];

        public static List<VeterinarioViewModel> Veterinarios() => [
            new() { Id = 1, IdCuenta = 3, NombreUsuario = "carla.vega", Dni = "40125896", Nombres = "Carla", ApellidoPaterno = "Vega", ApellidoMaterno = "Soto", Telefono = "987112233", NumeroColegiatura = "CMVP-04521" },
            new() { Id = 2, IdCuenta = 4, NombreUsuario = "miguel.castro", Dni = "42563187", Nombres = "Miguel Ángel", ApellidoPaterno = "Castro", ApellidoMaterno = "Ruiz", Telefono = "965443322", NumeroColegiatura = "CMVP-03987" },
            new() { Id = 3, IdCuenta = 5, NombreUsuario = "patricia.leon", Dni = "47896541", Nombres = "Patricia", ApellidoPaterno = "León", ApellidoMaterno = "Flores", Telefono = "954778899", NumeroColegiatura = "CMVP-05210" }
        ];

        public static List<AdministradorViewModel> Administradores() => [
            new() { Id = 1, IdCuenta = 1, NombreUsuario = "diego.lopez", Dni = "72145638", Nombres = "Diego", ApellidoPaterno = "López", ApellidoMaterno = "Reyes", Telefono = "999111222" },
            new() { Id = 2, IdCuenta = 2, NombreUsuario = "gabriela.rios", Dni = "71236547", Nombres = "Gabriela", ApellidoPaterno = "Ríos", ApellidoMaterno = "Campos", Telefono = "988333444" }
        ];

        // ---------------------------------------------------------------- Mascotas
        public static List<MascotaViewModel> Mascotas() => [
            new() { Id = 1, IdCliente = 1, NombreDueno = "Ana Torres Quispe", Nombre = "Firulais", TipoMascota = TipoMascota.PERRO, Raza = "Mestizo", Sexo = SexoMascota.MACHO, Peso = 12.5, FechaNacimiento = new DateOnly(2021, 3, 14) },
            new() { Id = 2, IdCliente = 1, NombreDueno = "Ana Torres Quispe", Nombre = "Michi", TipoMascota = TipoMascota.GATO, Raza = "Siamés", Sexo = SexoMascota.HEMBRA, Peso = 4.2, FechaNacimiento = new DateOnly(2022, 7, 2) },
            new() { Id = 3, IdCliente = 2, NombreDueno = "Luis Ramírez Paredes", Nombre = "Rocky", TipoMascota = TipoMascota.PERRO, Raza = "Labrador", Sexo = SexoMascota.MACHO, Peso = 28.0, FechaNacimiento = new DateOnly(2019, 11, 20) },
            new() { Id = 4, IdCliente = 3, NombreDueno = "María Fernanda Salas Cáceres", Nombre = "Luna", TipoMascota = TipoMascota.GATO, Raza = "Persa", Sexo = SexoMascota.HEMBRA, Peso = 3.8, FechaNacimiento = new DateOnly(2023, 1, 9) },
            new() { Id = 5, IdCliente = 4, NombreDueno = "Jorge Huamán Rojas", Nombre = "Max", TipoMascota = TipoMascota.PERRO, Raza = "Schnauzer", Sexo = SexoMascota.MACHO, Peso = 9.1, FechaNacimiento = new DateOnly(2020, 5, 30) },
            new() { Id = 6, IdCliente = 4, NombreDueno = "Jorge Huamán Rojas", Nombre = "Canela", TipoMascota = TipoMascota.PERRO, Raza = "Shih Tzu", Sexo = SexoMascota.HEMBRA, Peso = 6.4, FechaNacimiento = new DateOnly(2022, 9, 18) },
            new() { Id = 7, IdCliente = 5, NombreDueno = "Rosa Mendoza Vílchez", Nombre = "Simba", TipoMascota = TipoMascota.GATO, Raza = "Mestizo", Sexo = SexoMascota.MACHO, Peso = 5.0, FechaNacimiento = new DateOnly(2021, 12, 1) }
        ];

        public static List<MascotaViewModel> MascotasDeCliente(int idCliente) {
            return Mascotas().Where(m => m.IdCliente == idCliente).ToList();
        }

        // ---------------------------------------------------------------- Catálogos
        public static List<ServicioViewModel> Servicios() => [
            new() { Id = 1, Nombre = "Consulta general", Descripcion = "Evaluación clínica general de la mascota", PrecioBase = 60.00, DuracionEstimada = 30, TipoServicio = TipoServicio.CONSULTA_MEDICA },
            new() { Id = 2, Nombre = "Vacuna antirrábica", Descripcion = "Aplicación de la vacuna contra la rabia", PrecioBase = 50.00, DuracionEstimada = 15, TipoServicio = TipoServicio.VACUNACION },
            new() { Id = 3, Nombre = "Vacuna quíntuple", Descripcion = "Vacuna contra moquillo, parvovirus, hepatitis, parainfluenza y leptospira", PrecioBase = 70.00, DuracionEstimada = 15, TipoServicio = TipoServicio.VACUNACION },
            new() { Id = 4, Nombre = "Baño y corte", Descripcion = "Baño medicado, corte de pelo y de uñas", PrecioBase = 45.00, DuracionEstimada = 60, TipoServicio = TipoServicio.ESTETICA },
            new() { Id = 5, Nombre = "Esterilización", Descripcion = "Cirugía de esterilización con anestesia general", PrecioBase = 350.00, DuracionEstimada = 120, TipoServicio = TipoServicio.OPERACION },
            new() { Id = 6, Nombre = "Atención de emergencia", Descripcion = "Atención inmediata de casos urgentes", PrecioBase = 120.00, DuracionEstimada = 45, TipoServicio = TipoServicio.EMERGENCIA },
            new() { Id = 7, Nombre = "Limpieza dental", Descripcion = "Profilaxis dental con ultrasonido", PrecioBase = 180.00, DuracionEstimada = 60, TipoServicio = TipoServicio.OPERACION }
        ];

        public static List<DiagnosticoViewModel> Diagnosticos() => [
            new() { Id = 1, NombreEnfermedad = "Otitis externa", Descripcion = "Inflamación del conducto auditivo externo" },
            new() { Id = 2, NombreEnfermedad = "Dermatitis alérgica", Descripcion = "Reacción alérgica de la piel con picazón y enrojecimiento" },
            new() { Id = 3, NombreEnfermedad = "Gastroenteritis", Descripcion = "Inflamación del estómago y los intestinos" },
            new() { Id = 4, NombreEnfermedad = "Parvovirosis canina", Descripcion = "Infección viral grave del tracto digestivo en perros" },
            new() { Id = 5, NombreEnfermedad = "Insuficiencia renal crónica", Descripcion = "Pérdida progresiva de la función de los riñones" },
            new() { Id = 6, NombreEnfermedad = "Conjuntivitis", Descripcion = "Inflamación de la membrana que recubre el ojo" }
        ];

        public static List<CategoriaViewModel> Categorias() => [
            new() { Id = 1, Nombre = "Medicamentos", Descripcion = "Fármacos de uso veterinario" },
            new() { Id = 2, Nombre = "Vacunas", Descripcion = "Vacunas para perros y gatos" },
            new() { Id = 3, Nombre = "Alimentos", Descripcion = "Alimento balanceado y snacks" },
            new() { Id = 4, Nombre = "Accesorios", Descripcion = "Collares, correas, juguetes y camas" },
            new() { Id = 5, Nombre = "Higiene", Descripcion = "Shampoos, algodón y productos de limpieza" }
        ];

        // Todas las categorías como casillas, marcando las indicadas
        public static List<OpcionSeleccion> OpcionesCategoria(params string[] seleccionadas) {
            return Categorias()
                .Select(c => new OpcionSeleccion { Id = c.Id, Texto = c.Nombre, Seleccionado = seleccionadas.Contains(c.Nombre) })
                .ToList();
        }

        public static List<ArticuloViewModel> Articulos() => [
            new() { Id = 1, Nombre = "Amoxicilina 250 mg", Descripcion = "Antibiótico en tabletas", PrecioBase = 2.50, StockActual = 38, StockMinimo = 10, Categorias = OpcionesCategoria("Medicamentos") },
            new() { Id = 2, Nombre = "Vacuna antirrábica (dosis)", Descripcion = "Dosis individual", PrecioBase = 35.00, StockActual = 12, StockMinimo = 5, Categorias = OpcionesCategoria("Vacunas") },
            new() { Id = 3, Nombre = "Desparasitante Drontal", Descripcion = "Antiparasitario interno", PrecioBase = 10.00, StockActual = 4, StockMinimo = 10, Categorias = OpcionesCategoria("Medicamentos") },
            new() { Id = 4, Nombre = "Shampoo antipulgas", Descripcion = "Frasco de 250 ml", PrecioBase = 25.00, StockActual = 15, StockMinimo = 5, Categorias = OpcionesCategoria("Higiene") },
            new() { Id = 5, Nombre = "Collar antipulgas", Descripcion = "Protección por 8 meses", PrecioBase = 30.00, StockActual = 3, StockMinimo = 5, Categorias = OpcionesCategoria("Accesorios", "Higiene") },
            new() { Id = 6, Nombre = "Alimento Pro Plan 3 kg", Descripcion = "Alimento balanceado para perro adulto", PrecioBase = 89.90, StockActual = 20, StockMinimo = 5, Categorias = OpcionesCategoria("Alimentos") },
            new() { Id = 7, Nombre = "Meloxicam 1.5 mg/ml", Descripcion = "Antiinflamatorio en suspensión oral", PrecioBase = 18.50, StockActual = 9, StockMinimo = 5, Categorias = OpcionesCategoria("Medicamentos") },
            new() { Id = 8, Nombre = "Algodón 100 g", Descripcion = "Insumo de uso clínico", PrecioBase = 4.00, StockActual = 50, StockMinimo = 10, Categorias = OpcionesCategoria("Higiene") }
        ];

        // Artículos que se pueden recetar
        public static List<ArticuloViewModel> Medicamentos() {
            return Articulos().Where(a => a.NombresCategorias.Contains("Medicamentos")).ToList();
        }

        // ---------------------------------------------------------------- Horarios
        // Veterinarios y administradores como casillas, marcando los asignados
        public static List<OpcionSeleccion> OpcionesPersonal(params string[] asignados) {
            List<OpcionSeleccion> opciones = [];
            foreach (VeterinarioViewModel veterinario in Veterinarios()) {
                opciones.Add(new OpcionSeleccion { Id = veterinario.Id, Texto = veterinario.NombreCompleto, Detalle = "Veterinario", Seleccionado = asignados.Contains(veterinario.NombreCompleto) });
            }
            foreach (AdministradorViewModel administrador in Administradores()) {
                opciones.Add(new OpcionSeleccion { Id = 100 + administrador.Id, Texto = administrador.NombreCompleto, Detalle = "Administrador", Seleccionado = asignados.Contains(administrador.NombreCompleto) });
            }
            return opciones;
        }

        public static List<HorarioViewModel> Horarios() => [
            new() { Id = 1, DiaSemana = DiaSemana.LUNES, HoraInicio = new TimeOnly(8, 0), HoraFin = new TimeOnly(14, 0), Personal = OpcionesPersonal("Carla Vega Soto", "Diego López Reyes") },
            new() { Id = 2, DiaSemana = DiaSemana.LUNES, HoraInicio = new TimeOnly(14, 0), HoraFin = new TimeOnly(20, 0), Personal = OpcionesPersonal("Miguel Ángel Castro Ruiz", "Gabriela Ríos Campos") },
            new() { Id = 3, DiaSemana = DiaSemana.MARTES, HoraInicio = new TimeOnly(8, 0), HoraFin = new TimeOnly(14, 0), Personal = OpcionesPersonal("Patricia León Flores", "Diego López Reyes") },
            new() { Id = 4, DiaSemana = DiaSemana.MIERCOLES, HoraInicio = new TimeOnly(8, 0), HoraFin = new TimeOnly(14, 0), Personal = OpcionesPersonal("Carla Vega Soto", "Miguel Ángel Castro Ruiz") },
            new() { Id = 5, DiaSemana = DiaSemana.SABADO, HoraInicio = new TimeOnly(9, 0), HoraFin = new TimeOnly(13, 0), Personal = OpcionesPersonal("Patricia León Flores", "Gabriela Ríos Campos") }
        ];

        // ---------------------------------------------------------------- Citas
        private static DetalleCitaViewModel Detalle(int id, int idServicio, int idVeterinario) {
            ServicioViewModel servicio = Servicios().First(s => s.Id == idServicio);
            VeterinarioViewModel veterinario = Veterinarios().First(v => v.Id == idVeterinario);
            return new DetalleCitaViewModel {
                Id = id,
                IdServicio = servicio.Id,
                NombreServicio = servicio.Nombre,
                IdVeterinario = veterinario.Id,
                NombreVeterinario = veterinario.NombreCompleto
            };
        }

        public static List<CitaViewModel> Citas() => [
            new() { Id = 1, IdCliente = 1, NombreCliente = "Ana Torres Quispe", IdMascota = 1, NombreMascota = "Firulais", FechaHora = Hoy.AddHours(9), Estado = EstadoCita.APROBADO, NombreAdministrador = "Diego López Reyes",
                    Detalles = [Detalle(1, 1, 1), Detalle(2, 2, 1)] },
            new() { Id = 2, IdCliente = 2, NombreCliente = "Luis Ramírez Paredes", IdMascota = 3, NombreMascota = "Rocky", FechaHora = Hoy.AddHours(10.5), Estado = EstadoCita.APROBADO, NombreAdministrador = "Diego López Reyes",
                    Detalles = [Detalle(3, 4, 2)] },
            new() { Id = 3, IdCliente = 3, NombreCliente = "María Fernanda Salas Cáceres", IdMascota = 4, NombreMascota = "Luna", FechaHora = Hoy.AddHours(11), Estado = EstadoCita.PENDIENTE, EsEmergencia = true,
                    Detalles = [Detalle(4, 6, 1)] },
            new() { Id = 4, IdCliente = 4, NombreCliente = "Jorge Huamán Rojas", IdMascota = 5, NombreMascota = "Max", FechaHora = Hoy.AddDays(1).AddHours(16), Estado = EstadoCita.PENDIENTE,
                    Detalles = [Detalle(5, 5, 3)] },
            new() { Id = 5, IdCliente = 1, NombreCliente = "Ana Torres Quispe", IdMascota = 2, NombreMascota = "Michi", FechaHora = Hoy.AddDays(-7).AddHours(10), Estado = EstadoCita.COMPLETADO, NombreAdministrador = "Gabriela Ríos Campos",
                    Detalles = [Detalle(6, 1, 3)] },
            new() { Id = 6, IdCliente = 5, NombreCliente = "Rosa Mendoza Vílchez", IdMascota = 7, NombreMascota = "Simba", FechaHora = Hoy.AddDays(-3).AddHours(15), Estado = EstadoCita.CANCELADO, NombreAdministrador = "Diego López Reyes",
                    Detalles = [Detalle(7, 3, 2)] },
            new() { Id = 7, IdCliente = 4, NombreCliente = "Jorge Huamán Rojas", IdMascota = 6, NombreMascota = "Canela", FechaHora = Hoy.AddDays(-2).AddHours(9.5), Estado = EstadoCita.RECHAZADO, NombreAdministrador = "Gabriela Ríos Campos",
                    Detalles = [Detalle(8, 7, 3)] },
            new() { Id = 8, IdCliente = 1, NombreCliente = "Ana Torres Quispe", IdMascota = 1, NombreMascota = "Firulais", FechaHora = Hoy.AddDays(-30).AddHours(17), Estado = EstadoCita.COMPLETADO, NombreAdministrador = "Diego López Reyes",
                    Detalles = [Detalle(9, 1, 1), Detalle(10, 2, 1)] }
        ];

        // Citas del cliente que inició sesión (en el ejemplo: Ana Torres)
        public static List<CitaViewModel> CitasDeCliente(int idCliente) {
            return Citas().Where(c => c.IdCliente == idCliente).OrderByDescending(c => c.FechaHora).ToList();
        }

        // Todos los servicios programados para hoy
        public static List<ItemAgendaViewModel> AgendaDeHoy() => [
            new() { IdCita = 1, IdDetalleCita = 1, IdMascota = 1, FechaHora = Hoy.AddHours(9), NombreMascota = "Firulais", NombreCliente = "Ana Torres Quispe", NombreServicio = "Consulta general", NombreVeterinario = "Carla Vega Soto" },
            new() { IdCita = 1, IdDetalleCita = 2, IdMascota = 1, FechaHora = Hoy.AddHours(9.5), NombreMascota = "Firulais", NombreCliente = "Ana Torres Quispe", NombreServicio = "Vacuna antirrábica", NombreVeterinario = "Carla Vega Soto" },
            new() { IdCita = 2, IdDetalleCita = 3, IdMascota = 3, FechaHora = Hoy.AddHours(10.5), NombreMascota = "Rocky", NombreCliente = "Luis Ramírez Paredes", NombreServicio = "Baño y corte", NombreVeterinario = "Miguel Ángel Castro Ruiz" },
            new() { IdCita = 3, IdDetalleCita = 4, IdMascota = 4, FechaHora = Hoy.AddHours(11), NombreMascota = "Luna", NombreCliente = "María Fernanda Salas Cáceres", NombreServicio = "Atención de emergencia", NombreVeterinario = "Carla Vega Soto", EsEmergencia = true }
        ];

        // Agenda del veterinario que inició sesión (en el ejemplo: Carla Vega)
        public static List<ItemAgendaViewModel> AgendaDeVeterinario(string nombreVeterinario) {
            return AgendaDeHoy().Where(i => i.NombreVeterinario == nombreVeterinario).ToList();
        }

        // ---------------------------------------------------------------- Atenciones y recetas
        public static List<AtencionViewModel> Atenciones() => [
            new() { Id = 1, IdDetalleCita = 6, IdMascota = 2, NombreMascota = "Michi", NombreCliente = "Ana Torres Quispe", NombreServicio = "Consulta general", NombreVeterinario = "Patricia León Flores",
                    FechaHora = Hoy.AddDays(-7).AddHours(10.25), MotivoConsulta = "Se rasca las orejas constantemente", Observaciones = "Secreción oscura en el oído izquierdo. Sin fiebre.",
                    Diagnosticos = [new() { IdDiagnostico = 1, NombreEnfermedad = "Otitis externa", NivelGravedad = NivelGravedad.MODERADO, DetalleDiagnostico = "Oído izquierdo" }],
                    Insumos = [new() { Id = 1, IdArticulo = 8, NombreArticulo = "Algodón 100 g", CantidadUtilizada = 1, EsCobrable = false, PrecioUnitario = 4.00 },
                               new() { Id = 2, IdArticulo = 7, NombreArticulo = "Meloxicam 1.5 mg/ml", CantidadUtilizada = 1, EsCobrable = true, PrecioUnitario = 18.50 }] },
            new() { Id = 2, IdDetalleCita = 9, IdMascota = 1, NombreMascota = "Firulais", NombreCliente = "Ana Torres Quispe", NombreServicio = "Consulta general", NombreVeterinario = "Carla Vega Soto",
                    FechaHora = Hoy.AddDays(-30).AddHours(17.2), MotivoConsulta = "Diarrea desde hace dos días", Observaciones = "Leve deshidratación. Se recomienda dieta blanda.",
                    Diagnosticos = [new() { IdDiagnostico = 3, NombreEnfermedad = "Gastroenteritis", NivelGravedad = NivelGravedad.LEVE }] },
            new() { Id = 3, IdDetalleCita = 10, IdMascota = 1, NombreMascota = "Firulais", NombreCliente = "Ana Torres Quispe", NombreServicio = "Vacuna antirrábica", NombreVeterinario = "Carla Vega Soto",
                    FechaHora = Hoy.AddDays(-30).AddHours(17.6), MotivoConsulta = "Vacunación anual", Observaciones = "Sin reacciones adversas.",
                    Insumos = [new() { Id = 3, IdArticulo = 2, NombreArticulo = "Vacuna antirrábica (dosis)", CantidadUtilizada = 1, EsCobrable = true, PrecioUnitario = 35.00 },
                               new() { Id = 4, IdArticulo = 8, NombreArticulo = "Algodón 100 g", CantidadUtilizada = 1, EsCobrable = false, PrecioUnitario = 4.00 }] },
            new() { Id = 4, IdDetalleCita = 1, IdMascota = 1, NombreMascota = "Firulais", NombreCliente = "Ana Torres Quispe", NombreServicio = "Consulta general", NombreVeterinario = "Carla Vega Soto",
                    FechaHora = Hoy.AddHours(9.15), MotivoConsulta = "Control general", Observaciones = "Mascota en buen estado. Se desparasita.",
                    Insumos = [new() { Id = 5, IdArticulo = 3, NombreArticulo = "Desparasitante Drontal", CantidadUtilizada = 1, EsCobrable = true, PrecioUnitario = 10.00 }] }
        ];

        public static List<AtencionViewModel> HistorialDeMascota(int idMascota) {
            return Atenciones().Where(a => a.IdMascota == idMascota).OrderByDescending(a => a.FechaHora).ToList();
        }

        public static List<RecetaViewModel> Recetas() => [
            new() { Id = 1, IdAtencion = 1, NombreMascota = "Michi", TipoMascota = TipoMascota.GATO, NombreCliente = "Ana Torres Quispe", NombreVeterinario = "Patricia León Flores", NumeroColegiatura = "CMVP-05210",
                    Diagnostico = "Otitis externa (moderado)", FechaEmision = DateOnly.FromDateTime(Hoy.AddDays(-7)),
                    IndicacionesGenerales = "Limpiar el oído con solución ótica antes de cada dosis. Control en 7 días.",
                    Medicamentos = [new() { IdArticulo = 7, NombreArticulo = "Meloxicam 1.5 mg/ml", Dosis = "0.1 ml", Frecuencia = "Cada 24 horas", DuracionDias = 5, CantidadTotal = 1 }] },
            new() { Id = 2, IdAtencion = 2, NombreMascota = "Firulais", TipoMascota = TipoMascota.PERRO, NombreCliente = "Ana Torres Quispe", NombreVeterinario = "Carla Vega Soto", NumeroColegiatura = "CMVP-04521",
                    Diagnostico = "Gastroenteritis (leve)", FechaEmision = DateOnly.FromDateTime(Hoy.AddDays(-30)),
                    IndicacionesGenerales = "Dieta blanda (arroz con pollo sancochado) por 3 días. Abundante agua.",
                    Medicamentos = [new() { IdArticulo = 1, NombreArticulo = "Amoxicilina 250 mg", Dosis = "1 tableta", Frecuencia = "Cada 12 horas", DuracionDias = 7, CantidadTotal = 14 }] }
        ];

        public static RecetaViewModel? RecetaDeAtencion(int idAtencion) {
            return Recetas().FirstOrDefault(r => r.IdAtencion == idAtencion);
        }

        // ---------------------------------------------------------------- Boletas
        public static List<BoletaViewModel> Boletas() => [
            new() { Id = 4, SerieCorrelativo = "B001-000124", Fecha = Hoy.AddHours(8.75), IdCliente = 2, NombreCliente = "Luis Ramírez Paredes", DniCliente = "41236598", MetodoPago = MetodoPago.TARJETA_DE_DEBITO,
                    Lineas = [new() { Tipo = "Artículo", Descripcion = "Alimento Pro Plan 3 kg", Cantidad = 1, PrecioUnitario = 89.90 }] },
            new() { Id = 3, SerieCorrelativo = "B001-000123", Fecha = Hoy.AddDays(-7).AddHours(10.8), IdCliente = 1, NombreCliente = "Ana Torres Quispe", DniCliente = "45879632", MetodoPago = MetodoPago.YAPE,
                    Lineas = [new() { Tipo = "Servicio", Descripcion = "Consulta general · Michi", Cantidad = 1, PrecioUnitario = 60.00 },
                              new() { Tipo = "Insumo", Descripcion = "Meloxicam 1.5 mg/ml · Michi", Cantidad = 1, PrecioUnitario = 18.50 },
                              new() { Tipo = "Artículo", Descripcion = "Shampoo antipulgas", Cantidad = 1, PrecioUnitario = 25.00 }] },
            new() { Id = 2, SerieCorrelativo = "B001-000122", Fecha = Hoy.AddDays(-30).AddHours(18), IdCliente = 1, NombreCliente = "Ana Torres Quispe", DniCliente = "45879632", MetodoPago = MetodoPago.EFECTIVO,
                    Lineas = [new() { Tipo = "Servicio", Descripcion = "Consulta general · Firulais", Cantidad = 1, PrecioUnitario = 60.00 },
                              new() { Tipo = "Servicio", Descripcion = "Vacuna antirrábica · Firulais", Cantidad = 1, PrecioUnitario = 50.00 },
                              new() { Tipo = "Insumo", Descripcion = "Vacuna antirrábica (dosis) · Firulais", Cantidad = 1, PrecioUnitario = 35.00 }] },
            new() { Id = 1, SerieCorrelativo = "B001-000121", Fecha = Hoy.AddDays(-10).AddHours(11.3), IdCliente = 4, NombreCliente = "Jorge Huamán Rojas", DniCliente = "46987123", MetodoPago = MetodoPago.EFECTIVO,
                    Lineas = [new() { Tipo = "Artículo", Descripcion = "Collar antipulgas", Cantidad = 1, PrecioUnitario = 30.00 }] }
        ];

        // Servicios atendidos e insumos cobrables que el cliente aún no ha pagado (en el ejemplo: la cita de hoy de Firulais)
        public static List<LineaBoletaViewModel> PendientesDeCobro() => [
            new() { Tipo = "Servicio", Descripcion = "Consulta general · Firulais", Cantidad = 1, PrecioUnitario = 60.00 },
            new() { Tipo = "Insumo", Descripcion = "Desparasitante Drontal · Firulais", Cantidad = 1, PrecioUnitario = 10.00 }
        ];
    }
}
