# Cambios al diagrama V2 (`diagrama-fisico_patitasV2.mwb`)

Regla: cada cambio se hace **primero en el diagrama**; luego se genera el DDL (Forward Engineer), se pasa a minúscula y recién después se adaptan procedures y Java.

Lógica de negocio que guía los cambios:
- Una **cita** es un contenedor. Cada servicio pedido es un `detalle_cita`, que lleva su servicio y el veterinario asignado.
- La **atención** es la ejecución de un `detalle_cita` (relación **1 a 1**). Una cita tiene varias atenciones.
- **Todo** servicio, también los estéticos, se ejecuta en una atención. Por eso la tabla `atencion_medica` pasó a llamarse `atencion`.

Estado: el DDL de referencia es `ddl-prueba` **v4** (revisión del 26/10/2026, al final de este archivo), con los ajustes de "Para dejar el diagrama bien".

---

## Hechos

### 1. El veterinario pasa de `cita` a `detalle_cita`
- **Por qué:** en una misma cita, cada servicio puede hacerlo un veterinario distinto (baño con uno, chequeo con otro). Con el veterinario en `cita` solo cabe uno por cita completa.
- **Historia:** primero se movió a `atencion_medica`. En la revisión v1 → v4 pasó a `detalle_cita`, porque el veterinario se asigna **al reservar** cada servicio, también los estéticos. Así se puede validar su disponibilidad contra `horario_veterinario` antes de la atención.
- **Cambio en el diagrama:**
  - `cita`: sin `id_veterinario`. Quedan `id_cita`, `id_administrador`, `id_mascota`, `fecha_hora`, `estado`, `es_emergencia`, `activo`.
  - `detalle_cita`: `id_veterinario INT NOT NULL`, con FK a `veterinario`.
  - `atencion`: **sin** `id_veterinario`, para no tener dos veterinarios que puedan contradecirse. Si se reasigna el veterinario, se actualiza en `detalle_cita`.
- **Decisiones:** obligatorio (`NOT NULL`); `horario_veterinario` no cambia de rol.
- **Java que arrastra (después):** `Cita`, `DetalleCita` y `Atencion` (antes `AtencionMedica`): modelo, DAO, BL, procedures y Tester.

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

**Decisión de diseño.** La línea guarda `id_articulo` y también `id_insumo_utilizado` (el insumo ya conoce su artículo). Se acepta esa pequeña redundancia a cambio de no tocar las ventas directas; el BL validará que ambos coincidan. `id_articulo` nunca queda vacío; solo `id_insumo_utilizado` es nullable.

**Por qué no hay una tabla `detalle_boleta_insu` aparte.** Un insumo cobrado sigue siendo la venta de un artículo. Una tabla extra obligaría a sumar tres tablas para el total y a usar `UNION` para listar las líneas.

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

**El `UNIQUE (id_insumo_utilizado)` se mantiene** (decisión de la revisión v4, igual que en los cambios 10 y 11). Consecuencia: la línea de una boleta anulada (`activo = 0`) sigue ocupando ese `id_insumo_utilizado`, así que la base no deja volver a cobrar ese insumo en una boleta nueva. Si hay que re-facturar tras anular, falta definir el procedimiento.

### 3. Quitar `id_mascota` de `atencion` (antes `atencion_medica`)
- **Por qué:** es redundante; la mascota se obtiene por `detalle_cita → cita → mascota`. Tener las dos rutas permitía datos contradictorios (atención de una mascota enlazada a la cita de otra). Toda atención tiene `id_detalle_cita NOT NULL`, así que no hay atenciones sueltas que necesiten mascota propia.
- **Cambio hecho en el diagrama:** se quitó `id_mascota`, su FK y su índice de la atención. Ahora solo `cita` apunta a `mascota`.
- **Java que arrastra (después):** `Atencion` obtiene la mascota vía la cita; procedures de búsqueda por mascota necesitarán `JOIN`.

