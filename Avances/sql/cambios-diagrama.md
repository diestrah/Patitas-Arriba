# Cambios al diagrama V2 (`diagrama-fisico_patitasV2.mwb`)

Regla: cada cambio se hace **primero en el diagrama**; luego se genera el DDL (Forward Engineer), se pasa a minúscula y recién después se adaptan procedures y Java.

Lógica de negocio que guía los cambios: una **cita** es un contenedor; cada servicio pedido es un `detalle_cita`, y la **atención** es la ejecución de cada `detalle_cita`. Una cita tiene varias atenciones.

---

## Hechos

### 1. El veterinario pasa de `cita` a `atencion_medica`
- **Por qué:** en una misma cita, cada atención puede hacerla un veterinario distinto (baño con uno, chequeo con otro). Con el veterinario en `cita` solo cabe uno por cita completa.
- **Cambio hecho en el diagrama:**
  - `cita`: se quitó `id_veterinario`, su índice y su FK. Quedan `id_cita`, `id_mascota`, `fecha_hora`, `estado`, `es_emergencia`.
  - `atencion_medica`: se agregó `id_veterinario INT NOT NULL`, con FK a `veterinario`.
- **Decisiones:** obligatorio en la atención (`NOT NULL`); `horario_veterinario` no cambia.
- **Java que arrastra (después):** `Cita` (modelo, DAO, BL, procedures, Tester) y `AtencionMedica`.

### 3. Quitar `id_mascota` de `atencion_medica`
- **Por qué:** es redundante; la mascota se obtiene por `detalle_cita → cita → mascota`. Tener las dos rutas permitía datos contradictorios (atención de una mascota enlazada a la cita de otra). Toda atención tiene `id_detalle_cita NOT NULL`, así que no hay atenciones sueltas que necesiten mascota propia.
- **Cambio hecho en el diagrama:** se quitó `id_mascota`, su FK y su índice de `atencion_medica`. Ahora solo `cita` apunta a `mascota`.
- **Java que arrastra (después):** `AtencionMedica` obtiene la mascota vía la cita; procedures de búsqueda por mascota necesitarán `JOIN`.

### 2. Cobrar en la boleta los insumos usados en una atención
_(Hecho y verificado en el diagrama: `es_cobrable` en `insumo_utilizado`; `id_insumo_utilizado` con FK y `UNIQUE` en `detalle_boleta_art`; índice `(id_boleta, id_articulo)` eliminado y FK de boleta recreada.)_

**El problema.** En una atención se usan insumos (vacuna, algodón, alcohol...). Algunos los **paga el cliente aparte** (la vacuna) y otros **ya vienen incluidos** en el precio del servicio (el algodón). Hoy `insumo_utilizado` registra todo lo usado, pero la boleta solo sabe cobrar servicios (`detalle_boleta_serv`) y artículos vendidos en caja (`detalle_boleta_art`). La vacuna usada en la atención no tiene dónde aparecer, y por eso no se cobra.

**La solución (dos arreglos que van juntos).**
- **A. Distinguir qué se cobra:** nuevo campo `es_cobrable` en `insumo_utilizado` (1 = lo paga el cliente aparte, 0 = incluido en el servicio).
- **B. Dónde se cobra:** nuevo campo opcional `id_insumo_utilizado` en `detalle_boleta_art`, con FK a `insumo_utilizado`. Si viene vacío, la línea es una venta directa de caja; si trae un valor, es un insumo de atención que se está cobrando.

**Cambios en el diagrama.**

| Tabla | Cambio | Para qué |
|---|---|---|
| `insumo_utilizado` | + `es_cobrable TINYINT(1) NOT NULL DEFAULT 0` | saber qué insumos se cobran |
| `detalle_boleta_art` | + `id_insumo_utilizado INT NULL` con FK a `insumo_utilizado` | enlazar la línea de boleta con el insumo |
| `detalle_boleta_art` | + `UNIQUE (id_insumo_utilizado)` | que un mismo insumo no se cobre dos veces |
| `detalle_boleta_art` | − índice único `(id_boleta, id_articulo)` | permitir dos líneas del mismo artículo cuando vienen de insumos distintos |

