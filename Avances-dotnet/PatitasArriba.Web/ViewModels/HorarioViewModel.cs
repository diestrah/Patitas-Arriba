namespace PatitasArriba.Web.ViewModels {
    public class HorarioViewModel {
        public int Id { get; set; }
        public DiaSemana DiaSemana { get; set; }
        public TimeOnly HoraInicio { get; set; } = new TimeOnly(8, 0);
        public TimeOnly HoraFin { get; set; } = new TimeOnly(14, 0);

        // Veterinarios y administradores asignados al turno
        public List<OpcionSeleccion> Personal { get; set; } = [];

        public string Rango => $"{HoraInicio:HH:mm} - {HoraFin:HH:mm}";
        public string PersonalAsignado => string.Join(", ", Personal.Where(p => p.Seleccionado).Select(p => p.Texto));
    }
}
