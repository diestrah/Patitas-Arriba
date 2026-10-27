using Microsoft.Playwright;
using Microsoft.Playwright.NUnit;

namespace PatitasArriba.Tests;

// Pruebas de interfaz (Lab 9): requieren que el front este corriendo en BaseUrl
//   cd PatitasArriba.Web ; dotnet run --launch-profile http
// La sesion del front es por circuito Blazor: despues de iniciar sesion hay que navegar con el menu,
// no con GotoAsync (una URL directa abre un circuito nuevo y pierde el rol).
[TestFixture]
public class PruebasInterfaz : PageTest
{
    private const string BaseUrl = "http://localhost:5118";

    // Inicia sesion eligiendo el rol. Blazor Server tarda un instante en enlazar los eventos,
    // por eso se reintenta si todavia no aparece el saludo.
    private async Task IniciarSesionAsync(string rol)
    {
        for (int intento = 0; intento < 3; intento++)
        {
            await Page.GotoAsync($"{BaseUrl}/login");
            await Page.WaitForLoadStateAsync(LoadState.NetworkIdle);

            await Page.Locator("input.form-control").First.FillAsync("usuario.demo");
            await Page.Locator("input[type=password]").FillAsync("clave123");
            await Page.Locator("select.form-select").SelectOptionAsync(rol);
            await Page.GetByRole(AriaRole.Button, new() { Name = "Ingresar", Exact = true }).ClickAsync();

            try
            {
                await Expect(Page.GetByText("Bienvenido,")).ToBeVisibleAsync(new() { Timeout = 4000 });
                return;
            }
            catch (Exception)
            {
                // reintentar con una pagina nueva
            }
        }
        throw new AssertionException($"No se pudo iniciar sesion como {rol}");
    }

    private ILocator GrupoDelMenu(string nombre) =>
        Page.Locator("nav button.dropdown-toggle", new() { HasTextString = nombre });

    // RF01: cada rol ve solo su menu
    [Test]
    public async Task Administrador_ve_los_menus_de_gestion()
    {
        await IniciarSesionAsync("ADMINISTRADOR");

        await Expect(GrupoDelMenu("Clientes")).ToBeVisibleAsync();
        await Expect(GrupoDelMenu("Citas")).ToBeVisibleAsync();
        await Expect(GrupoDelMenu("Personal")).ToBeVisibleAsync();
        await Expect(GrupoDelMenu("Mi portal")).ToHaveCountAsync(0);
    }

    [Test]
    public async Task Cliente_solo_ve_su_portal()
    {
        await IniciarSesionAsync("CLIENTE");

        await Expect(GrupoDelMenu("Mi portal")).ToBeVisibleAsync();
        await Expect(GrupoDelMenu("Personal")).ToHaveCountAsync(0);
        await Expect(GrupoDelMenu("Caja")).ToHaveCountAsync(0);
    }

    // RF10: la reserva vacia se rechaza con mensajes de validacion
    [Test]
    public async Task Reserva_sin_datos_muestra_validaciones()
    {
        await IniciarSesionAsync("CLIENTE");

        await GrupoDelMenu("Mi portal").ClickAsync();
        await Page.GetByRole(AriaRole.Link, new() { Name = "Reservar cita" }).ClickAsync();
        await Expect(Page.GetByRole(AriaRole.Heading, new() { Name = "Reservar cita" })).ToBeVisibleAsync();

        await Page.GetByRole(AriaRole.Button, new() { Name = "Reservar", Exact = true }).ClickAsync();

        await Expect(Page.GetByText("Seleccione la mascota").First).ToBeVisibleAsync();
    }

    // RF08: el administrador consulta el inventario
    [Test]
    public async Task Administrador_ve_el_inventario_de_articulos()
    {
        await IniciarSesionAsync("ADMINISTRADOR");

        await GrupoDelMenu("Inventario").ClickAsync();
        await Page.GetByRole(AriaRole.Link, new() { Name = "Artículos" }).ClickAsync();

        await Expect(Page.GetByRole(AriaRole.Heading, new() { Name = "Inventario de artículos" })).ToBeVisibleAsync();
        await Expect(Page.Locator("table tbody tr").First).ToBeVisibleAsync();
    }
}
