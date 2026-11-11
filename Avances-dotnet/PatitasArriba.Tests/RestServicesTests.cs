using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using System.Net;
using System.Text.Json;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using PatitasArriba.Web.Servicios;
using PatitasArriba.Web.Servicios.Rest;
using PatitasArriba.Web.ViewModels;

namespace PatitasArriba.Tests;

// HTTP real contra un servidor local controlado: no necesita GlassFish ni modifica la BD.
[TestFixture]
[NonParallelizable]
public class RestServicesTests {
    private ApiStub api = null!;
    private ServiceProvider services = null!;
    private static readonly JsonSerializerOptions json = new(JsonSerializerDefaults.Web);
    private static T Read<T>(string text) => JsonSerializer.Deserialize<T>(text, json)!;

    [SetUp]
    public async Task Setup() {
        api = new ApiStub();
        await api.Start();
        var config = new ConfigurationBuilder().AddInMemoryCollection(new Dictionary<string, string?> {
            ["RestApiBaseUrl"] = api.Url + "/app/api/v1"
        }).Build();
        services = new ServiceCollection().AddSingleton<IConfiguration>(config)
            .AddPatitasRestServices().BuildServiceProvider();
    }
    [TearDown]
    public async Task Cleanup() { await api.DisposeAsync(); services.Dispose(); }

    [Test]
    public async Task CrudUsesCorrectRoutesBodiesAndGeneratedId() {
        api.Respond = (method, path, body) => {
            if (method == "POST") {
                Assert.That(path, Is.EqualTo("/app/api/v1/categorias"));
                Assert.That(Read<Dictionary<string, JsonElement>>(body)["nombre"].GetString(), Is.EqualTo("Alimentos"));
                return (201, "{\"id\":42,\"nombre\":\"Alimentos\",\"descripcion\":\"Comida\"}");
            }
            if (method == "PUT") {
                Assert.That(path, Does.EndWith("/categorias/42"));
                return (200, body);
            }
            if (method == "DELETE") return (204, "");
            return (200, "[{\"id\":42,\"nombre\":\"Alimentos\",\"descripcion\":\"Comida\"}]");
        };
        var service = services.GetRequiredService<ICategoriaService>();
        var modelo = new CategoriaViewModel { Nombre = "Alimentos", Descripcion = "Comida" };
        await service.Insert(modelo);
        Assert.That(modelo.Id, Is.EqualTo(42));
        await service.Update(modelo);
        Assert.That((await service.FindAll()).Single().Nombre, Is.EqualTo("Alimentos"));
        await service.Delete(42);
    }

    [Test]
    public async Task NotFoundIsNullButValidationErrorsKeepBackendMessageAndStatus() {
        api.Respond = (_, path, _) => path.EndsWith("/99")
            ? (404, "{\"error\":\"No existe\"}") : (400, "{\"error\":\"Categoría en uso\"}");
        var service = services.GetRequiredService<ICategoriaService>();
        Assert.That(await service.FindById(99), Is.Null);
        var error = Assert.ThrowsAsync<HttpRequestException>(async () => await service.Delete(2));
        Assert.That(error!.StatusCode, Is.EqualTo(HttpStatusCode.BadRequest));
        Assert.That(error.Message, Is.EqualTo("Categoría en uso"));
    }

    [Test]
    public async Task FiltersConsumeArraysAndEncodeQueryValues() {
        api.Respond = (_, path, _) => {
            Assert.That(path, Does.Contain("nombre=Ba%C3%B1o%20%26%20corte"));
            return (200, "[{\"id\":2,\"nombre\":\"Baño & corte\",\"tipo\":\"ESTETICA\",\"precioBase\":30,\"duracionEstimada\":45}]");
        };
        var item = await services.GetRequiredService<IServicioService>().FindByName("Baño & corte");
        Assert.That(item.TipoServicio, Is.EqualTo(TipoServicio.ESTETICA));
        Assert.That(item.DuracionEstimada, Is.EqualTo(45));
    }

