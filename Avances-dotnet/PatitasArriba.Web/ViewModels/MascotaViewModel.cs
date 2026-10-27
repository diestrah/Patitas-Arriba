using System.ComponentModel.DataAnnotations;

namespace PatitasArriba.Web.ViewModels {
    public class MascotaViewModel {
        public int Id { get; set; }

        [Range(1, int.MaxValue, ErrorMessage = "Seleccione el dueño de la mascota")]
        public int IdCliente { get; set; }

        public string NombreDueno { get; set; } = "";

        [Required(ErrorMessage = "El nombre es obligatorio")]
        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string Nombre { get; set; } = "";

        public TipoMascota TipoMascota { get; set; }

        [StringLength(45, ErrorMessage = "Máximo 45 caracteres")]
        public string? Raza { get; set; }

        public SexoMascota Sexo { get; set; }

        [Range(0.1, 150, ErrorMessage = "El peso debe estar entre 0.1 y 150 kg")]
        public double? Peso { get; set; }

        [Required(ErrorMessage = "La fecha de nacimiento es obligatoria")]
        public DateOnly? FechaNacimiento { get; set; }
    }
}
