# Clientes REST de Patitas Arriba

Las 14 clases implementan las interfaces de Servicios. Comparten RegistroRestService para CRUD y usan HttpUtils para HTTP, siguiendo la separación cliente/DTO/ViewModel de los ejemplos de clase.

- URL base: `RestApiBaseUrl` en appsettings.json (incluye contexto y `/api/v1`).
- Registro de dependencias: `AddPatitasRestServices()` en Program.cs.
- Uso en Blazor: `@inject IMascotaService MascotaService`; luego `await MascotaService.FindAll()`.
- Las pantallas y SesionDemo todavía conservan su funcionamiento actual: este cambio incorpora clientes, no sustituye los datos de demostración.
- FindById devuelve null ante 404. Las otras operaciones propagan HttpRequestException con StatusCode y el mensaje `error` de Java.
- Las búsquedas por nombre, DNI y usuario leen el arreglo JSON que devuelve Java y extraen la coincidencia.
- Insert asigna al ViewModel el identificador devuelto por Java.
- Fechas: yyyy-MM-dd; fecha/hora local: yyyy-MM-ddTHH:mm:ss; horas: HH:mm:ss. No se agrega Z a los LocalDateTime de Java.
- Los enums se convierten por nombre y los valores desconocidos se rechazan.

## Relaciones y límites actuales del contrato Java

Clientes y personal necesitan una cuenta existente. Antes de guardar, el cliente REST crea la cuenta cuando IdCuenta es cero; si ya existe, actualiza sus datos. Conserva IdCuenta si la creación de la persona falla, para permitir corregir y reintentar sin crear otra cuenta. Son solicitudes separadas: no existe atomicidad entre cuenta y persona. Para garantizarla se necesita una operación transaccional en Java.

Atenciones y recetas consultan también citas para completar nombres de cliente y mascota: esos datos no están en su DTO directo. Las cuentas consultan clientes y personal para conocer su propietario y rol; una cuenta sin propietario muestra Sin asignar. Estas consultas complementarias implican peticiones adicionales.

Los selectores de categorías y horarios devueltos por los mappers contienen las opciones asignadas. Al implementar las páginas de edición, se debe cargar el catálogo completo y conservar la selección por identificador.

POST /login devuelve datos de sesión; este cliente no instala autenticación ni modifica automáticamente SesionDemo. Las restricciones de acceso deben completarse en la integración de las pantallas y en Java.

Las pruebas RestServicesTests usan un servidor HTTP local controlado, sin tocar la base de datos ni requerir GlassFish.
