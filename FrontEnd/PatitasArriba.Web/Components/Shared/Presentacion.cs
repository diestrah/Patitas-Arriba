namespace PatitasArriba.Web.Components.Shared {
    // Ayudas de presentación para las tarjetas: iniciales y un color estable para el avatar de una persona
    public static class Presentacion {
        public static string Iniciales(string nombre) {
            string[] partes = nombre.Split(' ', StringSplitOptions.RemoveEmptyEntries);
            if (partes.Length == 0) {
                return "?";
            }
            return partes.Length == 1 ? partes[0][..1].ToUpper() : $"{partes[0][..1]}{partes[1][..1]}".ToUpper();
        }

        // Cinco colores suaves; el mismo nombre siempre recibe el mismo
        public static string ColorAvatar(string nombre) {
            int suma = 0;
            foreach (char c in nombre) {
                suma += c;
            }
            return $"av-c{suma % 5}";
        }
    }
}