### 4. Corregir la expresión de `subtotal`
- **Por qué:** en `detalle_boleta_art` y `detalle_boleta_serv`, `subtotal` es una columna calculada por MySQL. La fórmula estaba guardada con backticks sobrantes (``CANTIDAD` * `PRECIO_UNITARIO``), y al generar el DDL salía SQL inválido que MySQL rechazaba. Cada vez que se regenerara el script habría que arreglarlo a mano.
- **Cambio hecho en el diagrama:** en las dos tablas la expresión ahora es `cantidad * precio_unitario` (sin backticks). En la v4 el almacenamiento es `VIRTUAL` (antes `STORED`); para el Java da igual.
- **Java que arrastra (después):** los procedures de `detalle_boleta_*` **no deben insertar ni actualizar `subtotal`** (MySQL lo calcula); el modelo solo lo lee.

### 5. `mascota.sexo` → `ENUM('MACHO','HEMBRA')`
- **Por qué:** uniformidad con los demás ENUM, cuyo valor es el `name()` del enum de Java. Antes se había descartado para conservar `ENUM('M','H')`; se revirtió en la revisión v4.
- **Cambio en el diagrama:** `sexo ENUM('MACHO','HEMBRA') NOT NULL` (ya está así en la v4).
- **Java que arrastra (después):** `MascotaDAOImpl` deja de traducir `MACHO↔'M'` y `HEMBRA↔'H'` y guarda o lee con `name()`/`valueOf()`. En `procedures-mascota.sql`, `p_sexo CHAR(1)` pasa a `VARCHAR(6)` (con `CHAR(1)` se truncaría `'MACHO'`).

### 6. `nivel_gravedad` y `dia_semana` pasan de `VARCHAR` a `ENUM`
- **Por qué:** en Java ya son enums (`NivelGravedad`, `DiaSemana`) y los DAOs los guardan con `name()` y leen con `valueOf()`. Con `VARCHAR`, la base aceptaba cualquier texto y un valor inválido solo fallaba después, al leerlo con `valueOf()`. Con `ENUM`, la base rechaza lo que no sea válido y el diagrama documenta los valores permitidos. Es coherente con los demás ENUM (`tipo_mascota`, `metodo_pago`, `estado`, `tipo_servicio`).
- **Cambio hecho en el diagrama:**
  - `atencion_diagnostico.nivel_gravedad` → `ENUM('LEVE','MODERADO','GRAVE','CRITICO')`, **obligatorio** (`NOT NULL`, desde la v4).
  - `horario.dia_semana` → `ENUM('LUNES','MARTES','MIERCOLES','JUEVES','VIERNES','SABADO','DOMINGO')`, sigue obligatorio.
- **Java que arrastra (después):** los DAOs ya usan `name()`/`valueOf()` y los procedures pueden mantener sus parámetros `VARCHAR`. El BL debe exigir `nivelGravedad` al registrar un diagnóstico.

### 7. `servicio.tipo_servicio`: valores definitivos
- **Situación:** V2 solo permitía `CONSULTA_MEDICA`, `OPERACION`, `VACUNACION`; el Java (`TipoServicio`) tenía además `EMERGENCIA` y `ESTETICO`. Guardar un servicio de esos dos tipos daba `Data truncated`.
- **Decisión:** la columna se renombra a `tipo_servicio` con `ENUM('CONSULTA_MEDICA','OPERACION','VACUNACION','ESTETICA','EMERGENCIA')`.
  - `ESTETICA` (no `ESTETICO`, ni `BAÑO`: la Ñ da problemas de encoding entre MySQL, JDBC y Java).
  - `EMERGENCIA` se mantiene para que base y Java coincidan, aunque exista `cita.es_emergencia`.
  - `DESPARASITACION` **no** va. La v4 la tiene: hay que quitarla del diagrama (ver "Para dejar el diagrama bien").
- **Java que arrastra (después):** en `TipoServicio`, `ESTETICO` pasa a `ESTETICA`. Costo nulo en procedures (`p_servicio_medico` es `VARCHAR(45)`), salvo renombrar la columna en los `INSERT`/`UPDATE`/`SELECT`.