**Por qué sale el índice `(id_boleta, id_articulo)`.** Antes obligaba "una línea por artículo, con la cantidad dentro". Con los insumos, el mismo artículo puede salir de dos atenciones distintas de una misma boleta (dos vacunas en dos servicios). Para saber de qué atención salió cada cobro hacen falta dos líneas, y ese índice las rechazaría. La boleta que ve el cliente puede mostrarse agrupada; la trazabilidad interna se queda por línea.

**Decisión de diseño.** La línea guarda `id_articulo` y también `id_insumo_utilizado` (el insumo ya conoce su artículo). Se acepta esa pequeña redundancia a cambio de no tocar las ventas directas; el BL validará que ambos coincidan.

**Ejemplo (una sola boleta, varias líneas).** Ana lleva a Firulais: vacunación (S/ 50) y consulta (S/ 80). Se usó una vacuna cobrable (S/ 35), 2 desparasitantes cobrables (S/ 10 c/u), algodón y alcohol (incluidos), y Ana compra un shampoo (S/ 25) en caja.

| Línea | Tabla | Subtotal |
|---|---|---|
| Vacunación | `detalle_boleta_serv` | 50 |
| Consulta médica | `detalle_boleta_serv` | 80 |
| Vacuna antirrábica (insumo 101) | `detalle_boleta_art` | 35 |
| Desparasitante ×2 (insumo 104) | `detalle_boleta_art` | 20 |
| Shampoo (venta directa) | `detalle_boleta_art` | 25 |
| **Total de la boleta** | | **210** |

El algodón y el alcohol no se cobran porque su `es_cobrable` es 0.

**Flujo (cómo se arma la boleta).**
1. El BL crea una línea de servicio por cada `detalle_cita`.
2. Agrega los insumos de esas atenciones con `es_cobrable = 1` que aún no estén en ninguna boleta.
3. Suma las ventas directas de caja.
4. El total es la suma de los subtotales.

**Java que arrastra (después).**
- El **stock se descuenta cuando se registra el insumo en la atención**, sea cobrable o no (hoy no se descuenta en ningún caso). Si no alcanza el stock, el BL lanza error y revierte.
- En la boleta, las líneas con `id_insumo_utilizado` **no** vuelven a descontar stock.
- `DetalleBoleta` gana un `insumoUtilizado` opcional; el BL valida que el artículo coincida con el del insumo.

**Pregunta abierta para el compañero (mismo problema que el cambio 10).** El `UNIQUE (id_insumo_utilizado)` de `detalle_boleta_art` impide cobrar un insumo dos veces, pero también impediría **volver a cobrarlo después de anular una boleta**: la línea de la boleta anulada sigue ocupando ese `id_insumo_utilizado`. Hay que decidir entre:
- **Dejar el `UNIQUE`** (garantía fuerte en la base; re-facturar un insumo tras anular exigiría un procedimiento aparte que libere la línea anulada).
- **Quitarlo y controlarlo en el BL** (como se decidió para `detalle_boleta_serv`): antes de cobrar un insumo, el BL comprueba que no esté en una boleta no anulada.
- Recomendación: la segunda, por coherencia con el cambio 10.

### 4. Corregir la expresión de `subtotal`
- **Por qué:** en `detalle_boleta_art` y `detalle_boleta_serv`, `subtotal` es una columna calculada por MySQL. La fórmula estaba guardada con backticks sobrantes (``CANTIDAD` * `PRECIO_UNITARIO``), y al generar el DDL salía SQL inválido que MySQL rechazaba. Cada vez que se regenerara el script habría que arreglarlo a mano.
- **Cambio hecho en el diagrama:** en las dos tablas la expresión ahora es `CANTIDAD * PRECIO_UNITARIO` (sin backticks), con almacenamiento `STORED` en ambas.
- **Java que arrastra (después):** los procedures de `detalle_boleta_*` **no deben insertar ni actualizar `subtotal`** (MySQL lo calcula); el modelo solo lo lee.

