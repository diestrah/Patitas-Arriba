# Lineamientos del prof. Eric Huiza para la Tarea Académica (TA) — Patitas Arriba

Síntesis de lo que Huiza dijo en clase (transcripciones 31/08–11/11/2026) y del documento oficial "Horarios, Lineamientos y Cronograma 1INF30-2026-2". Entre corchetes: clase y minuto donde lo dijo. Transcripciones limpias (una por clase, con marca `[NNm]` por minuto) en `E:\Almacen\pucp\Progra3\transcripciones\limpias\` (fuera del repo y en dispositivo de Nando); el resumen por clase y minuto es `00_resumen_de_clases_y_temas_por_minuto.txt` en esa misma carpeta. Sirve como checklist para revisar el proyecto.

## 0. Fechas clave (documento oficial)
- Subida a PAIDEIA de la TA: hasta **domingo 22/11/2026, 23:59**.
- Presentación final: **25/11/2026** (orden por sorteo, publicado en PAIDEIA [9nov 13m]).
- Lab 13 (integrador, individual): 25/11. Examen final: 04/12.
- Alcance: **3 RF + 1 RNF por integrante**; **mínimo 2, máximo 3 reportes**. RF redactados como "El sistema permitirá a <actor> <acción>". Toda la información de la BD debe poder gestionarse (registrar, modificar, eliminar, consultar) desde el software.
- Back-end en **Java**, front-end en **C#**; sin errores de compilación ni de ejecución; debe funcionar en las PCs del laboratorio/docente (saber guiar la configuración).
- Nota de 0 a 18 + **2 puntos de investigación** (algo no enseñado en clase) para llegar a 20 [2nov 68m].

## 1. Arquitectura general (Back-end Java)
- Proyecto Maven multimódulo: `dbmanager`, `modelo`, `accesodatos` (DAO), `negocio` (BL), `rest` (API), `reportes` (servlet). [2sep 77m]
- `DBManager` Singleton con `getInstance()` **synchronized**, configuración en `db.properties` leída con `ResourceBundle`; subir `db.properties.example` y no exponer contraseñas reales en GitHub. [2sep 94m, 7sep]
- Responsabilidad única por capa: DBManager solo crea conexiones; DAO ejecuta comandos; BL valida y orquesta transacciones. [16sep 02m]

## 2. Modelo de dominio
- Encapsulamiento completo: atributos privados, constructor por defecto y **constructor de copia**, getters/setters. [7sep 51m]
- **Las FK se modelan como objetos, nunca como `int idX`** ("regla fundamental de calificación"). [10sep 34m]
- Copias defensivas en getters/setters de objetos compuestos; usar `LocalDate`/`LocalDateTime` (no `java.util.Date`). [10sep 34m]
- Maestro-detalle (composición): getter con `Collections.unmodifiableList`, setter con `List.copyOf`. [14sep 05m]
- **Validaciones completas en los setters** (`IllegalArgumentException`): en labs eran opcionales, en la TA "las quiero completitas". [23sep 24m, 28sep 07m]
- Herencia en clases: base abstracta `Registro` (id, activo) y subclases como `Persona`. Para la TA **sí exige la refactorización** con herencia y genéricos (en el lab se permitía duplicar). [10sep 14m–16m]
- Herencia en BD: recomendada **Concrete Table Inheritance** (una tabla por clase concreta). Si usan Class Table, debe haber código transaccional (lo ha visto mal en TAs). [7sep 37m–43m]
- Enums mapeados desde VARCHAR con `Enum.valueOf`. [10sep 15m]

## 3. Acceso a datos (DAO)
- Patrón DAO: interfaz genérica `DAO<T, ID>` (findAll, findById, insert, update, delete) + interfaces por entidad que la extienden + implementaciones genéricas abstractas (`RegistroDAOImpl<T extends Registro>`, `mapear()` en cascada). [10sep 61m]
- **Procedimientos almacenados obligatorios en la TA** (en labs era opcional): `CallableStatement`, `{call sp(?)}`, parámetros OUT para el id generado. Nombres en `snake_case` minúsculas. [16sep 27m–29m]
- Nunca `Statement`: `PreparedStatement`/`CallableStatement` (SQL injection). `try-with-resources`. [7sep]
- FK nulas con `rs.wasNull()`; FK resueltas con el DAO correspondiente. [10sep 34m]
- Maestro-detalle: DAO de detalle **con visibilidad de paquete** (sin `public`), solo lo usa el DAO maestro; inserción de líneas con **batch** (`addBatch`/`executeBatch`); update = borrar líneas y reinsertar; delete = detalle primero. [14sep 19m–50m]
- Entregar scripts SQL de tablas y de **todos** los procedimientos almacenados.

## 4. Negocio (BL)
- Interfaz genérica `RegistroBL<T, ID>`; BL simples hacen "bypass" al DAO; BL transaccionales usan `TransactionManager` con `ThreadLocal<Connection>` (iniciar/commit/rollback/cerrar). El BL no depende de `java.sql`. [14sep 32m, 16sep 12m]
- Escrituras que tocan varias tablas → transacción; lecturas → conexión normal. [16sep 08m]
- Reglas de negocio reales (DNI 8 dígitos con regex, mayoría de edad, unicidad con `findByDni`, etc.). [14sep 63m]
- **El servidor nunca confía en los cálculos del cliente**: recalcular subtotales/totales en el negocio. [28sep 82m]
- **Validaciones en todas las capas** (cliente, servidor, BD): "así tienen que entregar sus TA". [28sep 87m]
- Excepciones propias: `BusinessException`/`BLException` como base y, **exigido en la TA**, subclases **NoEncontradoException** y **ValidacionException**; el BL lanza, no devuelve `null`. Ocultan los `SQLException`. [2nov 44m–48m]

## 5. Integración: API REST (obligatorio REST, no SOAP)
- "En la tarea académica usen **REST**, no XML/SOAP"; toda la integración con REST. [21oct 81m, 28oct 72m]
- Módulo Jakarta EE (JAX-RS) desplegado en GlassFish; `Application` con `@ApplicationPath("/api/v1")`: **contexto `/api` + versión obligatoria** ("si no veo eso, no sería completa la entrega"). [28oct 74m, 2nov 04m]
- Recursos con **sustantivos en plural**, **sin verbos en la URL** (penalización). [28oct 65m]
- Operación por verbo HTTP: GET/POST/PUT/DELETE; filtros con `@QueryParam` dentro del `findAll` (no rutas que choquen). [2nov 63m]
- Códigos de estado correctos (se evalúan): GET 200, POST **201** + cabecera `Location`, PUT 200 (validar id URL = id cuerpo, si no 400), DELETE **204**; NoEncontrado → 404, Validación → 400, otros → 500; errores en **JSON** (`Map` con `error`). [28oct 86m, 2nov 16m–71m]
- El recurso **solo traduce DTO ↔ modelo y delega al BL** (patrón Facade); nada de lógica de negocio en la API. [26oct 82m]
- **DTO + Mappers obligatorios en la TA** ("tiene que ser completo"; en labs/examen se puede usar el modelo directo). [26oct 78m, 4nov 59m–61m]
- Probar con Postman (colecciones y entornos).

## 6. Front-end Blazor (C#)
- Blazor Web App, modo **InteractiveServer**; Bootstrap; `MainLayout` con navegación; HTML semántico. [5oct 85m, 7oct 26m]
- **Inyección de dependencias** (`AddScoped`) en `Program.cs`, `@inject` en páginas; nunca `new` de servicios. [5oct 99m]
- **Capa de servicios con bajo acoplamiento obligatoria en la TA**: interfaces `IXService` + implementación REST; los componentes Razor no conocen la tecnología de comunicación. [28oct 00m–31m, 4nov 82m]
- `HttpUtils` estático con un único `HttpClient` y métodos genéricos `DoGet/DoPost/DoPut/DoDelete`; URL base en `appsettings.json` (`RestApi:BaseUrl`) leída con `IConfiguration`. [4nov 29m]
- **Todo asíncrono**: servicios devuelven `Task`/`Task<T>`; `OnInitializedAsync`/`OnParametersSetAsync`; nunca llamar async desde sync. [4nov 17m]
- DTOs en .NET + mapeo `ToViewModel`/`ToDto`. [4nov 54m]
- **ViewModels con Data Annotations** (`[Required]`, `[StringLength]`, `[Range]`, `[RegularExpression]`) y `<EditForm>` + `DataAnnotationsValidator` + `ValidationMessage`; no enlazar formularios al modelo de dominio. [19oct 14m–23m]
- Rutas semánticas en plural (`/productos`, `/productos/nuevo`, `/productos/editar/{Id:int}`); listados con QuickGrid (paginación, ordenamiento); búsqueda con `@bind`. [7oct]
- **Componentes reutilizables** (carpeta `Shared`, `[Parameter]`, `[EditorRequired]`) y **maestro-detalle con componentes padre-hijo**: "los que van a trabajar con maestro-detalle deberían trabajarlo de esa manera". [19oct 52m–67m, 21oct 03m]
- Opcional sugerido: pruebas E2E con Playwright para no fallar en la presentación. [21oct 22m]

## 7. Seguridad
- **Autenticación y autorización por roles en la TA** ("aplicar algo similar, algo simple"): login con cookie + Claims, políticas, `<AuthorizeView>`, `[Authorize(Policy=...)]`, `UseAntiforgery`. [21oct 50m–77m]
- **Pregunta segura en la presentación**: qué pasa si en el login no ingresan nada → debe haber validación. [21oct 46m]
- Extra (puntos de investigación): tokens (JWT/OAuth), contraseñas encriptadas, verbo HTTP **QUERY**. [2nov 68m, 4nov 04m]

## 8. Reportes (JasperReports)
- **Mínimo 2 reportes** (máx. 3), con JasperSoft Studio, **parametrizados**, usando procedimientos almacenados; números con `BigDecimal`. [11nov 113m]
- Módulo `reportes` (servlet `@WebServlet` que devuelve `application/pdf`); el front abre el enlace "Imprimir" con `Reportes:BaseUrl` en `appsettings`. [11nov 72m–104m]
- Cuidar tildes/ortografía en los reportes. [11nov 52m]

## 9. Despliegue (obligatorio en la TA)
- BD en **AWS RDS**; back-end en EC2 Windows Server (large, 8 GB) con GlassFish; front-end en otra EC2 con IIS + ASP.NET Core Hosting Bundle. [9nov 02m–62m, 52m]
- Quitar `-SNAPSHOT` de todos los `pom.xml`, `mvn clean package`, desplegar los `.war` con el mismo procedimiento, incluido el `.war` de **reportes** (en clase eran ws + rest + reportes; si la TA usa solo REST, serían rest + reportes). Abrir puertos 8080/80/443 en Security Group y Firewall de Windows. [9nov 14m–28m, 23m]
- `appsettings` del front apuntando al DNS público del **backend**.

## 10. Trabajo en equipo
- Repositorio en GitHub con **Git Flow**
- En cada avance: reporte con actividades y responsables (quien no figure → 00).
