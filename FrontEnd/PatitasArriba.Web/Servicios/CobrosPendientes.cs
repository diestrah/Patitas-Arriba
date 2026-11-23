using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

// Lo que un cliente aún no ha pagado: los servicios de sus citas que ya se atendieron (RN-BO-07) y los insumos
// cobrables de esas atenciones (RN-BO-08). Lo decide el backend (GET /boletas/pendientes?cliente={id}): algo que
// ya estuvo en una boleta no vuelve a quedar pendiente, aunque esa boleta se haya anulado o la línea se haya quitado.
// GET /citas?cliente={id} solo sirve para poner el nombre de la mascota en cada línea. Los precios mostrados son los
// del catálogo; al emitir, el backend los vuelve a fijar (RN-BO-05).
public static class CobrosPendientes {
    public static async Task<List<LineaBoletaViewModel>> DelCliente(int idCliente, ICitaService citaService,
            IBoletaService boletaService) {
        Task<List<CitaViewModel>> citas = citaService.FindByCliente(idCliente);
        Task<List<LineaPendiente>> pendientes = boletaService.Pendientes(idCliente);

        // Servicio de cita (detalle_cita) -> nombre de su mascota
        Dictionary<int, string> mascotas = [];
        foreach (CitaViewModel cita in await citas) {
            foreach (DetalleCitaViewModel detalle in cita.Detalles) {
                mascotas[detalle.Id] = cita.NombreMascota;
            }
        }

        List<LineaBoletaViewModel> lineas = [];
        foreach (LineaPendiente pendiente in await pendientes) {
            if (mascotas.TryGetValue(pendiente.IdDetalleCita, out string? mascota)) {
                pendiente.Linea.Descripcion = $"{pendiente.Linea.Descripcion} · {mascota}";
            }
            lineas.Add(pendiente.Linea);
        }
        return lineas;
    }
}
