using System.Net;
using PatitasArriba.Web.DTOs;
using PatitasArriba.Web.ViewModels;
using PatitasArriba.Web.Servicios.Rest;

namespace PatitasArriba.Web.Servicios.Reste;

public class DiagnosticoRestService : RegistroRestService<DiagnosticoViewModel, DiagnosticoDTO>, IDiagnosticoService {
    public DiagnosticoRestService(IConfiguration configuration) : base(configuration, "diagnosticos") { }
    protected override DiagnosticoViewModel ToViewModel(DiagnosticoDTO dto) => RestMapper.ToViewModel(dto);
    protected override DiagnosticoDTO ToDto(DiagnosticoViewModel modelo) => RestMapper.ToDto(modelo);
    protected override int GetId(DiagnosticoViewModel modelo) => modelo.Id;
    protected override void SetId(DiagnosticoViewModel modelo, DiagnosticoDTO dto) => modelo.Id = dto.Id;
    public Task<DiagnosticoViewModel> FindByNombreEnfermedad(string valor) => FindOne("nombreEnfermedad", valor);
}