### 8. Se eliminan `tratamiento` y `atencion_tratamiento`
- **Decisión:** alternativa A, confirmada por la v4, que no tiene esas tablas. `insumo_utilizado` cuelga directo de `atencion`.
- **Por qué:** la atención es la ejecución de **un servicio** pedido en la cita. Si el servicio ya dice qué se hace (consulta, operación, vacunación, baño), un catálogo aparte de tratamientos duplica la idea.
- **Java que arrastra (después), ~14 archivos a retirar o adaptar:**
  - Modelo: `Tratamiento`, `AtencionTratamiento`; referencias en `AtencionMedica`.
  - Persistencia: `TratamientoDAO`/`Impl`, `AtencionTratamientoDAO`/`Impl`; referencias en `AtencionMedicaDAOImpl` e `InsumoUtilizadoDAOImpl`.
  - Negocio: `TratamientoBL`/`Impl`.
  - SQL: `procedures-tratamiento.sql`, `procedures-atencion_tratamiento.sql`, y la línea de `limpiar-tablas.sql`.
  - App: `probarTratamiento` del Tester y referencias en `Principal`.
  - Cada dueño retira su Java (`Tratamiento`: Naim; `AtencionTratamiento`: Leonardo). Conviene revisar antes el catálogo de requerimientos por si menciona tratamientos como funcionalidad pedida.

### 9. Borrado lógico (pedido de la JP)
**El problema.** La JP pidió que **todos los deletes sean lógicos** (marcar el registro como inactivo en vez de borrarlo). V2 dejó varias tablas sin columna `activo`, pero **todas las clases del modelo Java extienden `Registro`**, que trae `id` y `activo`.

**Decisión (v4).** **Todas las tablas tienen `activo TINYINT(1) NOT NULL DEFAULT 1`**:
- **Maestros y catálogos:** "eliminar" es `activo = 0`.
- **`boleta` y `atencion`:** `activo` **reemplaza** a `anulado`. Una boleta o atención anulada tiene `activo = 0`.
- **`cita`:** conserva `estado` (`PENDIENTE`, `APROBADO`, `RECHAZADO`, `CANCELADO`, `COMPLETADO`) y además tiene `activo`.
- **Detalles** (`detalle_cita`, `detalle_receta`, `detalle_boleta_art`, `detalle_boleta_serv`, `atencion_diagnostico`, `insumo_utilizado`): `activo`, para la cascada lógica.
- **Enlaces** (`articulo_categoria`, `horario_administrador`, `horario_veterinario`): también llevan `activo` y se desactivan con `UPDATE`, no con `DELETE`. Como su PK es compuesta, volver a agregar un enlace desactivado es **reactivarlo** (`UPDATE activo = 1`), no insertarlo de nuevo.

Con esto se resuelve la "decisión pendiente" anterior: `Registro` no cambia en Java, y no hay redundancia `activo` + `anulado`.

**Cómo se elimina (patrón que hay que respetar).** Es el patrón maestro–detalle que mostró el profesor: al eliminar un maestro, **primero se desactivan sus detalles y al final el maestro, en una sola transacción manejada desde el BL**. Ya existe así en `CitaDAOImpl.delete` y en `eliminar_detalles_por_receta`. Es además lo que demuestra el requisito de la rúbrica sobre transacciones.

**Cómo afecta a cada quien (procedures, después del diagrama).** Todos los procedures `eliminar_*` pasan a `UPDATE ... SET activo = 0`, también los de enlaces. Los de `boleta` y `atencion` usan `activo` en lugar de `anulado`.

### 10. `UNIQUE (id_detalle_cita)` en `detalle_boleta_serv`
- **Qué busca:** que un servicio de una cita (`detalle_cita`) no se cobre dos veces, ni en una misma boleta ni en dos distintas.
- **Historia:** antes se había descartado para controlarlo en el BL. En la revisión v4 se decidió **ponerlo en la base**.
- **Consecuencia aceptada:** si una boleta se anula (`activo = 0`), su línea sigue ocupando ese `id_detalle_cita`, y la base no deja volver a cobrar ese servicio en una boleta nueva. MySQL no permite un `UNIQUE` que valga solo para filas activas. Falta definir el procedimiento si hay que re-facturar.