    [Test]
    public async Task PetsMapNestedOwnerAndSerializeJavaDatesAndEnums() {
        api.Respond = (method, path, body) => {
            if (method == "POST") {
                var dto = Read<PatitasArriba.Web.DTOs.MascotaDTO>(body);
                Assert.That(dto.Cliente!.Id, Is.EqualTo(7));
                Assert.That(dto.FechaNacimiento, Is.EqualTo("2020-02-03"));
                Assert.That(dto.TipoMascota, Is.EqualTo("GATO"));
                Assert.That(dto.Foto, Is.EqualTo("data:image/webp;base64,AAAA"));
                return (201, body);
            }
            Assert.That(path, Does.EndWith("/mascotas?cliente=7"));
            return (200, "[{\"id\":8,\"nombre\":\"Michi\",\"sexo\":\"HEMBRA\",\"tipoMascota\":\"GATO\",\"peso\":4,\"foto\":\"data:image/webp;base64,AAAA\",\"fechaNacimiento\":\"2020-02-03\",\"cliente\":{\"id\":7,\"nombres\":\"Ana\",\"apellidoPaterno\":\"Paz\"}}]");
        };
        var service = services.GetRequiredService<IMascotaService>();
        var pet = (await service.FindByCliente(7)).Single();
        Assert.That(pet.NombreDueno, Is.EqualTo("Ana Paz"));
        Assert.That(pet.Foto, Is.EqualTo("data:image/webp;base64,AAAA"));
        await service.Insert(pet);
    }

    [Test]
    public async Task LoginPreservesRoleAndBothIdentifiers() {
        api.Respond = (method, path, body) => {
            Assert.That(method, Is.EqualTo("POST"));
            Assert.That(path, Does.EndWith("/login"));
            Assert.That(Read<Dictionary<string, string>>(body)["correo"], Is.EqualTo("ana@example.com"));
            return (200, "{\"id\":10,\"idPersona\":7,\"nombreUsuario\":\"ana\",\"rol\":\"CLIENTE\",\"nombres\":\"Ana\"}");
        };
        var session = await services.GetRequiredService<ICuentaService>().Login("ana@example.com", "secret");
        Assert.That(session.Id, Is.EqualTo(10));
        Assert.That(session.Correo, Is.EqualTo(""));
        Assert.That(session.IdPersona, Is.EqualTo(7));
        Assert.That(session.Rol, Is.EqualTo(RolUsuario.CLIENTE));
    }

    [Test]
    public async Task ServiceWithoutDurationReadsAsNull() {
        api.Respond = (_, _, _) => (200, "[{\"id\":8,\"nombre\":\"Atención de emergencia\",\"precioBase\":150,\"tipo\":\"EMERGENCIA\",\"duracionEstimada\":null}]");
        var servicios = await services.GetRequiredService<IServicioService>().FindAll();
        Assert.That(servicios.Single().DuracionEstimada, Is.Null);
    }

    [Test]
    public async Task DateFilterOmitsOptionalVetAndAllImplementationsResolve() {
        api.Respond = (_, path, _) => {
            Assert.That(path, Does.EndWith("/citas?fecha=2026-11-20"));
            return (200, "[]");
        };
        await services.GetRequiredService<ICitaService>().FindByFecha(new DateOnly(2026, 11, 20));
        var contracts = typeof(IMascotaService).Assembly.GetTypes()
            .Where(t => t.IsInterface && t.Namespace == "PatitasArriba.Web.Servicios" && !t.IsGenericType);
        foreach (var contract in contracts) Assert.That(services.GetRequiredService(contract), Is.Not.Null);
    }

    [Test]
    public async Task ClinicalRecordsResolvePetFromAppointmentAndMapPrescriptionDetails() {
        var detalle = new { id = 11, servicio = new { id = 2, nombre = "Consulta", tipo = "CONSULTA_MEDICA" },
            veterinario = new { id = 3, nombres = "Luis", numeroColegiatura = "CM12" } };
        var atencion = new { id = 12, detalleCita = detalle, fechaHora = "2026-11-10T10:30:00",
            diagnosticos = new[] { new { diagnostico = new { id = 9, nombreEnfermedad = "Alergia" }, nivelGravedad = "LEVE" } },
            insumosUtilizados = Array.Empty<object>() };
        api.Respond = (_, path, _) => {
            object payload = path.EndsWith("/citas") ? new[] { new { id = 4,
                mascota = new { id = 8, nombre = "Michi", tipoMascota = "GATO", cliente = new { id = 7, nombres = "Ana" } },
                detalles = new[] { detalle } } }
                : path.Contains("/atenciones") ? new[] { atencion }
                : new[] { new { id = 13, atencion, fechaEmision = "2026-11-10", indicacionesGenerales = "Reposo",
                    detalles = new[] { new { articulo = new { id = 5, nombre = "Medicina" }, dosis = "1 ml", frecuencia = "Diaria", duracionDias = 3, cantidadTotal = 1 } } } };
            return (200, JsonSerializer.Serialize(payload, json));
        };
        var care = (await services.GetRequiredService<IAtencionService>().FindByMascota(8)).Single();
        Assert.That(care.IdMascota, Is.EqualTo(8));
        Assert.That(care.NombreCliente, Is.EqualTo("Ana"));
        Assert.That(care.Diagnosticos.Single().NivelGravedad, Is.EqualTo(NivelGravedad.LEVE));
        var prescription = (await services.GetRequiredService<IRecetaService>().FindByAtencion(12)).Single();
        Assert.That(prescription.TipoMascota, Is.EqualTo(TipoMascota.GATO));
        Assert.That(prescription.NombreMascota, Is.EqualTo("Michi"));
        Assert.That(prescription.Medicamentos.Single().IdArticulo, Is.EqualTo(5));
    }

