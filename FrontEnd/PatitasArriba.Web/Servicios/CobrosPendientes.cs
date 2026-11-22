using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

// Lo que un cliente aún no ha pagado: los servicios de sus citas que ya se atendieron (RN-BO-07) y los insumos
// cobrables de esas atenciones (RN-BO-08) que no aparecen en ninguna boleta activa. El backend no tiene una
// consulta de pendientes, así que se arma con GET /citas?cliente={id}, GET /atenciones, GET /boletas y
// GET /servicios. Los precios mostrados son los del catálogo; al emitir, el backend los vuelve a fijar (RN-BO-05).
public static class CobrosPendientes {
    public static async Task<List<LineaBoletaViewModel>> DelCliente(int idCliente, ICitaService citaService,
            IAtencionService atencionService, IBoletaService boletaService, IServicioService servicioService) {
        Task<List<CitaViewModel>> citas = citaService.FindByCliente(idCliente);
        Task<List<AtencionViewModel>> atenciones = atencionService.FindAll();
        Task<List<BoletaViewModel>> boletas = boletaService.FindAll();
        Task<List<ServicioViewModel>> servicios = servicioService.FindAll();

        // Servicio de cita (detalle_cita) -> su cita, solo de las citas de este cliente
        Dictionary<int, (CitaViewModel Cita, DetalleCitaViewModel Detalle)> detalles = [];
        foreach (CitaViewModel cita in await citas) {
            foreach (DetalleCitaViewModel detalle in cita.Detalles) {
                detalles[detalle.Id] = (cita, detalle);
            }
        }
        List<LineaBoletaViewModel> cobradas = (await boletas).Where(b => b.IsActivo).SelectMany(b => b.Lineas).ToList();
        HashSet<int> serviciosCobrados = cobradas.Where(l => l.IdDetalleCita != null).Select(l => l.IdDetalleCita!.Value).ToHashSet();
        HashSet<int> insumosCobrados = cobradas.Where(l => l.IdInsumoUtilizado != null).Select(l => l.IdInsumoUtilizado!.Value).ToHashSet();
        Dictionary<int, ServicioViewModel> catalogo = (await servicios).ToDictionary(s => s.Id);

        List<LineaBoletaViewModel> pendientes = [];
        foreach (AtencionViewModel atencion in (await atenciones).OrderBy(a => a.FechaHora)) {
            if (!detalles.TryGetValue(atencion.IdDetalleCita, out var origen)) {
                continue;
            }
            string mascota = origen.Cita.NombreMascota;
            if (!serviciosCobrados.Contains(atencion.IdDetalleCita)) {
                ServicioViewModel? servicio = catalogo.GetValueOrDefault(origen.Detalle.IdServicio);
                pendientes.Add(new LineaBoletaViewModel {
                    Tipo = "Servicio",
                    IdDetalleCita = atencion.IdDetalleCita,
                    Descripcion = $"{servicio?.Nombre ?? origen.Detalle.NombreServicio} · {mascota}",
                    Cantidad = 1,
                    PrecioUnitario = servicio?.PrecioBase ?? 0
                });
            }
            foreach (InsumoUtilizadoViewModel insumo in atencion.Insumos.Where(i => i.EsCobrable && !insumosCobrados.Contains(i.Id))) {
                pendientes.Add(new LineaBoletaViewModel {
                    Tipo = "Insumo",
                    IdArticulo = insumo.IdArticulo,
                    IdInsumoUtilizado = insumo.Id,
                    Descripcion = $"{insumo.NombreArticulo} · {mascota}",
                    Cantidad = insumo.CantidadUtilizada,
                    PrecioUnitario = insumo.PrecioUnitario
                });
            }
        }
        return pendientes;
    }
}
