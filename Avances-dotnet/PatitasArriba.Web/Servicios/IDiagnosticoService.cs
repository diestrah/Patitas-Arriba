using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

public interface IDiagnosticoService : IRegistroService<DiagnosticoViewModel>{
    Task<DiagnosticoViewModel> FindByNombreEnfermedad(string NombreEnfermedad);
}

