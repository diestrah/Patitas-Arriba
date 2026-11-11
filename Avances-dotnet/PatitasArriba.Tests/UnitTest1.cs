using Microsoft.Playwright;
using Microsoft.Playwright.NUnit;

namespace PatitasArriba.Tests;

// Pruebas de interfaz (Lab 9): requieren que el front este corriendo en BaseUrl y el backend desplegado
//   cd PatitasArriba.Web ; dotnet run --launch-profile http
// La sesion vive en una cookie (Seguridad/SeguridadExtensions.cs): sobrevive a recargar la pagina y
// a abrir una URL directa, y cada pagina exige el rol de su permiso (Permisos.RolesPorPermiso).
[TestFixture]
public class PruebasInterfaz : PageTest
{
    private const string BaseUrl = "http://localhost:5118";

    // Cuentas de Avances/sql/datos-prueba.sql (el login es real: requiere el backend en GlassFish con esos datos).
    // El rol no se elige, se deduce de la cuenta
    private const string ClavePrueba = "Patitas123";
    private static string UsuarioDelRol(string rol) => rol switch
    {
        "ADMINISTRADOR" => "rosa.admin@patitas.pe",
        "VETERINARIO" => "dra.lucia@patitas.pe",
        _ => "ana.torres@gmail.com"
    };

    // Inicia sesion con la cuenta del rol. Blazor Server tarda un instante en enlazar los eventos,
    // por eso se reintenta si todavia no aparece el usuario en la barra superior.
    private async Task IniciarSesionAsync(string rol)
    {
        for (int intento = 0; intento < 3; intento++)
        {
            await Page.GotoAsync($"{BaseUrl}/login");
            await Page.WaitForLoadStateAsync(LoadState.NetworkIdle);

            await Page.Locator("#correo").FillAsync(UsuarioDelRol(rol));
            await Page.Locator("#clave").FillAsync(ClavePrueba);
            await Page.GetByRole(AriaRole.Button, new() { Name = "Ingresar", Exact = true }).ClickAsync();

            try
            {
                await Expect(Page.Locator(".usuario-barra")).ToBeVisibleAsync(new() { Timeout = 15000 });
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

    // Seguridad: sin sesion, una pagina protegida manda al login
    [Test]
    public async Task Sin_sesion_una_pagina_protegida_lleva_al_login()
    {
        await Page.GotoAsync($"{BaseUrl}/clientes");

        await Expect(Page).ToHaveURLAsync(new System.Text.RegularExpressions.Regex("/login"));
    }

    // Seguridad: el cliente no entra por URL a pantallas de otros roles
    [TestCase("/clientes")]
    [TestCase("/boletas")]
    [TestCase("/cuentas-usuario")]
    public async Task Cliente_no_entra_a_pantallas_de_gestion(string ruta)
    {
        await IniciarSesionAsync("CLIENTE");

        await Page.GotoAsync($"{BaseUrl}{ruta}");

        await Expect(Page.GetByText("No tiene permisos para acceder a esta página.")).ToBeVisibleAsync();
    }

    // Seguridad: recargar la pagina no pierde la sesion
    [Test]
    public async Task Recargar_la_pagina_mantiene_la_sesion()
    {
        await IniciarSesionAsync("ADMINISTRADOR");

        await Page.ReloadAsync();

        await Expect(Page.Locator(".usuario-barra")).ToBeVisibleAsync();
        await Expect(GrupoDelMenu("Personal")).ToBeVisibleAsync();
    }
}
