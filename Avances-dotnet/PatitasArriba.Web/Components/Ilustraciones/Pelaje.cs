using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Web.Components.Ilustraciones {
    // Colores con los que se dibuja una mascota en las ilustraciones.
    // La tabla mascota no guarda el color, así que se deduce de la raza
    // (y, si no se reconoce, del id para que cada mascota tenga siempre el mismo).
    public record Pelaje(string Base, string Claro, string Oscuro, string Collar, bool Manchas = false, bool Rayas = false) {

        // Perros
        public static readonly Pelaje Dorado = new("#E7A955", "#FBE3BD", "#B9772F", "#E85D75");
        public static readonly Pelaje Chocolate = new("#8B5A3C", "#D9B08C", "#5E3923", "#4EA8DE");
        public static readonly Pelaje Negro = new("#3F3D56", "#B8B5CC", "#24233A", "#F6BD60");
        public static readonly Pelaje Dalmata = new("#F7F3EE", "#FFFFFF", "#3F3D56", "#2A9D8F", Manchas: true);
        public static readonly Pelaje GrisPlata = new("#9AA5B1", "#E4E8EC", "#5F6B78", "#FF7A59");
        public static readonly Pelaje Canela = new("#C97B4A", "#F3D2B3", "#8E4E26", "#7B5EA7", Manchas: true);

        // Gatos
        public static readonly Pelaje Naranja = new("#F4A259", "#FDE2C4", "#C8702A", "#2A9D8F", Rayas: true);
        public static readonly Pelaje Ceniza = new("#8D99AE", "#DDE3EA", "#5A6478", "#F6BD60", Rayas: true);
        public static readonly Pelaje Azabache = new("#34324A", "#77738F", "#1E1D2C", "#E85D75");
        public static readonly Pelaje Siames = new("#F1E3CF", "#FFF6EA", "#6B4A3A", "#4EA8DE");
        public static readonly Pelaje Nieve = new("#FAFAFA", "#FFFFFF", "#C9CED6", "#FF7A59");

        private static readonly Pelaje[] PerrosVarios = [Dorado, Chocolate, Negro, Dalmata, GrisPlata, Canela];
        private static readonly Pelaje[] GatosVarios = [Naranja, Ceniza, Azabache, Siames, Nieve];

        public static Pelaje De(MascotaViewModel mascota) {
            string raza = (mascota.Raza ?? "").ToLowerInvariant();
            if (mascota.TipoMascota == TipoMascota.GATO) {
                if (raza.Contains("siam")) return Siames;
                if (raza.Contains("persa") || raza.Contains("angora")) return Nieve;
                return GatosVarios[mascota.Id % GatosVarios.Length];
            }
            if (raza.Contains("labrador") || raza.Contains("golden")) return Dorado;
            if (raza.Contains("schnauzer")) return GrisPlata;
            if (raza.Contains("shih") || raza.Contains("beagle")) return Canela;
            if (raza.Contains("dálmata") || raza.Contains("dalmata")) return Dalmata;
            if (raza.Contains("pug") || raza.Contains("chihuahua")) return Chocolate;
            return PerrosVarios[mascota.Id % PerrosVarios.Length];
        }
    }
}