### 11. `UNIQUE (id_detalle_cita)` en `atencion`: relación 1 a 1
- **Qué busca:** reflejar la regla "cada servicio pedido (`detalle_cita`) se ejecuta en una sola atención".
- **Cambio en el diagrama:**
  - `detalle_cita`: **sin** `id_atencion`.
  - `atencion`: `id_detalle_cita INT NOT NULL` con `UNIQUE` y FK a `detalle_cita`.
  - *Por qué la FK va en `atencion`:* en una 1 a 1 basta una FK de un solo lado, y va en la tabla que se crea después. Con la FK en `detalle_cita` y `NOT NULL`, no se podía reservar un servicio sin crear antes la atención. Tener la FK en ambos lados duplicaría la relación y crearía una FK circular.
- **Historia:** antes se había descartado el `UNIQUE` para controlarlo en el BL. En la revisión v4 se decidió **ponerlo en la base**.
- **Consecuencia aceptada:** si una atención se anula (`activo = 0`), sigue ocupando ese `id_detalle_cita`, y la base no deja registrar otra atención para ese servicio. Para corregir un registro erróneo hay que **editar** la atención existente o reactivarla, no crear otra.

---

## Descartados

### 12. Estado también en `detalle_cita` (no se agrega)
- **Qué buscaba:** reflejar que los servicios de una cita se ejecutan por separado (el baño puede estar hecho y la vacuna pendiente).
- **Por qué se descarta:** el estado de cada servicio se puede **deducir** de lo que ya existe, sin columna nueva:
  - *pendiente:* el `detalle_cita` no tiene atención con `activo = 1`;
  - *ejecutado:* tiene una atención con `activo = 1`;
  - *cancelado individualmente:* su propio `activo = 0`.
  La cita pasa a `COMPLETADO` cuando todos sus servicios activos están ejecutados, y eso lo calcula el BL al registrar la última atención. `APROBADO` y `RECHAZADO` son decisiones sobre la cita completa, no sobre cada servicio. Una columna extra duplicaría información de `atencion` y podría desincronizarse.

---

## Revisión del DDL: `ddl-prueba` v1 → v4 (26/10/2026)

Resumen de todo lo que cambió entre la v1 y la v4 del DDL. Las decisiones que modifican cambios anteriores ya están integradas arriba (cambios 1, 2, 4, 5, 6, 7, 8, 9, 10 y 11).

### Cambios generales
| Cambio | v1 | v4 |
|---|---|---|
| Nombre del esquema | `mydb` | `patitasarriba` |
| Nombres de tablas, columnas e índices | mezcla de mayúsculas y minúsculas | todo en minúscula |
| `activo` | faltaba en varias tablas; en `atencion` tenía `DEFAULT 0` | presente en todas las tablas, con `DEFAULT 1` (cambio 9) |

### Cambios por tabla

**`detalle_cita` y `atencion`**
- `detalle_cita`: se quitó `id_atencion`. Se agregaron `id_servicio INT NOT NULL` e `id_veterinario INT NOT NULL`, con sus FK (cambios 1 y 11).
- `atencion`: se agregó `id_detalle_cita INT NOT NULL` con `UNIQUE` (cambio 11). Se quitaron `id_servicio` e `id_veterinario`.
  - *Por qué `id_servicio` pasa a `detalle_cita`:* el servicio se elige al reservar, no al atender, y `detalle_boleta_serv` cobra desde `detalle_cita`.
- `atencion.motivo_consulta` pasó a ser `NULL`: una atención estética no tiene motivo de consulta. El BL lo exige solo en servicios médicos.

**`servicio`**
- `TIPO_SERVICIO_MEDICO` → `tipo_servicio` (cambio 7).
- Se eliminaron `requiere_triaje` y `requiere_vacuna`. No había tablas donde registrar el triaje ni las vacunas de la mascota.
- Se agregó `UNIQUE (nombre)`.

**`cita`**: se agregó `id_administrador INT NULL` con FK a `administrador`: quién aprobó o rechazó la cita (vacío mientras está `PENDIENTE`).

**`boleta`**
- `ANULADO` → `activo` (cambio 9).
- Se agregó `serie_correlativo VARCHAR(15) NOT NULL` con `UNIQUE` (por ejemplo `B001-000123`).
- `fecha` pasó de `DATE` a `DATETIME`, para los reportes de caja.

**`detalle_boleta_art`**: `subtotal` `VIRTUAL` (cambio 4); se agregó `activo`. Se mantiene `UNIQUE (id_insumo_utilizado)` (cambio 2).

