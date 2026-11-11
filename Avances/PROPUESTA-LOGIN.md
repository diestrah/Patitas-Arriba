# Propuesta de login (rama `propuesta-login`)

**Estado:** propuesta para revisar. **No mezclar con `main` hasta tener la aprobación del JP**: agrega requerimientos nuevos (RF19 y RF20) y modifica RF01, RF02, RF04 y RF05.

Reglas completas: `REGLAS-DE-NEGOCIO.md` §4.1 (RN-AC-01 a 09).

---

## ⚠ Advertencias: lo que cambia para el resto del equipo

1. **El login es por correo.** `CuentaBL.login(correo, password)` y `POST /login` recibe `{ "correo", "password" }` (antes `nombreUsuario`). El error es "Correo o contraseña incorrectos". Hay que actualizar el contrato de Postman, el `Tester` propio y todo lo que llame al login.
2. **Hay que ejecutar un script SQL.** `sql/actualizacion-acceso.sql` agrega 2 tablas y 9 procedures. Solo agrega: no modifica nada existente. Sin él funcionan el login y el resto, pero fallan el registro y la recuperación de contraseña.
3. **Los formularios de persona ya no piden usuario ni contraseña.** Cliente, veterinario y administrador se crean con `POST /clientes|veterinarios|administradores/con-cuenta`, enviando solo `cuenta.correo`. El `POST` anterior (con `cuenta.id`) sigue existiendo.
4. **Cambio de visibilidad (sin cambio de lógica):** `validar` y `validarUnicos` de `ClienteBLImpl`, `VeterinarioBLImpl`, `AdministradorBLImpl` y `CuentaBLImpl` pasaron de `private` a paquete, para reutilizarlas desde `AccesoBLImpl`.
5. **Se quitó la portada previa al login.** `/` sin sesión lleva a `/login`.
6. **`Tester`:** los nombres de prueba ya no llevan dígitos y hay una sección nueva "ACCESO".

## Qué trae

| Capa | Cambio |
|---|---|
| **SQL** | Tablas `registro_pendiente` y `token_recuperacion`; procedures en `procedures-registro_pendiente.sql` y `procedures-token_recuperacion.sql`; `actualizacion-acceso.sql` las aplica a una base ya instalada; `limpiar-tablas.sql` las vacía. |
| **Modelo** | `RegistroPendiente`, `TokenRecuperacion`. |
| **DAO** | `RegistroPendienteDAO`, `TokenRecuperacionDAO` (con sus `Impl`) y `CuentaDAO.findByCorreo` (usa el procedure `buscar_cuenta_por_correo`, que ya existía). |
| **BL** | `AccesoBL`/`AccesoBLImpl`: solicitar y confirmar registro, solicitar y restablecer recuperación, y registrar cliente / veterinario / administrador (persona y cuenta en una transacción). Paquete `bl.correo` (`CorreoService`, `CorreoConsola`, `CorreoSmtp`, `Correos`) y `TokenUtil`. |
| **REST** | `AccesoResource` (`/acceso/registro`, `/acceso/registro/confirmar`, `/acceso/recuperacion`, `/acceso/recuperacion/restablecer`) y `POST /con-cuenta` en clientes, veterinarios y administradores. |
| **Front** | Pantallas `/registro`, `/verificar-correo`, `/olvide-contrasena` y `/restablecer-contrasena`; login con correo, enlaces y errores en rojo que aparecen al salir del campo; formularios de persona solo con correo. |

## Cómo funciona (resumen)

- **Registro de cliente:** guarda una solicitud y envía un enlace de 24 h; la cuenta y el cliente se crean al abrirlo.
- **Olvidé mi contraseña:** enlace de 30 min y un solo uso; la respuesta es igual exista o no el correo.
- **Alta por el administrador:** la cuenta nace con una clave que nadie conoce y la persona elige la suya con un enlace de 48 h.
- Los tokens se guardan solo como hash. Contraseña mínima: 8 caracteres. Nombres y apellidos solo con letras.

## Configuración (opcional)

Sin configurar nada, los correos **no se envían**: se imprimen en la consola del servidor (ahí se ve el enlace). Para enviar de verdad, variables de entorno (**nunca en el repositorio**): `PATITAS_SMTP_HOST`, `PATITAS_SMTP_PUERTO` (465 por defecto), `PATITAS_SMTP_USUARIO`, `PATITAS_SMTP_CLAVE`, `PATITAS_SMTP_REMITENTE` y `PATITAS_URL_FRONT` (por defecto `http://localhost:5118`).

## Cómo probar

1. Ejecutar `sql/actualizacion-acceso.sql` en la base.
2. Correr `ejecucion.Tester`: debe terminar con "Sin fallas." (incluye la sección "ACCESO").
3. Front: `dotnet run` en `Avances-dotnet/PatitasArriba.Web`. Iniciar sesión con `diego.lopez@patasarriba.pe` (administrador), `carla.vega@patasarriba.pe` (veterinaria) o `ana.torres@gmail.com` (cliente), con cualquier contraseña.

## Sin probar o pendiente

- El `Tester` no se ejecutó contra la base con estos cambios.
- El envío SMTP real (`CorreoSmtp`, solo JDK, puerto 465) no se probó.
- El módulo `rest` no se pudo compilar sin conexión; se revisó con stubs de Jakarta.
- Las pantallas del front funcionan en **modo demostración**: aún no llaman a la API.
- Sin límite de intentos ni captcha en el registro público.
- Las contraseñas siguen con SHA-256 sin sal (cumple RNF05; BCrypt sería lo recomendable).
- Informe: hay ajustes pendientes (RF19 y RF20, RF01/02/04/05, RNF05 y RNF08, diagramas de clases y físico, anexos).