### 6. `nivel_gravedad` y `dia_semana` pasan de `VARCHAR` a `ENUM`
- **Por qué:** en Java ya son enums (`NivelGravedad`, `DiaSemana`) y los DAOs los guardan con `name()` y leen con `valueOf()`. Con `VARCHAR`, la base aceptaba cualquier texto y un valor inválido solo fallaba después, al leerlo con `valueOf()`. Con `ENUM`, la base rechaza lo que no sea válido y el diagrama documenta los valores permitidos. Es coherente con los demás ENUM (`tipo_mascota`, `metodo_pago`, `estado`, `tipo_servicio_medico`).
- **Cambio hecho en el diagrama:**
  - `atencion_diagnostico.nivel_gravedad` → `ENUM('LEVE','MODERADO','GRAVE','CRITICO')`, sigue opcional.
  - `horario.dia_semana` → `ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO')`, sigue obligatorio.
- **Java que arrastra (después):** nada. Los DAOs ya usan `name()`/`valueOf()` y los procedures pueden mantener sus parámetros `VARCHAR`.

---

## Descartados

### 5. `mascota.sexo` → `ENUM('MACHO','HEMBRA')` (se queda `ENUM('M','H')`)
- **Por qué se propuso:** uniformidad con los demás ENUM, cuyo valor es el `name()` del enum de Java.
- **Por qué se descarta:** el DDL anterior ya tenía `sexo CHAR(1)` con `'M'`/`'H'` y V2 solo lo volvió `ENUM('M','H')`. `MascotaDAOImpl` ya traduce `MACHO↔'M'` y `HEMBRA↔'H'`, y esa traducción funciona (el Tester de Mascota pasó). Cambiarlo obligaría a tocar el procedure (`p_sexo CHAR(1)` truncaría `'MACHO'`) y el DAO, sin ganar nada funcional.

### 10. `UNIQUE (id_detalle_cita)` en `detalle_boleta_serv` (no se agrega; lo controla el BL)
- **Qué buscaba:** que un servicio de una cita (`detalle_cita`) no se cobre dos veces, ni en una misma boleta ni en dos distintas.
- **Por qué se descarta:** un `UNIQUE` también bloquearía un caso normal: si una boleta se **anula** y hay que emitir otra corrigiendo un error (por ejemplo el método de pago), la línea de la boleta anulada sigue en la tabla y el `UNIQUE` impediría volver a cobrar ese mismo servicio. MySQL no permite un `UNIQUE` que valga solo para boletas no anuladas.
- **Decisión:** el BL comprueba, antes de agregar la línea, que el `detalle_cita` no esté en una boleta **no anulada**.

### 11. `UNIQUE (id_detalle_cita)` en `atencion_medica` (no se agrega; lo controla el BL)
- **Qué buscaba:** reflejar la regla de negocio "cada servicio pedido (`detalle_cita`) se ejecuta en una sola atención" (relación 1 a 1) y evitar dos atenciones del mismo servicio.
- **Por qué se descarta:** mismo problema que el cambio 10. Si una atención se **anula** (`anulado = 1`) por un error de registro, el servicio sigue sin ejecutarse y habría que registrarlo de nuevo, pero la atención anulada seguiría ocupando ese `id_detalle_cita` y el `UNIQUE` lo bloquearía.
- **Decisión:** el BL comprueba, antes de registrar una atención, que ese `detalle_cita` no tenga otra atención **no anulada**.

### 12. Estado también en `detalle_cita` (no se agrega)
- **Qué buscaba:** reflejar que los servicios de una cita se ejecutan por separado (el baño puede estar hecho y la vacuna pendiente).
- **Por qué se descarta:** el estado de cada servicio se puede **deducir** de lo que ya existe, sin columna nueva:
  - *pendiente:* el `detalle_cita` no tiene ninguna atención con `anulado = 0`;
  - *ejecutado:* tiene una atención con `anulado = 0`;
  - *cancelado individualmente:* su `activo = 0` (si se aprueba la propuesta 9).
  La cita pasa a `COMPLETADO` cuando todos sus servicios activos están ejecutados, y eso lo calcula el BL al registrar la última atención. `APROBADO` y `RECHAZADO` son decisiones sobre la cita completa, no sobre cada servicio. Una columna extra duplicaría información de `atencion_medica` y podría desincronizarse.

---

## Para que decida el compañero que corrige el diagrama