    [Test]
    public async Task ReceiptSeparatesServiceSaleAndConsumedSupplyReferences() {
        api.Respond = (_, path, body) => {
            Assert.That(path, Does.EndWith("/boletas"));
            var dto = Read<PatitasArriba.Web.DTOs.BoletaDTO>(body);
            Assert.That(dto.Fecha, Is.EqualTo("2026-11-10T14:15:00"));
            Assert.That(dto.SerieCorrelativo, Is.Null);
            Assert.That(dto.DetallesServicio.Single().DetalleCita!.Id, Is.EqualTo(11));
            Assert.That(dto.DetallesArticulo.Count, Is.EqualTo(2));
            Assert.That(dto.DetallesArticulo.Count(x => x.InsumoUtilizado == null), Is.EqualTo(1));
            Assert.That(dto.DetallesArticulo.Single(x => x.InsumoUtilizado != null).InsumoUtilizado!.Id, Is.EqualTo(22));
            dto.Id = 30;
            dto.SerieCorrelativo = "B001-000124";
            return (201, JsonSerializer.Serialize(dto, json));
        };
        var receipt = new BoletaViewModel { IdCliente = 7, Fecha = new DateTime(2026, 11, 10, 14, 15, 0),
            MetodoPago = MetodoPago.YAPE, Lineas = [
                new() { Tipo = "Servicio", IdDetalleCita = 11 },
                new() { Tipo = "Artículo", IdArticulo = 5, Cantidad = 2 },
                new() { Tipo = "Insumo", IdArticulo = 6, IdInsumoUtilizado = 22 }
            ] };
        await services.GetRequiredService<IBoletaService>().Insert(receipt);
        Assert.That(receipt.Id, Is.EqualTo(30));
        Assert.That(receipt.SerieCorrelativo, Is.EqualTo("B001-000124"));
    }

