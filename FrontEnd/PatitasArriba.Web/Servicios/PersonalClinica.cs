using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Servicios;

// Horarios, veterinarios y administradores cargados juntos desde el backend.
// En Java la asignación de horarios vive en la persona (VeterinarioDTO.horarios, AdministradorDTO.horarios):
// GET /horarios no dice quién atiende en cada uno, así que aquí se completa cruzando las tres listas.
public class PersonalClinica {
    public List<HorarioViewModel> Horarios { get; private init; } = [];
    public List<VeterinarioViewModel> Veterinarios { get; private init; } = [];
    public List<AdministradorViewModel> Administradores { get; private init; } = [];

    // GET /horarios, /veterinarios y /administradores en paralelo
    public static async Task<PersonalClinica> Cargar(IHorarioService horarioService,
            IVeterinarioService veterinarioService, IAdministradorService administradorService) {
        Task<List<HorarioViewModel>> horarios = horarioService.FindAll();
        Task<List<VeterinarioViewModel>> veterinarios = veterinarioService.FindAll();
        Task<List<AdministradorViewModel>> administradores = administradorService.FindAll();
        PersonalClinica personal = new() {
            Horarios = (await horarios).OrderBy(h => h.DiaSemana).ThenBy(h => h.HoraInicio).ToList(),
            Veterinarios = await veterinarios,
            Administradores = await administradores
        };
        personal.CompletarNombres();
        personal.CompletarTextos();
        return personal;
    }

    // Llena NombresPersonal / NombresVeterinarios / NombresAdministradores de cada horario
    private void CompletarNombres() {
        foreach (HorarioViewModel horario in Horarios) {
            horario.NombresVeterinarios = Veterinarios.Where(v => Tiene(v, horario.Id)).Select(v => v.NombreCompleto).ToList();
            horario.NombresAdministradores = Administradores.Where(a => Tiene(a, horario.Id)).Select(a => a.NombreCompleto).ToList();
            horario.NombresPersonal = [.. horario.NombresVeterinarios, .. horario.NombresAdministradores];
        }
    }

    // Las casillas de horario de cada persona con el mismo texto que el formulario ("Lunes 08:00 - 14:00")
    private void CompletarTextos() {
        foreach (PersonalViewModel persona in Personas) {
            persona.Horarios = OpcionesHorario(persona.Horarios);
        }
    }

    public IEnumerable<PersonalViewModel> Personas => Veterinarios.Cast<PersonalViewModel>().Concat(Administradores);

    public static bool Tiene(PersonalViewModel persona, int idHorario) =>
        persona.Horarios.Any(h => h.Seleccionado && h.Id == idHorario);

    // Los horarios completos (día y horas) asignados a una persona
    public List<HorarioViewModel> HorariosDe(PersonalViewModel persona) =>
        Horarios.Where(h => Tiene(persona, h.Id)).ToList();

    public static string Texto(HorarioViewModel horario) => $"{TextoEnum.De(horario.DiaSemana)} {horario.Rango}";

    // Todos los horarios como casillas, marcando los asignados
    public List<OpcionSeleccion> OpcionesHorario(IEnumerable<OpcionSeleccion> asignados) =>
        OpcionesHorario(Horarios, asignados);

    public static List<OpcionSeleccion> OpcionesHorario(IEnumerable<HorarioViewModel> horarios, IEnumerable<OpcionSeleccion> asignados) {
        HashSet<int> marcados = asignados.Where(h => h.Seleccionado).Select(h => h.Id).ToHashSet();
        return horarios
            .OrderBy(h => h.DiaSemana).ThenBy(h => h.HoraInicio)
            .Select(h => new OpcionSeleccion { Id = h.Id, Texto = Texto(h), Seleccionado = marcados.Contains(h.Id) })
            .ToList();
    }
}
