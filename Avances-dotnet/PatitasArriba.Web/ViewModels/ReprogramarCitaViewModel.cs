using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    // Nueva fecha y hora de una cita que el cliente reprograma (la mascota y los servicios no cambian)
    public class ReprogramarCitaViewModel : IValidatableObject {
        [Required(ErrorMessage = "Indique la fecha y hora de la cita")]
        public DateTime? FechaHora { get; set; }

        // RN-CI-03: la fecha y hora no puede estar en el pasado
        public IEnumerable<ValidationResult> Validate(ValidationContext validationContext) {
            if (FechaHora != null && FechaHora < DateTime.Now) {
                yield return new ValidationResult("La fecha y hora no puede estar en el pasado", [nameof(FechaHora)]);
            }
        }
    }
}
