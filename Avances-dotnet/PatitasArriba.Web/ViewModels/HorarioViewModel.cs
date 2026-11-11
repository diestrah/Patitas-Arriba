using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class HorarioViewModel : IValidatableObject {
        public int Id { get; set; }
        public DiaSemana DiaSemana { get; set; }
        public TimeOnly HoraInicio { get; set; } = new TimeOnly(8, 0);
        public TimeOnly HoraFin { get; set; } = new TimeOnly(14, 0);

        // Solo para mostrar: el personal se asigna en la ficha de cada veterinario o administrador
        public List<string> NombresPersonal { get; set; } = [];

        // Lo mismo separado por tipo de personal (la pantalla distingue veterinarios y administradores)
        public List<string> NombresVeterinarios { get; set; } = [];
        public List<string> NombresAdministradores { get; set; } = [];

        public string Rango => $"{HoraInicio:HH:mm} - {HoraFin:HH:mm}";
        public double Horas => (HoraFin - HoraInicio).TotalHours;
        public bool EsTarde => HoraInicio >= new TimeOnly(12, 0);
        // Una franja sin veterinario no puede recibir citas (RN-CI-05)
        public bool TieneVeterinario => NombresVeterinarios.Count > 0;
        public string PersonalAsignado => string.Join(", ", NombresPersonal);

        // RN-HO-02: la hora de inicio debe ser anterior a la hora de fin
        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            if (HoraInicio >= HoraFin) {
                yield return new ValidationResult("La hora de fin debe ser posterior a la hora de inicio", [nameof(HoraFin)]);
            }
        }
    }
}
