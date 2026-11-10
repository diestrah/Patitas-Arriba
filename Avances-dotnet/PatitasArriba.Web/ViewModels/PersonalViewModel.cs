namespace PatitasArriba.Web.ViewModels {
    // Datos comunes de veterinarios y administradores (en Java: clase Personal).
    // Los horarios se asignan en la ficha de cada persona, igual que en el backend (VeterinarioDTO.horarios).
    public class PersonalViewModel : PersonaViewModel {
        // Todos los horarios como casillas, marcados los que tiene asignados
        public List<OpcionSeleccion> Horarios { get; set; } = [];

        public string TextoHorarios => string.Join(", ", Horarios.Where(h => h.Seleccionado).Select(h => h.Texto));
    }
}