    [Test]
    public async Task PendingChargesListOnlyAttendedServicesAndChargeableSuppliesNotYetBilled() {
        var vet = new { id = 3, nombres = "Luis" };
        var consulta = new { id = 11, servicio = new { id = 2, nombre = "Consulta", tipo = "CONSULTA_MEDICA" }, veterinario = vet };
        var corte = new { id = 12, servicio = new { id = 4, nombre = "Corte", tipo = "ESTETICA" }, veterinario = vet };
        var vacuna = new { id = 13, servicio = new { id = 5, nombre = "Vacuna", tipo = "VACUNACION" }, veterinario = vet };
        var cita = new { id = 4, fechaHora = "2026-11-10T10:00:00", estado = "APROBADO",
            mascota = new { id = 8, nombre = "Michi", cliente = new { id = 7, nombres = "Ana" } },
            detalles = new[] { consulta, corte, vacuna } };
        var ajena = new { id = 5, fechaHora = "2026-11-10T11:00:00", estado = "APROBADO",
            mascota = new { id = 9, nombre = "Rocky", cliente = new { id = 99, nombres = "Otro" } },
            detalles = new[] { new { id = 20, servicio = new { id = 2, nombre = "Consulta", tipo = "CONSULTA_MEDICA" }, veterinario = vet } } };
        object Insumo(int id, int articulo, double precio, bool cobrable) => new {
            id, articulo = new { id = articulo, nombre = $"Articulo {articulo}", precioBase = precio }, cantidadUtilizada = 2, esCobrable = cobrable };
        object Atencion(int id, object detalle, params object[] insumos) => new {
            id, detalleCita = detalle, fechaHora = "2026-11-10T10:30:00", diagnosticos = Array.Empty<object>(), insumosUtilizados = insumos };
        api.Respond = (_, path, _) => {
            object payload =
                path.EndsWith("/citas?cliente=7") ? new[] { cita }
                : path.EndsWith("/citas") ? new object[] { cita, ajena }
                // Consulta (con un insumo cobrable y otro no) y Corte atendidos; la Vacuna no; la de otro cliente tampoco cuenta
                : path.EndsWith("/atenciones") ? new[] {
                    Atencion(1, consulta, Insumo(31, 6, 18.0, true), Insumo(32, 7, 4.0, false)),
                    Atencion(2, corte, Insumo(33, 6, 18.0, true)),
                    Atencion(3, ajena.detalles[0]) }
                // El Corte y su insumo ya se cobraron en una boleta activa
                : path.EndsWith("/boletas") ? new[] { new { id = 40, activo = true, fecha = "2026-11-10T12:00:00", metodoPago = "YAPE",
                    cliente = new { id = 7 },
                    detallesServicio = new[] { new { detalleCita = new { id = 12 }, cantidad = 1, precioUnitario = 50.0 } },
                    detallesArticulo = new[] { new { articulo = new { id = 6 }, insumoUtilizado = new { id = 33 }, cantidad = 2, precioUnitario = 18.0 } } } }
                : new[] { new { id = 2, nombre = "Consulta", tipo = "CONSULTA_MEDICA", precioBase = 60.0 },
                          new { id = 4, nombre = "Corte", tipo = "ESTETICA", precioBase = 50.0 },
                          new { id = 5, nombre = "Vacuna", tipo = "VACUNACION", precioBase = 45.0 } };
            return (200, JsonSerializer.Serialize(payload, json));
        };
        var pendientes = await CobrosPendientes.DelCliente(7, services.GetRequiredService<ICitaService>(),
            services.GetRequiredService<IAtencionService>(), services.GetRequiredService<IBoletaService>(),
            services.GetRequiredService<IServicioService>());
        Assert.That(pendientes.Count, Is.EqualTo(2));
        var servicio = pendientes.Single(l => l.Tipo == "Servicio");
        Assert.That(servicio.IdDetalleCita, Is.EqualTo(11));
        Assert.That(servicio.PrecioUnitario, Is.EqualTo(60.0));
        Assert.That(servicio.Descripcion, Is.EqualTo("Consulta · Michi"));
        var insumo = pendientes.Single(l => l.Tipo == "Insumo");
        Assert.That(insumo.IdInsumoUtilizado, Is.EqualTo(31));
        Assert.That(insumo.IdArticulo, Is.EqualTo(6));
        Assert.That(insumo.SubTotal, Is.EqualTo(36.0));
    }

    [Test]
    public async Task PersonInsertCreatesTheAccountWithOnlyTheEmail() {
        string? method = null, path = null;
        PatitasArriba.Web.DTOs.ClienteDTO? sent = null;
        api.Respond = (m, p, body) => {
            method = m;
            path = p;
            sent = Read<PatitasArriba.Web.DTOs.ClienteDTO>(body);
            return (201, "{\"id\":5,\"cuenta\":{\"id\":20,\"correo\":\"ana@example.com\"}}");
        };
        var client = new ClienteViewModel { Correo = "ana@example.com", Dni = "12345678", Nombres = "Ana" };
        await services.GetRequiredService<IClienteService>().Insert(client);
        Assert.That(method, Is.EqualTo("POST"));
        Assert.That(path, Does.EndWith("/clientes/con-cuenta"));
        Assert.That(sent!.Cuenta!.Correo, Is.EqualTo("ana@example.com"));
        Assert.That(sent.Cuenta.Password, Is.Null);
        Assert.That(client.Id, Is.EqualTo(5));
    }

    [Test]
    public async Task PersonInsertPropagatesTheValidationError() {
        api.Respond = (_, _, _) => (400, "{\"error\":\"DNI inválido\"}");
        var client = new ClienteViewModel { Correo = "ana@example.com" };
        Assert.ThrowsAsync<HttpRequestException>(
            async () => await services.GetRequiredService<IClienteService>().Insert(client));
        await Task.CompletedTask;
    }

    [Test]
    public async Task LoginReadsTheAccountEmail() {
        api.Respond = (_, _, _) => (200, "{\"id\":10,\"correo\":\"ana@example.com\",\"idPersona\":7,\"nombreUsuario\":\"ana\",\"rol\":\"CLIENTE\",\"nombres\":\"Ana\"}");
        var session = await services.GetRequiredService<ICuentaService>().Login("ana@example.com", "secret");
        Assert.That(session.Correo, Is.EqualTo("ana@example.com"));
    }