### 7. `servicio.tipo_servicio_medico`: agregar `ESTETICO` y `EMERGENCIA`
- **Situación:** la base (V2) solo permite `CONSULTA_MEDICA`, `OPERACION`, `VACUNACION`; el Java (`TipoServicio`) tiene además `EMERGENCIA` y `ESTETICO`. Guardar un servicio de esos dos tipos daría `Data truncated`. V2 parece venir de una versión anterior a la corrección que hicimos en `ServicioDAOImpl`.
- **Nuestra recomendación:** ampliar el ENUM a `('CONSULTA_MEDICA','OPERACION','VACUNACION','EMERGENCIA','ESTETICO')`. `ESTETICO` es necesario (un baño es un servicio estético y se menciona como ejemplo de servicio de una cita); `EMERGENCIA` es opcional porque ahora existe `cita.es_emergencia`, pero conviene dejarlo para que base y Java coincidan sin tocar código.
- **Costo:** nulo en Java y procedures (`p_servicio_medico` es `VARCHAR(45)`).
- **Estado:** se deja documentado para que lo resuelva él al corregir el diagrama.

### 8. ¿Se eliminan `tratamiento` y `atencion_tratamiento`?
- **Situación:** V2 no tiene esas dos tablas, y `insumo_utilizado` ya no cuelga de `atencion_tratamiento` sino directo de `atencion_medica`. En los audios no se menciona; parece una decisión de diseño, pero no está confirmada.
- **Qué eran en el Java:** `Tratamiento` es un catálogo de procedimientos (`nombreProcedimiento`, `descripcion`); `AtencionTratamiento` registra qué tratamientos se aplicaron en una atención, con sus insumos.
- **Por qué podría ser intencional:** según la lógica del negocio, la atención es la ejecución de **un servicio** pedido en la cita. Si el servicio ya dice qué se hace (consulta, operación, vacunación, baño), un catálogo aparte de tratamientos duplica la idea.
- **Qué arrastra si se eliminan (Java a retirar o adaptar, ~14 archivos):**
  - Modelo: `Tratamiento`, `AtencionTratamiento`; referencias en `AtencionMedica`.
  - Persistencia: `TratamientoDAO`/`Impl`, `AtencionTratamientoDAO`/`Impl`; referencias en `AtencionMedicaDAOImpl` e `InsumoUtilizadoDAOImpl`.
  - Negocio: `TratamientoBL`/`Impl`.
  - SQL: `procedures-tratamiento.sql`, `procedures-atencion_tratamiento.sql`, y la línea de `limpiar-tablas.sql`.
  - App: `probarTratamiento` del Tester y referencias en `Principal`.
- **Alternativas:**
  - **A. Aceptar que se eliminan** (coherente con V2 y con los audios). Cada dueño retira su Java (`Tratamiento`: Naim; `AtencionTratamiento`: Leonardo).
  - **B. Restituirlas:** volver a agregar `tratamiento` y `atencion_tratamiento` al diagrama y reconectar `insumo_utilizado`. Más trabajo y contradice la dirección de V2.
- **Nuestra recomendación:** A, pero debe confirmarse con el grupo y con los dueños de esos módulos antes de borrar código. Conviene revisar antes el catálogo de requerimientos por si menciona tratamientos como funcionalidad pedida.
- **Estado:** se deja documentado para que lo decida el compañero que corrige el diagrama.

### 9. Borrado lógico (pedido de la JP) con las tablas de V2
**El problema.** La JP pidió que **todos los deletes sean lógicos** (marcar el registro como inactivo en vez de borrarlo). V2 dejó **12 tablas sin columna `activo`**, pero **todas las clases del modelo Java extienden `Registro`**, que trae `id` y `activo`. Esas tablas no se pueden desactivar y, además, sus clases fallarían al leer o insertar buscando una columna que no existe.

**Cómo es el estado de las 23 tablas en V2.**

