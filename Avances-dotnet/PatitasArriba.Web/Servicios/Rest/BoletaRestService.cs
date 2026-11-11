using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Rest;

public class BoletaRestService : RegistroRestService<BoletaViewModel, BoletaDTO>, IBoletaService {
    public BoletaRestService(IConfiguration configuration) : base(configuration, "boletas") { }
    protected override BoletaViewModel ToViewModel(BoletaDTO dto) => RestMapper.ToViewModel(dto);
    protected override BoletaDTO ToDto(BoletaViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(BoletaViewModel modelo) => modelo.Id;
    protected override void SetId(BoletaViewModel modelo, BoletaDTO dto) {
        modelo.Id = dto.Id;
        if (!string.IsNullOrEmpty(dto.SerieCorrelativo)) modelo.SerieCorrelativo = dto.SerieCorrelativo;
    }
    // El número (B001-000123) lo asigna Java al registrar la boleta (RN-BO-03): así no se repite
    // aunque haya boletas anuladas. Se envía vacío y se copia el que devuelve el backend.
    public override async Task Insert(BoletaViewModel modelo) {
        var dto = ToDto(modelo);
        dto.SerieCorrelativo = null;
        var creada = await HttpUtils.DoPost<BoletaDTO, BoletaDTO>(RecursoUrl, dto);
        SetId(modelo, creada);
    }
}
