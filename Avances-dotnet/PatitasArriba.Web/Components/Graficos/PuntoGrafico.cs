using System.Globalization;

namespace PatitasArriba.Web.Components.Graficos {
    // Un valor de un gráfico: su etiqueta, el número y el color con el que se pinta
    public record PuntoGrafico(string Etiqueta, double Valor, string Color, string? Detalle = null);

    public static class Paleta {
        public const string Turquesa = "#2A9D8F";
        public const string Coral = "#FF7A59";
        public const string Sol = "#F6BD60";
        public const string Uva = "#7B5EA7";
        public const string Cielo = "#4EA8DE";
        public const string Rosa = "#E85D75";
        public const string Oliva = "#8AB17D";
        public const string Gris = "#A8A29E";

        public static readonly string[] Serie = [Turquesa, Coral, Sol, Uva, Cielo, Rosa, Oliva, Gris];

        public static string Color(int indice) => Serie[indice % Serie.Length];

        // Los números del SVG siempre con punto decimal, sin importar la cultura del servidor
        public static string Svg(double valor) => valor.ToString("0.###", CultureInfo.InvariantCulture);

        public static string Soles(double valor) => "S/ " + valor.ToString("N2", CultureInfo.GetCultureInfo("es-PE"));
    }
}
