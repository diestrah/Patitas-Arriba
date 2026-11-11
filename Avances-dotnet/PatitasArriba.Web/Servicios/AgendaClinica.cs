using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

// Agenda de la clínica armada con las citas del backend, con la misma regla que CitaBLImpl (RN-CI-05 y RN-CI-06):
// los servicios de cada veterinario van uno tras otro desde la hora de la cita, cada uno con la duración estimada
// del servicio (30 minutos si no la tiene). Si una cita tiene dos veterinarios, ambos empiezan a la hora de la cita.
// Las citas rechazadas y canceladas no ocupan agenda.
public static class AgendaClinica {
    public const int DuracionPorDefecto = 30;

    public static int Duracion(ServicioViewModel? servicio) => servicio?.DuracionEstimada ?? DuracionPorDefecto;

    public static bool OcupaAgenda(CitaViewModel cita) =>
        cita.Estado != EstadoCita.RECHAZADO && cita.Estado != EstadoCita.CANCELADO;

    // Un elemento por cada servicio de cada cita del día. servicios = GET /servicios (para la duración y el tipo)
    public static List<ItemAgendaViewModel> DelDia(DateTime fecha, IEnumerable<CitaViewModel> citas, IEnumerable<ServicioViewModel> servicios) {
        Dictionary<int, ServicioViewModel> porId = servicios.ToDictionary(s => s.Id);
        List<ItemAgendaViewModel> agenda = [];
        foreach (CitaViewModel cita in citas) {
            if (cita.FechaHora?.Date != fecha.Date || !OcupaAgenda(cita)) {
                continue;
            }
            DateTime inicioCita = cita.FechaHora.Value;
            Dictionary<int, DateTime> siguientePorVeterinario = [];
            foreach (DetalleCitaViewModel detalle in cita.Detalles) {
                ServicioViewModel? servicio = porId.GetValueOrDefault(detalle.IdServicio);
                DateTime inicio = siguientePorVeterinario.GetValueOrDefault(detalle.IdVeterinario, inicioCita);
                int duracion = Duracion(servicio);
                siguientePorVeterinario[detalle.IdVeterinario] = inicio.AddMinutes(duracion);
                agenda.Add(new ItemAgendaViewModel {
                    IdCita = cita.Id,
                    IdDetalleCita = detalle.Id,
                    IdMascota = cita.IdMascota,
                    FechaHora = inicio,
                    DuracionMinutos = duracion,
                    NombreMascota = cita.NombreMascota,
                    NombreCliente = cita.NombreCliente,
                    NombreServicio = servicio?.Nombre ?? detalle.NombreServicio,
                    TipoServicio = servicio?.TipoServicio ?? default,
                    IdVeterinario = detalle.IdVeterinario,
                    NombreVeterinario = detalle.NombreVeterinario,
                    EsEmergencia = cita.EsEmergencia,
                    EstadoCita = cita.Estado
                });
            }
        }
        return agenda.OrderBy(i => i.FechaHora).ToList();
    }

    // Minutos que cada veterinario ocupa en una cita (CitaBLImpl.minutosPorVeterinario)
    public static Dictionary<int, int> MinutosPorVeterinario(CitaViewModel cita, IEnumerable<ServicioViewModel> servicios) {
        Dictionary<int, ServicioViewModel> porId = servicios.ToDictionary(s => s.Id);
        Dictionary<int, int> minutos = [];
        foreach (DetalleCitaViewModel detalle in cita.Detalles) {
            minutos[detalle.IdVeterinario] = minutos.GetValueOrDefault(detalle.IdVeterinario)
                + Duracion(porId.GetValueOrDefault(detalle.IdServicio));
        }
        return minutos;
    }
}