| Grupo | Tablas | Cómo se "elimina" |
|---|---|---|
| Con `activo` (11) | `administrador`, `articulo`, `categoria`, `cliente`, `cuenta`, `diagnostico`, `horario`, `mascota`, `receta`, `servicio`, `veterinario` | `UPDATE ... SET activo = 0` |
| Con indicador propio (3) | `boleta`, `atencion_medica` (`anulado`), `cita` (`estado`) | `anulado = 1` / `estado = 'CANCELADO'` |
| Sin ningún indicador (9) | `detalle_cita`, `detalle_receta`, `detalle_boleta_art`, `detalle_boleta_serv`, `atencion_diagnostico`, `insumo_utilizado`, `articulo_categoria`, `horario_administrador`, `horario_veterinario` | por definir |

**Cómo se elimina hoy en el código (patrón que hay que respetar).** Es el patrón maestro–detalle que mostró el profesor: al eliminar un maestro, **primero se eliminan sus detalles y al final el maestro, en una sola transacción manejada desde el BL**. Ya existe así en `CitaDAOImpl.delete` (detalles de la cita y luego la cita) y en `eliminar_detalles_por_receta` (que pone `activo = 0` en los detalles de la receta). Es además lo que demuestra el requisito de la rúbrica sobre transacciones.

**Por qué es un problema con V2.** Con borrado lógico, esa cascada también tiene que ser lógica: marcar los detalles como inactivos y luego el maestro. Pero si los detalles **no tienen `activo`**, la única forma de "eliminarlos" es borrarlos de verdad mientras el maestro queda ahí (inactivo). Eso destruye el historial: una **boleta anulada se quedaría sin sus líneas**, una cita cancelada sin sus servicios, una atención anulada sin diagnósticos ni insumos.

**Propuesta.**
1. **Agregar `activo TINYINT(1) NOT NULL DEFAULT 1` a las tablas detalle que cuelgan de un maestro:** `detalle_cita`, `detalle_receta`, `detalle_boleta_art`, `detalle_boleta_serv`, `atencion_diagnostico`, `insumo_utilizado`.
   - *Porqué:* la cascada lógica necesita marcar cada línea como inactiva; sin la columna solo se puede borrar la línea y se pierde el historial.
2. **Eliminar = cascada lógica en una sola transacción desde el BL:** (1) `activo = 0` en los detalles; (2) `activo = 0` / `anulado = 1` / `estado = 'CANCELADO'` en el maestro.
   - *Porqué:* es el patrón que ya usa el proyecto; la transacción evita hijos activos con padre desactivado si algo falla a la mitad.
3. **Los enlaces puros quedan sin `activo` y se borran con `DELETE`:** `articulo_categoria`, `horario_administrador`, `horario_veterinario`.
   - *Porqué:* solo dicen "este artículo está en esta categoría" o "este veterinario tiene este horario"; no tienen historia propia, borrarlos no pierde nada y evita filas muertas.
4. **Los maestros con indicador propio lo conservan:** `boleta` y `atencion_medica` con `anulado = 1`, `cita` con `estado = 'CANCELADO'`.
   - *Porqué:* anular una boleta o atención y cancelar una cita expresan mejor el negocio que un `activo = 0`, y esos registros no se eliminan, se invalidan.

**Decisión pendiente (la más importante para el Java).** `boleta`, `atencion_medica` y `cita` no tienen `activo`, pero sus clases heredan de `Registro`. Dos caminos:
- **A. Agregar también `activo`** a esas tres tablas: el Java queda sin cambios, pero la tabla tendría `activo` y `anulado`/`estado` a la vez (redundante).
- **B. Cambiar la jerarquía en Java:** que `Registro` no obligue a `activo` en esas tres clases (por ejemplo, una base solo con `id`). Más limpio en el modelo, pero toca clases de varios módulos.

**Cómo afecta a cada quien (procedures, después del diagrama).** Los procedures `eliminar_*` de maestros y detalles pasan a `UPDATE`; los de detalles necesitan el parámetro/columna `activo`; los enlaces puros siguen con `DELETE`.

**Estado:** se deja para que lo decida el compañero que corrige el diagrama.

---

## En análisis

_Ninguno por ahora._ Los 3 cambios que planteó tu compañero (1, 2 y 3) ya están hechos en el diagrama.

---

## Para dejar el diagrama bien

- Pasar nombres de tablas, columnas, índices y FKs del `.mwb` a minúscula (hoy sigue en mayúscula).
- Una sola persona edita el `.mwb` a la vez (es binario; git no puede fusionarlo).