    [Test]
    public async Task RegistrationPostsTrimmedDataAndReturnsBackendMessage() {
        Dictionary<string, string>? sent = null;
        api.Respond = (method, path, body) => {
            Assert.That(method, Is.EqualTo("POST"));
            Assert.That(path, Is.EqualTo("/app/api/v1/acceso/registro"));
            sent = Read<Dictionary<string, string>>(body);
            return (202, "{\"mensaje\":\"Te enviamos un correo para confirmar tu registro\"}");
        };
        var registro = new RegistroViewModel {
            Dni = "70123456", Nombres = " Ana ", ApellidoPaterno = "Torres", ApellidoMaterno = "Lopez",
            Telefono = "987654321", Correo = " ana@example.com ", Password = " clave123 ", ConfirmarPassword = " clave123 "
        };
        var mensaje = await services.GetRequiredService<IAccesoService>().SolicitarRegistro(registro);
        Assert.That(mensaje, Is.EqualTo("Te enviamos un correo para confirmar tu registro"));
        Assert.That(sent!["nombres"], Is.EqualTo("Ana"));
        Assert.That(sent["correo"], Is.EqualTo("ana@example.com"));
        Assert.That(sent["password"], Is.EqualTo(" clave123 "));
        Assert.That(sent.ContainsKey("confirmarPassword"), Is.False);
    }

    [Test]
    public async Task AccessRoutesSendTheExpectedBodies() {
        var calls = new List<(string Path, Dictionary<string, string> Body)>();
        api.Respond = (_, path, body) => {
            calls.Add((path, Read<Dictionary<string, string>>(body)));
            return (path.EndsWith("/confirmar") ? 201 : path.EndsWith("/restablecer") ? 200 : 202, "{\"mensaje\":\"ok\"}");
        };
        var acceso = services.GetRequiredService<IAccesoService>();
        Assert.That(await acceso.ConfirmarRegistro("tok-1"), Is.EqualTo("ok"));
        Assert.That(await acceso.SolicitarRecuperacion(" ana@example.com "), Is.EqualTo("ok"));
        Assert.That(await acceso.RestablecerContrasena("tok-2", "NuevaClave123"), Is.EqualTo("ok"));
        Assert.That(calls[0].Path, Is.EqualTo("/app/api/v1/acceso/registro/confirmar"));
        Assert.That(calls[0].Body["token"], Is.EqualTo("tok-1"));
        Assert.That(calls[1].Path, Is.EqualTo("/app/api/v1/acceso/recuperacion"));
        Assert.That(calls[1].Body["correo"], Is.EqualTo("ana@example.com"));
        Assert.That(calls[2].Path, Is.EqualTo("/app/api/v1/acceso/recuperacion/restablecer"));
        Assert.That(calls[2].Body["token"], Is.EqualTo("tok-2"));
        Assert.That(calls[2].Body["password"], Is.EqualTo("NuevaClave123"));
    }

    [Test]
    public void ExpiredTokenKeepsBackendMessageAndStatus() {
        api.Respond = (_, _, _) => (400, "{\"error\":\"El enlace no es válido o ya venció\"}");
        var error = Assert.ThrowsAsync<HttpRequestException>(
            async () => await services.GetRequiredService<IAccesoService>().ConfirmarRegistro("viejo"));
        Assert.That(error!.StatusCode, Is.EqualTo(HttpStatusCode.BadRequest));
        Assert.That(error.Message, Is.EqualTo("El enlace no es válido o ya venció"));
    }

    private sealed class ApiStub : IAsyncDisposable {
        private readonly Microsoft.AspNetCore.Builder.WebApplication app;
        public string Url { get; private set; } = "";
        public Func<string, string, string, (int Status, string Body)> Respond { get; set; } = (_, _, _) => (200, "[]");
        public ApiStub() {
            var builder = Microsoft.AspNetCore.Builder.WebApplication.CreateBuilder();
            builder.WebHost.UseUrls("http://127.0.0.1:0");
            builder.Logging.ClearProviders();
            app = builder.Build();
            app.Run(async context => {
                using var reader = new StreamReader(context.Request.Body);
                var body = await reader.ReadToEndAsync();
                var result = Respond(context.Request.Method,
                    context.Request.Path.Value + context.Request.QueryString.Value, body);
                context.Response.StatusCode = result.Status;
                context.Response.ContentType = "application/json; charset=utf-8";
                if (result.Status != 204) await context.Response.WriteAsync(result.Body);
            });
        }
        public async Task Start() {
            await app.StartAsync();
            Url = app.Urls.Single();
        }
        public async ValueTask DisposeAsync() {
            await app.StopAsync();
            await app.DisposeAsync();
        }
    }
}
