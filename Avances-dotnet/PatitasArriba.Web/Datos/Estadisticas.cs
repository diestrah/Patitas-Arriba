using PatitasArriba.Web.Components.Graficos;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Datos {
    // Indicadores y series de los paneles de inicio de cada rol.
    // Se calculan sobre la misma fuente que usan las pantallas (hoy DatosEjemplo, que replica las tablas
    // de patitasarriba-mysql.sql). Cuando se conecte con el backend solo cambia de dónde salen las listas;
    // cada método indica la tabla y columnas de la BD en las que se basa.
    public static class Estadisticas {

        private static DateTime Hoy => DateTime.Today;

        // Citas que siguen en pie (no rechazadas ni canceladas)
        private static bool EstaVigente(CitaViewModel cita) =>
            cita.Estado != EstadoCita.RECHAZADO && cita.Estado != EstadoCita.CANCELADO;

        public static string ColorDe(EstadoCita estado) => estado switch {
            EstadoCita.PENDIENTE => Paleta.Sol,
            EstadoCita.APROBADO => Paleta.Turquesa,
            EstadoCita.RECHAZADO => Paleta.Rosa,
            EstadoCita.CANCELADO => Paleta.Gris,
            EstadoCita.COMPLETADO => Paleta.Uva,
            _ => Paleta.Gris
        };

        public static string ColorDe(TipoServicio tipo) => tipo switch {
            TipoServicio.CONSULTA_MEDICA => Paleta.Turquesa,
            TipoServicio.OPERACION => Paleta.Uva,
            TipoServicio.VACUNACION => Paleta.Cielo,
            TipoServicio.ESTETICA => Paleta.Coral,
            TipoServicio.EMERGENCIA => Paleta.Rosa,
            _ => Paleta.Gris
        };

        public static string ColorDe(NivelGravedad nivel) => nivel switch {
            NivelGravedad.LEVE => Paleta.Oliva,
            NivelGravedad.MODERADO => Paleta.Sol,
            NivelGravedad.GRAVE => Paleta.Coral,
            NivelGravedad.CRITICO => Paleta.Rosa,
            _ => Paleta.Gris
        };

        // ------------------------------------------------------------------ Comunes

        // cita.estado: cuántas citas hay en cada estado del ENUM
        public static List<PuntoGrafico> CitasPorEstado(IEnumerable<CitaViewModel> citas) {
            return Enum.GetValues<EstadoCita>()
                .Select(e => new PuntoGrafico(TextoEnum.De(e), citas.Count(c => c.Estado == e), ColorDe(e)))
                .ToList();
        }

        // mascota.tipo_mascota
        public static List<PuntoGrafico> MascotasPorTipo(IEnumerable<MascotaViewModel> mascotas) {
            return [
                new PuntoGrafico("Perros", mascotas.Count(m => m.TipoMascota == TipoMascota.PERRO), Paleta.Coral),
                new PuntoGrafico("Gatos", mascotas.Count(m => m.TipoMascota == TipoMascota.GATO), Paleta.Uva)
            ];
        }

        // detalle_cita ⋈ servicio: servicios más pedidos en las citas vigentes
        public static List<PuntoGrafico> ServiciosMasSolicitados(IEnumerable<CitaViewModel> citas, int cantidad = 5) {
            Dictionary<int, ServicioViewModel> servicios = DatosEjemplo.Servicios().ToDictionary(s => s.Id);
            return citas.Where(EstaVigente)
                .SelectMany(c => c.Detalles)
                .GroupBy(d => d.IdServicio)
                .Select(g => {
                    ServicioViewModel? servicio = servicios.GetValueOrDefault(g.Key);
                    string color = servicio == null ? Paleta.Gris : ColorDe(servicio.TipoServicio);
                    string? tipo = servicio == null ? null : TextoEnum.De(servicio.TipoServicio);
                    return new PuntoGrafico(g.First().NombreServicio, g.Count(), color, tipo);
                })
                .OrderByDescending(p => p.Valor)
                .Take(cantidad)
                .ToList();
        }

        // detalle_cita ⋈ servicio.tipo_servicio
        public static List<PuntoGrafico> ServiciosPorTipo(IEnumerable<DetalleCitaViewModel> detalles) {
            Dictionary<int, ServicioViewModel> servicios = DatosEjemplo.Servicios().ToDictionary(s => s.Id);
            List<DetalleCitaViewModel> lista = detalles.ToList();
            return Enum.GetValues<TipoServicio>()
                .Select(t => new PuntoGrafico(TextoEnum.De(t),
                    lista.Count(d => servicios.TryGetValue(d.IdServicio, out ServicioViewModel? s) && s.TipoServicio == t),
                    ColorDe(t)))
                .ToList();
        }

        // ------------------------------------------------------------------ Administrador

        public record ResumenAdministrador(
            int CitasHoy, int CitasPendientes, int EmergenciasPendientes,
            double IngresosMes, int BoletasMes, int ArticulosStockBajo,
            int Clientes, int Mascotas, int Veterinarios);

        public static ResumenAdministrador Administrador() {
            List<CitaViewModel> citas = DatosEjemplo.Citas();
            List<BoletaViewModel> boletasMes = DatosEjemplo.Boletas()
                .Where(b => b.IsActivo && b.Fecha.Year == Hoy.Year && b.Fecha.Month == Hoy.Month)
                .ToList();
            return new ResumenAdministrador(
                CitasHoy: citas.Count(c => c.FechaHora?.Date == Hoy && EstaVigente(c)),
                CitasPendientes: citas.Count(c => c.Estado == EstadoCita.PENDIENTE),
                EmergenciasPendientes: citas.Count(c => c.Estado == EstadoCita.PENDIENTE && c.EsEmergencia),
                IngresosMes: boletasMes.Sum(b => b.Total),
                BoletasMes: boletasMes.Count,
                ArticulosStockBajo: DatosEjemplo.Articulos().Count(a => a.StockActual <= a.StockMinimo),
                Clientes: DatosEjemplo.Clientes().Count,
                Mascotas: DatosEjemplo.Mascotas().Count,
                Veterinarios: DatosEjemplo.Veterinarios().Count);
        }

        // boleta.fecha, boleta.total: ingresos de las últimas semanas (la última es la actual)
        public static List<PuntoGrafico> IngresosPorSemana(int semanas = 6) {
            List<BoletaViewModel> boletas = DatosEjemplo.Boletas().Where(b => b.IsActivo).ToList();
            List<PuntoGrafico> serie = [];
            for (int i = semanas - 1; i >= 0; i--) {
                DateTime fin = Hoy.AddDays(1 - 7 * i);
                DateTime inicio = fin.AddDays(-7);
                double total = boletas.Where(b => b.Fecha >= inicio && b.Fecha < fin).Sum(b => b.Total);
                string etiqueta = i == 0 ? "Esta sem." : inicio.ToString("dd/MM");
                serie.Add(new PuntoGrafico(etiqueta, total, i == 0 ? Paleta.Coral : Paleta.Turquesa));
            }
            return serie;
        }

        // boleta.metodo_pago
        public static List<PuntoGrafico> IngresosPorMetodoPago() {
            List<BoletaViewModel> boletas = DatosEjemplo.Boletas().Where(b => b.IsActivo).ToList();
            return Enum.GetValues<MetodoPago>()
                .Select((m, i) => new PuntoGrafico(TextoEnum.De(m), boletas.Where(b => b.MetodoPago == m).Sum(b => b.Total), Paleta.Color(i)))
                .ToList();
        }

        // detalle_cita.id_veterinario: carga de trabajo de cada veterinario en las citas vigentes
        public static List<PuntoGrafico> ServiciosPorVeterinario() {
            List<DetalleCitaViewModel> detalles = DatosEjemplo.Citas().Where(EstaVigente).SelectMany(c => c.Detalles).ToList();
            return DatosEjemplo.Veterinarios()
                .Select((v, i) => new PuntoGrafico(v.NombreCompleto, detalles.Count(d => d.IdVeterinario == v.Id), Paleta.Color(i + 3), v.NumeroColegiatura))
                .OrderByDescending(p => p.Valor)
                .ToList();
        }

        // articulo.stock_actual vs articulo.stock_minimo, primero los que están más cerca de agotarse
        public static List<ArticuloViewModel> ArticulosPorReponer(int cantidad = 6) {
            return DatosEjemplo.Articulos()
                .OrderBy(a => a.StockMinimo == 0 ? double.MaxValue : (double)a.StockActual / a.StockMinimo)
                .Take(cantidad)
                .ToList();
        }

        // cita + mascota: las próximas citas por aprobar
        public static List<CitaViewModel> CitasPorAprobar(int cantidad = 5) {
            return DatosEjemplo.Citas()
                .Where(c => c.Estado == EstadoCita.PENDIENTE)
                .OrderByDescending(c => c.EsEmergencia)
                .ThenBy(c => c.FechaHora)
                .Take(cantidad)
                .ToList();
        }

        // ------------------------------------------------------------------ Veterinario

        public record ResumenVeterinario(
            int ServiciosHoy, int EmergenciasHoy, int PacientesAtendidos, int AtencionesRegistradas, int RecetasEmitidas);

        public static ResumenVeterinario Veterinario(string nombreVeterinario) {
            List<ItemAgendaViewModel> agenda = DatosEjemplo.AgendaDeVeterinario(nombreVeterinario);
            List<AtencionViewModel> atenciones = DatosEjemplo.Atenciones().Where(a => a.NombreVeterinario == nombreVeterinario).ToList();
            return new ResumenVeterinario(
                ServiciosHoy: agenda.Count,
                EmergenciasHoy: agenda.Count(i => i.EsEmergencia),
                PacientesAtendidos: atenciones.Select(a => a.IdMascota).Distinct().Count(),
                AtencionesRegistradas: atenciones.Count,
                RecetasEmitidas: DatosEjemplo.Recetas().Count(r => r.NombreVeterinario == nombreVeterinario));
        }

        // detalle_cita del veterinario en citas vigentes
        public static List<DetalleCitaViewModel> DetallesDeVeterinario(string nombreVeterinario) {
            return DatosEjemplo.Citas().Where(EstaVigente)
                .SelectMany(c => c.Detalles)
                .Where(d => d.NombreVeterinario == nombreVeterinario)
                .ToList();
        }

        // mascota de las citas en las que participa el veterinario
        public static List<MascotaViewModel> PacientesDeVeterinario(string nombreVeterinario) {
            HashSet<int> idsMascota = DatosEjemplo.Citas()
                .Where(c => c.Detalles.Any(d => d.NombreVeterinario == nombreVeterinario))
                .Select(c => c.IdMascota)
                .ToHashSet();
            return DatosEjemplo.Mascotas().Where(m => idsMascota.Contains(m.Id)).ToList();
        }

        // atencion_diagnostico ⋈ diagnostico: enfermedades más diagnosticadas en la clínica
        public static List<PuntoGrafico> DiagnosticosFrecuentes(int cantidad = 5) {
            return DatosEjemplo.Atenciones()
                .SelectMany(a => a.Diagnosticos)
                .GroupBy(d => d.NombreEnfermedad)
                .Select((g, i) => new PuntoGrafico(g.Key, g.Count(), Paleta.Color(i + 1)))
                .OrderByDescending(p => p.Valor)
                .Take(cantidad)
                .ToList();
        }

        // atencion_diagnostico.nivel_gravedad
        public static List<PuntoGrafico> DiagnosticosPorGravedad() {
            List<AtencionDiagnosticoViewModel> diagnosticos = DatosEjemplo.Atenciones().SelectMany(a => a.Diagnosticos).ToList();
            return Enum.GetValues<NivelGravedad>()
                .Select(n => new PuntoGrafico(TextoEnum.De(n), diagnosticos.Count(d => d.NivelGravedad == n), ColorDe(n)))
                .ToList();
        }

        // insumo_utilizado.cantidad_utilizada agrupado por artículo
        public static List<PuntoGrafico> InsumosMasUtilizados(int cantidad = 5) {
            return DatosEjemplo.Atenciones()
                .SelectMany(a => a.Insumos)
                .GroupBy(i => i.NombreArticulo)
                .Select(g => new PuntoGrafico(g.Key, g.Sum(i => i.CantidadUtilizada), Paleta.Cielo,
                    g.Any(i => i.EsCobrable) ? "cobrable" : "incluido"))
                .OrderByDescending(p => p.Valor)
                .Take(cantidad)
                .ToList();
        }

        // ------------------------------------------------------------------ Cliente

        public record ResumenCliente(int Mascotas, int CitasProximas, int Atenciones, double TotalPagado);

        public static ResumenCliente Cliente(int idCliente) {
            List<MascotaViewModel> mascotas = DatosEjemplo.MascotasDeCliente(idCliente);
            HashSet<int> idsMascota = mascotas.Select(m => m.Id).ToHashSet();
            return new ResumenCliente(
                Mascotas: mascotas.Count,
                CitasProximas: ProximasCitas(idCliente).Count,
                Atenciones: DatosEjemplo.Atenciones().Count(a => idsMascota.Contains(a.IdMascota)),
                TotalPagado: DatosEjemplo.Boletas().Where(b => b.IsActivo && b.IdCliente == idCliente).Sum(b => b.Total));
        }

        // cita.fecha_hora >= hoy y todavía vigente
        public static List<CitaViewModel> ProximasCitas(int idCliente) {
            return DatosEjemplo.CitasDeCliente(idCliente)
                .Where(c => c.FechaHora >= Hoy && (c.Estado == EstadoCita.PENDIENTE || c.Estado == EstadoCita.APROBADO))
                .OrderBy(c => c.FechaHora)
                .ToList();
        }

        // boleta del cliente: total de cada una en orden cronológico
        public static List<PuntoGrafico> PagosDeCliente(int idCliente) {
            return DatosEjemplo.Boletas()
                .Where(b => b.IsActivo && b.IdCliente == idCliente)
                .OrderBy(b => b.Fecha)
                .Select((b, i) => new PuntoGrafico(b.Fecha.ToString("dd/MM"), b.Total, Paleta.Color(i), b.SerieCorrelativo))
                .ToList();
        }

        // detalle_boleta_serv vs detalle_boleta_art (insumos de atenciones y artículos de caja)
        public static List<PuntoGrafico> GastoPorConcepto(int idCliente) {
            List<LineaBoletaViewModel> lineas = DatosEjemplo.Boletas()
                .Where(b => b.IsActivo && b.IdCliente == idCliente)
                .SelectMany(b => b.Lineas)
                .ToList();
            return [
                new PuntoGrafico("Servicios", lineas.Where(l => l.Tipo == "Servicio").Sum(l => l.SubTotal), Paleta.Turquesa),
                new PuntoGrafico("Insumos", lineas.Where(l => l.Tipo == "Insumo").Sum(l => l.SubTotal), Paleta.Cielo),
                new PuntoGrafico("Artículos", lineas.Where(l => l.Tipo == "Artículo").Sum(l => l.SubTotal), Paleta.Coral)
            ];
        }

        // mascota.peso de cada mascota del cliente
        public static List<PuntoGrafico> PesoDeMascotas(int idCliente) {
            return DatosEjemplo.MascotasDeCliente(idCliente)
                .Select(m => new PuntoGrafico(m.Nombre, m.Peso ?? 0, Components.Ilustraciones.Pelaje.De(m).Oscuro, m.Raza))
                .ToList();
        }

        // atencion por mascota, para el resumen del historial
        public static int AtencionesDeMascota(int idMascota) => DatosEjemplo.Atenciones().Count(a => a.IdMascota == idMascota);

        public static string Edad(DateOnly? fechaNacimiento) {
            if (fechaNacimiento == null) {
                return "—";
            }
            DateOnly hoy = DateOnly.FromDateTime(Hoy);
            int meses = (hoy.Year - fechaNacimiento.Value.Year) * 12 + hoy.Month - fechaNacimiento.Value.Month;
            if (hoy.Day < fechaNacimiento.Value.Day) {
                meses--;
            }
            if (meses < 12) {
                return $"{meses} {(meses == 1 ? "mes" : "meses")}";
            }
            int anios = meses / 12;
            return $"{anios} {(anios == 1 ? "año" : "años")}";
        }
    }
}