**`detalle_boleta_serv`**: `UNIQUE (id_detalle_cita)` (cambio 10); `subtotal` `VIRTUAL`; se agregó `activo`.

**`atencion_diagnostico`**
- Se eliminó la PK propia (`ID_ATENCION_DIAGNOSTICO`). Ahora la **PK es compuesta: `(id_atencion, id_diagnostico)`**, para impedir el mismo diagnóstico dos veces en una atención.
- `ID_ATENCION_MEDICA` → `id_atencion`.
- `nivel_gravedad` `NOT NULL` (cambio 6); se agregó `activo`.

**`horario_administrador` y `horario_veterinario`**: se eliminaron las PK propias (`id_horario_admin`, `id_horario_vet`). Ahora la **PK es compuesta** (`(id_administrador, id_horario)` y `(id_veterinario, id_horario)`), con el mismo criterio que `articulo_categoria`.
- **Java:** estas tablas y `atencion_diagnostico` ya no tienen `id` autogenerado. Sus DAO buscan, actualizan y eliminan por las dos claves, y no usan `getGeneratedKeys()` al insertar.

**`articulo_categoria`**, **`insumo_utilizado`** y **`detalle_receta`**: se agregó `activo`.

**`receta`**: `ID_ATENCION_MEDICA` → `id_atencion`.

**`diagnostico`**: `nombre_enfermedad` pasó de `VARCHAR(45)` a `VARCHAR(80)`, con `UNIQUE`.

**`mascota`**: la columna `tipo mascota`, que tenía un espacio, pasó a `tipo_mascota`. `sexo` queda `ENUM('MACHO','HEMBRA')` (cambio 5).

**`administrador`**: `nombre` → `nombres`, igual que en `cliente` y `veterinario`.

**`cliente`, `administrador`, `veterinario`**: se mantiene `UNIQUE (id_cuenta)`. Se perdió en una versión intermedia y se restauró. Una cuenta pertenece a una sola persona.

### Reglas que quedan en el BL
La base no puede validar estas reglas, porque dependen de columnas de otras tablas.
- `atencion_diagnostico`: solo en atenciones cuyo servicio sea `CONSULTA_MEDICA`. El tipo se obtiene con `atencion → detalle_cita → servicio`.
- `receta`: solo en servicios médicos, nunca en `ESTETICA`.
- `motivo_consulta`: obligatorio en servicios médicos.
- Insumos en la boleta:
  - Solo se cobran los que tienen `es_cobrable = 1`.
  - `cantidad` no debe superar `cantidad_utilizada`.
  - `id_articulo` de la línea debe coincidir con el del insumo.
- La boleta debe ser del dueño de la mascota de la cita.
- `boleta.total` es la suma de los subtotales de sus líneas.
- El stock se descuenta al registrar el insumo en la atención o al vender en caja.

### Opcionales (no aplicados en v4)
- Agregar `CHECK`:
  - `cantidad > 0` y `precio_unitario >= 0` en los detalles de boleta.
  - `hora_fin > hora_inicio` en `horario`.
  - `cantidad_utilizada > 0` en `insumo_utilizado`.
  - `stock_minimo >= 0` en `articulo`.
- `UNIQUE (dia_semana, hora_inicio, hora_fin)` en `horario`.
- Cambiar `utf8` por `utf8mb4` en el esquema.
- Quitar índices redundantes generados por Workbench: los `fk_*_idx` que ya cubre un `UNIQUE` o la PK compuesta.
- FK compuesta `(id_insumo_utilizado, id_articulo)` → `insumo_utilizado`, para que la base valide que coincidan el artículo y el insumo. Requiere `UNIQUE (id_insumo_utilizado, id_articulo)` en `insumo_utilizado`.

---

## En análisis

_Ninguno por ahora._

---

## Para dejar el diagrama bien

- **`servicio.tipo_servicio`:** quitar `DESPARASITACION` y agregar `EMERGENCIA` (cambio 7). La v4 todavía tiene `DESPARASITACION`.
- ~~Pasar nombres de tablas, columnas, índices y FKs del `.mwb` a minúscula.~~ Hecho en la v4.
- Una sola persona edita el `.mwb` a la vez (es binario; git no puede fusionarlo).
