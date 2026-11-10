using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class HorarioViewModel : IValidatableObject {
        public int Id { get; set; }
        public DiaSemana DiaSemana { get; set; }
        public TimeOnly HoraInicio { get; set; } = new TimeOnly(8, 0);
        public TimeOnly HoraFin { get; set; } = new TimeOnly(14, 0);

        // Solo para mostrar: el personal se asigna en la ficha de cada veterinario o administrador
        public List<string> NombresPersonal { get; set; } = [];

        public string Rango => $"{HoraInicio:HH:mm} - {HoraFin:HH:mm}";
        public string PersonalAsignado => string.Join(", ", NombresPersonal);

        // RN-HO-02: la hora de inicio debe ser anterior a la hora de fin
        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            if (HoraInicio >= HoraFin) {
                yield return new ValidationResult("La hora de fin debe ser posterior a la hora de inicio", [nameof(HoraFin)]);
            }
        }
    }
}
