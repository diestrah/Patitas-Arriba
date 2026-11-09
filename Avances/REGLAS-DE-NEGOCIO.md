# Reglas de Negocio — Patitas Arriba

Sistema de gestión para una clínica veterinaria de perros y gatos: clientes y sus mascotas, agenda de citas, atención médica y estética, recetas, inventario de artículos y pago en caja (boletas).

**Cómo leer este documento**

- Cada regla tiene un código (`RN-XX-nn`) para citarla en requisitos, pruebas y reportes de errores. Los códigos no se reutilizan: una regla nueva toma el siguiente número libre de su sección.
- Salvo que se indique otra cosa, las reglas se validan en la capa de negocio (`patitasarriba-negocio`). Las marcadas con **[BD]** también están protegidas por una restricción de la base de datos.
- Las reglas marcadas con **[Rol]** definen **quién** puede hacer una operación. Las aplica el frontend y la capa REST; la capa de negocio no conoce al usuario que la invoca.

---

## 1. Conceptos clave

| Concepto | Qué es |
|---|---|
| **Servicio** | Elemento del **catálogo** (por ejemplo, "Consulta general" o "Baño y corte"), con tipo, precio base y duración estimada. No es algo que ocurrió: muchas citas y atenciones pueden referirse al mismo servicio. |
| **Cita** | Solicitud de atención para **una mascota** en una fecha y hora. Agrupa uno o más servicios solicitados. |
| **DetalleCita** (servicio solicitado) | Cada servicio que el cliente pidió dentro de una cita, con el **veterinario** asignado. Una cita puede incluir varias veces el mismo servicio del catálogo. |
| **Atención** (servicio ejecutado) | Registro de lo que se hizo al realizar **un DetalleCita**: motivo, observaciones, diagnósticos e insumos. Cada DetalleCita tiene como máximo una atención. |
| **Diagnóstico** | Enfermedad del **catálogo** de diagnósticos. En una atención se registra con su nivel de gravedad. |
| **Insumo utilizado** | Artículo del inventario consumido durante una atención. Descuenta stock al registrarse y puede ser **cobrable** o no. |
| **Receta** | Indicaciones y medicamentos (artículos) que el veterinario prescribe en una atención médica. No mueve stock. |
| **Artículo** | Producto del inventario (medicamento, alimento, accesorio...), con precio base y stock. |
| **Boleta** | Comprobante del pago en caja de un cliente. |
| **Línea de servicio** (DetalleBoletaServ) | Línea de boleta que cobra **un DetalleCita ya atendido**. |
| **Línea de artículo** (DetalleBoletaArt) | Línea de boleta que cobra un **insumo cobrable** de una atención, o un artículo vendido **en caja** (compra extra o lo recetado). |

---

## 2. Proceso de negocio

### 2.1 Actores y responsabilidades [Rol]

| Actor | Responsabilidades |
|---|---|
| **Cliente** | Solicita citas para sus mascotas, eligiendo servicios y veterinarios según la disponibilidad. Puede modificar sus citas mientras están PENDIENTE y cancelarlas mientras no se haya atendido ningún servicio. Paga en caja. |
| **Administrador** | Gestiona las citas: las aprueba o rechaza, las registra en nombre del cliente, las reprograma, las cancela, registra inasistencias, quita servicios no realizados y las marca como completadas. Gestiona catálogos, inventario, personal y horarios. Emite las boletas en caja. |
| **Veterinario** | Registra la **atención** de cada servicio que realizó (diagnósticos e insumos utilizados) y emite las recetas. |

### 2.2 Flujo principal

1. **Solicitud.** El cliente solicita una cita para una de sus mascotas, indica la fecha y hora y elige los servicios que necesita. Para cada servicio elige un veterinario que esté disponible en ese horario. Cada servicio elegido es un DetalleCita. La cita queda **PENDIENTE**.
2. **Revisión.** El administrador **aprueba** o **rechaza** la cita. Mientras esté PENDIENTE o APROBADO, el cliente puede **cancelarla**.
3. **Atención.** El día de la cita, la mascota es atendida. Al terminar cada servicio, el veterinario asignado registra su **atención**: motivo de consulta, observaciones, diagnósticos (solo en servicios médicos) e insumos utilizados. Los insumos se descuentan del stock en ese momento. Si corresponde, emite una **receta**.
4. **Pago en caja.** El cliente se acerca a caja y el administrador emite la **boleta**. El sistema la propone con lo pendiente de cobro (RN-BO-22), y puede cobrar:
   - las **atenciones** realizadas, con una línea de servicio por cada DetalleCita atendido;
   - los **insumos utilizados cobrables** de esas atenciones;
   - **artículos** que el cliente quiera comprar, sean extras o lo indicado en una receta (venta en caja, que descuenta stock).
5. **Cierre.** Cuando todos los servicios de la cita tienen su atención, el administrador marca la cita como **COMPLETADO**. Desde ese momento sus atenciones ya no se modifican.

### 2.3 Diagrama

```
 Cliente                  Administrador               Veterinario                 Caja (Administrador)
 ───────                  ─────────────               ───────────                 ────────────────────
 Solicita cita ──────────► PENDIENTE
 (servicios +              │
  veterinarios)            ├─► RECHAZADO (fin)
                           ├─► CANCELADO (cliente o admin)
                           ▼
                        APROBADO ───────────────────► Atención por cada DetalleCita
                           │                            ├─ Diagnósticos (médicos)
                           │                            ├─ Insumos (descuentan stock)
                           │                            └─ Receta (médicos)
                           │                                    │
                           │                                    ▼
                           │                          Boleta al dueño de la mascota:
                           │                            · servicios atendidos
                           │                            · insumos cobrables
                           │                            · artículos (venta en caja)
                           ▼
                        COMPLETADO (todos los servicios atendidos)
```

### 2.4 Situaciones alternativas

| Situación | Cómo se resuelve |
|---|---|
| El cliente ya no quiere la cita | El cliente (o el administrador) la **cancela**, siempre que no haya ningún servicio atendido (RN-CI-10). |
| La clínica no puede atenderla | El administrador la **rechaza** (si está PENDIENTE) o la **cancela** (si está APROBADO). |
| El cliente no asiste | El administrador registra la inasistencia **cancelando** la cita (RN-CI-17). |
| Un servicio solicitado no se realizó | El administrador **quita ese DetalleCita** de la cita, si no tiene atención ni cobro (RN-CI-16). Así la cita puede completarse. |
| Emergencia | Se registra al llegar el paciente, se atiende de inmediato y el administrador la aprueba después (RN-CI-11). |
| Compra sin cita | Se emite una boleta solo con artículos (venta en caja) a un cliente registrado (RN-BO-21). |
| Error en un cobro | Se **modifica** la boleta; anularla no libera sus servicios ni insumos para otro cobro (RN-BO-10, RN-BO-13). |
| Error en una atención | El veterinario **modifica** la atención mientras la cita no esté cerrada (RN-AT-10); anularla impide volver a registrarla (RN-AT-02). |

---

## 3. Reglas generales (aplican a todo el sistema)

| Código | Regla |
|---|---|
| RN-GE-01 | **Ningún registro se borra físicamente.** "Eliminar" significa dar de baja (borrado lógico): el registro queda inactivo y deja de aparecer en búsquedas y listados. |
| RN-GE-02 | **Dar de baja no borra el historial.** Un registro dado de baja ya no puede usarse en operaciones nuevas (citas, atenciones, recetas, ventas), pero los registros históricos que lo referencian (citas, atenciones, boletas, recetas) se siguen leyendo completos. |
| RN-GE-03 | Las operaciones que afectan varios registros a la vez (atención + stock, boleta + stock, cliente + mascotas, persona + cuenta) se ejecutan **todo o nada**: si una parte falla, no se guarda ninguna. |
| RN-GE-04 | Los valores únicos (usuario, correo, DNI, teléfono, colegiatura y los nombres de servicio, diagnóstico y categoría) **siguen ocupados aunque el registro esté dado de baja**: no se puede crear uno nuevo con el valor de uno inactivo. |
| RN-GE-05 | Algunas entidades no pueden darse de baja mientras tengan registros que dependan de ellas, porque esos registros quedarían huérfanos o inconsistentes (por ejemplo, un cliente con citas). Cada caso se detalla en su sección y se resume en §13. |
| RN-GE-06 | **El estado activo/inactivo solo cambia al dar de baja.** Registrar o modificar un registro nunca lo da de baja ni lo reactiva, y un registro dado de baja no puede modificarse. |

---

## 4. Cuentas y acceso

| Código | Regla |
|---|---|
| RN-CU-01 | Toda cuenta requiere nombre de usuario, contraseña y un correo con formato válido (`usuario@dominio.ext`). |
| RN-CU-02 | El nombre de usuario es único. **[BD]** |
| RN-CU-03 | El correo es único, sin distinguir mayúsculas de minúsculas. **[BD]** |
| RN-CU-04 | La contraseña nunca se guarda en texto plano: se almacena su hash SHA-256. Al modificar una cuenta, la contraseña solo se vuelve a cifrar si realmente cambió. |
| RN-CU-05 | Para iniciar sesión se requieren usuario y contraseña. Solo pueden ingresar cuentas activas de una persona activa. Al ingresar, el sistema identifica a la persona y, con ella, su rol (cliente, veterinario o administrador). |
| RN-CU-06 | Ante un acceso fallido el sistema responde siempre "Usuario o contraseña incorrectos", sin indicar cuál de los dos falló (evita que se descubran usuarios existentes). |
| RN-CU-07 | No se puede dar de baja una cuenta que esté siendo usada por un cliente, veterinario o administrador activo. |
| RN-CU-08 | Cada cuenta pertenece a una sola persona, sea cliente, veterinario o administrador: una misma cuenta no puede usarse para dos personas, ni siquiera de tipos distintos o si una de ellas está dada de baja. **[BD]** solo dentro de cada tipo; entre tipos lo valida la capa de negocio. |

---

## 5. Personas: clientes, veterinarios y administradores

### 5.1 Reglas comunes

| Código | Regla |
|---|---|
| RN-PE-01 | Toda persona debe tener una cuenta existente y activa. |
| RN-PE-02 | El DNI es obligatorio y tiene exactamente 8 dígitos numéricos. |
| RN-PE-03 | Nombres, apellido paterno y apellido materno son obligatorios. |
| RN-PE-04 | El teléfono es obligatorio y tiene exactamente 9 dígitos numéricos. |
| RN-PE-05 | DNI y teléfono son únicos dentro de cada tipo de persona (no puede haber dos clientes con el mismo DNI o teléfono, por ejemplo). Ambos siguen ocupados aunque la persona esté dada de baja. **[BD]** |
| RN-PE-06 | Al dar de baja a una persona, su cuenta se da de baja en la misma operación: deja de poder iniciar sesión (RN-CU-05). |

### 5.2 Cliente

| Código | Regla |
|---|---|
| RN-CL-01 | No se puede dar de baja a un cliente si alguna de sus mascotas tiene citas o si tiene boletas emitidas. |
| RN-CL-02 | Al dar de baja a un cliente, también se dan de baja todas sus mascotas. |
| RN-CL-03 | **[Rol]** Un cliente solo ve y opera sobre sus propias mascotas, citas, atenciones, recetas y boletas. |

### 5.3 Veterinario

| Código | Regla |
|---|---|
| RN-VE-01 | El número de colegiatura es obligatorio y único; sigue ocupado aunque el veterinario esté dado de baja. **[BD]** |
| RN-VE-02 | Un veterinario atiende según sus horarios asignados (ver §7). |
| RN-VE-03 | Un veterinario puede darse de baja aunque tenga citas terminadas (RN-GE-02): deja de poder asignarse a citas nuevas o reprogramadas, pero sus citas, atenciones y boletas anteriores se conservan. **No** puede darse de baja mientras tenga citas PENDIENTE o APROBADO: primero hay que reasignarlas, cancelarlas o rechazarlas. |

### 5.4 Administrador

| Código | Regla |
|---|---|
| RN-AD-01 | El administrador es quien aprueba o rechaza las citas (ver RN-CI-07). |
| RN-AD-02 | Siempre debe existir al menos un administrador activo: no se puede dar de baja al último. |
| RN-AD-03 | El administrador que aprueba o rechaza una cita debe existir y estar activo. |
| RN-AD-04 | El primer administrador lo registra la clínica al instalar el sistema (script `sql/datos-iniciales.sql`); los demás usuarios los crea un administrador. |

---

## 6. Mascotas

| Código | Regla |
|---|---|
| RN-MA-01 | La clínica atiende **perros y gatos**. |
| RN-MA-02 | Toda mascota tiene nombre, sexo (macho / hembra), tipo, fecha de nacimiento y un dueño. La raza y el peso son opcionales. |
| RN-MA-03 | El dueño debe ser un cliente existente y activo. |
| RN-MA-04 | El peso, si se registra, debe ser mayor a 0 (nunca negativo). |
| RN-MA-05 | La fecha de nacimiento no puede ser futura. |
| RN-MA-06 | No se puede dar de baja una mascota que tenga citas registradas (en cualquier estado). |

---

## 7. Horarios del personal

| Código | Regla |
|---|---|
| RN-HO-01 | Un horario define un día de la semana (lunes a domingo), una hora de inicio y una hora de fin. |
| RN-HO-02 | La hora de inicio debe ser anterior a la hora de fin. |
| RN-HO-03 | Un horario puede asignarse a veterinarios y administradores. En los veterinarios determina cuándo pueden recibir citas (RN-CI-05); en los administradores es **solo informativo** (registra sus turnos) y no restringe ninguna operación. |
| RN-HO-04 | No se puede eliminar un horario que esté asignado a algún veterinario o administrador activo. Los horarios de personal dado de baja quedan libres. |
| RN-HO-05 | Los horarios que se asignan a una persona deben existir y estar activos. |

---

## 8. Catálogo: servicios, artículos y categorías

### 8.1 Servicios

| Código | Regla |
|---|---|
| RN-SE-01 | Todo servicio tiene nombre, descripción, tipo y precio base mayor a 0. |
| RN-SE-02 | Tipos de servicio: **Consulta médica, Operación, Vacunación, Emergencia** (servicios **médicos**) y **Estética** (servicio **no médico**). Esta distinción determina qué se puede registrar en la atención (ver §10). |
| RN-SE-03 | La duración estimada es opcional; si se indica, debe ser mayor a 0. Se usa para calcular la agenda de los veterinarios (RN-CI-05). |
| RN-SE-04 | El nombre del servicio es único. **[BD]** |
| RN-SE-05 | Un servicio puede darse de baja aunque haya sido incluido en citas (RN-GE-02): deja de poder agregarse a citas nuevas, pero los DetalleCita ya registrados se siguen atendiendo y cobrando. |

### 8.2 Categorías de artículo

| Código | Regla |
|---|---|
| RN-CA-01 | Toda categoría tiene nombre y descripción. |
| RN-CA-02 | El nombre de la categoría es único. **[BD]** |
| RN-CA-03 | No se puede dar de baja una categoría que tenga artículos asignados. |

### 8.3 Artículos (inventario)

| Código | Regla |
|---|---|
| RN-AR-01 | Todo artículo tiene nombre y precio base mayor a 0. |
| RN-AR-02 | Todo artículo pertenece a **al menos una** categoría existente; puede pertenecer a varias. |
| RN-AR-03 | El stock actual y el stock mínimo no pueden ser negativos. |
| RN-AR-04 | El stock **nunca** puede quedar negativo: toda operación que lo descuente se rechaza si no hay unidades suficientes, incluso si dos operaciones ocurren al mismo tiempo. |
| RN-AR-05 | El stock disminuye en dos situaciones: (a) cuando se usa como insumo en una atención, y (b) cuando se vende en caja. Aumenta cuando se revierten esas operaciones (insumo retirado, atención anulada, venta modificada o boleta anulada). |
| RN-AR-06 | Un artículo puede darse de baja aunque se haya usado en atenciones, recetas o boletas (RN-GE-02): deja de poder usarse como insumo nuevo, recetarse o venderse en caja, pero los insumos ya registrados se siguen cobrando y su stock puede devolverse. |
| RN-AR-07 | El stock inicial se fija al registrar el artículo. Después solo cambia por los movimientos de RN-AR-05 o por una **reposición o ajuste de inventario** explícito del administrador. Modificar los datos del artículo (nombre, precio, categorías) no cambia su stock. |

---

## 9. Citas

### 9.1 Solicitud y registro

| Código | Regla |
|---|---|
| RN-CI-01 | Una cita es para **una mascota** existente, en una **fecha y hora** determinadas. |
| RN-CI-02 | Una cita incluye **uno o más servicios solicitados** (DetalleCita), cada uno con **un veterinario** asignado. Una misma cita puede involucrar a varios veterinarios e incluir varias veces el mismo servicio. |
| RN-CI-03 | La fecha y hora de la cita no puede estar en el pasado (salvo emergencias, ver RN-CI-11). |
| RN-CI-04 | Toda cita nueva se registra en estado **PENDIENTE**. |
| RN-CI-05 | El veterinario de cada servicio debe existir, estar activo y **atender ese día durante todo el tiempo de sus servicios**: desde la hora de la cita hasta que termina el último, sumando la duración estimada de cada servicio (30 minutos si no la tiene). El servicio debe terminar a más tardar a la hora de fin del horario. |
| RN-CI-11 | **Emergencias:** una cita marcada como emergencia puede registrarse con una hora de hasta **1 hora en el pasado**, no exige el horario del veterinario (RN-CI-05) ni revisa cruces (RN-CI-12), y puede atenderse estando PENDIENTE (RN-AT-10). Una cita no puede pasar de normal a emergencia ni al revés. |
| RN-CI-12 | Un veterinario no puede tener servicios que se crucen en el tiempo con los de otra cita PENDIENTE o APROBADO (duraciones según RN-CI-05). |
| RN-CI-13 | **[Rol]** La cita la solicita el cliente para una de sus mascotas. El administrador también puede registrarla en nombre del cliente (por ejemplo, por teléfono o en una emergencia). |

### 9.2 Ciclo de vida (estados)

```
                 ┌──► RECHAZADO   (administrador)
                 │
PENDIENTE ───────┼──► CANCELADO   (cliente o administrador)
                 │
                 └──► APROBADO ───┬──► COMPLETADO (administrador, todo atendido)
                    (administrador)└──► CANCELADO  (cliente o administrador; también inasistencia)
```

| Código | Regla |
|---|---|
| RN-CI-07 | Solo se permiten las transiciones del diagrama. Aprobar o rechazar requiere indicar **el administrador** que toma la decisión (RN-AD-03). |
| RN-CI-08 | **RECHAZADO, CANCELADO y COMPLETADO son estados finales**: la cita ya no puede modificarse. |
| RN-CI-09 | Una cita **solo se completa** cuando **todos** sus servicios solicitados (DetalleCita activos) tienen su atención registrada. |
| RN-CI-10 | Una cita **no puede cancelarse, rechazarse ni eliminarse** si alguno de sus servicios ya fue atendido (una emergencia puede tener atenciones estando PENDIENTE), ni **eliminarse** si alguno de sus servicios ya fue cobrado. |
| RN-CI-17 | Si el cliente no asiste, el administrador **cancela** la cita (registrando el motivo en las observaciones). |

### 9.3 Cambios a una cita

| Código | Regla |
|---|---|
| RN-CI-06 | Al reprogramar una cita (cambiar su fecha/hora) se vuelven a exigir RN-CI-03, RN-CI-05 y RN-CI-12. Al agregar, quitar o cambiar servicios o veterinarios se vuelven a exigir RN-CI-05 y RN-CI-12. Si nada de eso cambia, no se exigen: una cita cuya hora ya pasó sí puede completarse. |
| RN-CI-14 | **[Rol]** Mientras la cita está PENDIENTE, el cliente puede reprogramarla y cambiar sus servicios. Una vez APROBADO, solo el administrador puede hacerlo, y la cita sigue APROBADO. |
| RN-CI-15 | **La mascota de una cita no cambia** después de registrarla. Un DetalleCita que ya tiene atención (activa o anulada) o que ya fue cobrado **no puede quitarse ni cambiar su servicio o veterinario**. |
| RN-CI-16 | Si un servicio solicitado no se realizó, el administrador puede **quitar ese DetalleCita** de una cita APROBADO, siempre que no tenga atención ni cobro (RN-CI-15). La cita debe conservar al menos un servicio. |

---

## 10. Atención (servicio ejecutado)

Una atención es el registro de lo que se hizo al realizar **un DetalleCita**.

| Código | Regla |
|---|---|
| RN-AT-01 | Toda atención está asociada a un DetalleCita existente y activo, y tiene fecha y hora. |
| RN-AT-02 | **Cada DetalleCita tiene como máximo una atención**, aunque esa atención se haya anulado: un DetalleCita con una atención anulada no puede volver a atenderse. Para corregir datos, la atención se modifica. **[BD]** |
| RN-AT-03 | En servicios **médicos**, el **motivo de consulta es obligatorio**. |
| RN-AT-04 | En servicios **estéticos** **no se registran diagnósticos**. |
| RN-AT-05 | Cada diagnóstico registrado indica la enfermedad (del catálogo, activa al agregarla) y su **nivel de gravedad**: Leve, Moderado, Grave o Crítico. |
| RN-AT-06 | Cada insumo utilizado indica el artículo y una cantidad mayor a 0, y si es **cobrable** o no (por defecto, no cobrable). Un insumo no cobrable se considera incluido en el precio del servicio. |
| RN-AT-07 | Al registrar la atención, los insumos **se descuentan del stock** en ese momento; si no hay stock suficiente, la atención no se registra. |
| RN-AT-08 | Al modificar una atención, el stock se ajusta según la diferencia por artículo: si se usa más se descuenta, si se usa menos se devuelve. Solo los aumentos exigen que el artículo esté activo. Los insumos retirados de la atención se dan de baja. |
| RN-AT-09 | No se puede anular una atención que tenga recetas emitidas. |
| RN-AT-10 | Solo se registra, modifica o anula una atención mientras su cita está **APROBADO**, o **PENDIENTE** si es de emergencia. Con la cita COMPLETADO, RECHAZADO o CANCELADO, sus atenciones quedan cerradas. |
| RN-AT-11 | Un insumo que ya se cobró no puede quitarse ni cambiarse (artículo, cantidad ni si es cobrable), y una atención con insumos cobrados no puede anularse. |
| RN-AT-12 | Al anular una atención, sus insumos se devuelven al stock. |
| RN-AT-13 | **[Rol]** La atención la registra el veterinario asignado a ese DetalleCita. |
| RN-AT-14 | La fecha y hora de la atención no puede ser anterior a la de su cita ni posterior al momento en que se registra. |
| RN-AT-15 | **El DetalleCita de una atención no cambia**: una atención no puede moverse a otro servicio ni a otra cita. |

### 10.1 Catálogo de diagnósticos

| Código | Regla |
|---|---|
| RN-DI-01 | Todo diagnóstico tiene un nombre de enfermedad, que es único **[BD]**, y una descripción. |
| RN-DI-02 | Un diagnóstico puede darse de baja aunque esté registrado en atenciones (RN-GE-02): deja de poder agregarse a atenciones, pero las atenciones que ya lo tienen lo conservan. |

---

## 11. Recetas

| Código | Regla |
|---|---|
| RN-RE-01 | Toda receta pertenece a una atención existente y activa, y tiene fecha de emisión e indicaciones generales. |
| RN-RE-02 | **No se emiten recetas en servicios estéticos.** |
| RN-RE-03 | Una receta tiene **al menos un** medicamento/artículo. |
| RN-RE-04 | Cada línea de la receta indica: artículo, dosis, frecuencia, duración en días (> 0) y cantidad total (> 0). Los artículos que se agregan deben estar activos; al modificar, se conservan los que la receta ya tenía aunque se hayan dado de baja. |
| RN-RE-05 | Emitir una receta **no descuenta ni reserva stock**: el stock solo se mueve por atención o venta. |
| RN-RE-06 | **[Rol]** La receta la emite el veterinario que realizó la atención. **La atención de una receta no cambia** después de emitirla. |
| RN-RE-07 | Comprar lo recetado es opcional: el cliente lo adquiere en caja como **venta en caja** (RN-BO-14), en la misma boleta de la atención o en otra. |

---

## 12. Pago en caja (boletas)

El cliente paga en caja cuando termina la atención. Una boleta puede combinar tres tipos de línea:

| Tipo de línea | Qué cobra | ¿Mueve stock? |
|---|---|---|
| **Servicio** (DetalleBoletaServ) | Un DetalleCita ya atendido | No |
| **Insumo** (DetalleBoletaArt con insumo) | Un insumo cobrable usado en una atención | No (ya se descontó en la atención) |
| **Venta en caja** (DetalleBoletaArt sin insumo) | Un artículo comprado en caja (extra o recetado) | Sí, se descuenta al emitir |

### 12.1 Datos de la boleta

| Código | Regla |
|---|---|
| RN-BO-01 | Toda boleta tiene cliente, fecha, método de pago y al menos una línea. |
| RN-BO-02 | Métodos de pago aceptados: **Efectivo, Tarjeta de crédito, Tarjeta de débito, Yape**. |
| RN-BO-03 | El número de boleta lo **asigna el sistema** al emitirla: serie `B001` y el siguiente correlativo de 6 dígitos (ej. `B001-000123`). No cambia al modificar la boleta y nunca se reutiliza, ni siquiera el de una boleta anulada. **[BD]** |
| RN-BO-04 | El cliente de la boleta debe existir y estar activo. Si la boleta cobra servicios o insumos, el cliente debe ser **el dueño de la mascota** atendida en cada uno. |
| RN-BO-19 | **[Rol]** La boleta la emite el administrador en caja. |
| RN-BO-21 | Una boleta puede tener solo líneas de venta en caja (compra sin cita). |
| RN-BO-22 | Al cobrar, el sistema propone la boleta con todo lo **pendiente de cobro** del cliente: cada DetalleCita atendido y cada insumo cobrable que aún no tienen línea en ninguna boleta, con sus precios. En caja se agregan el método de pago y, si corresponde, artículos de venta en caja. |

### 12.2 Precios y totales

| Código | Regla |
|---|---|
| RN-BO-05 | **Los precios los fija el sistema, no quien emite la boleta**: el precio unitario es el precio base vigente en el catálogo. En una línea de servicio, se usa el servicio que tiene registrado el DetalleCita, nunca uno indicado al emitir la boleta. |
| RN-BO-06 | Subtotal de cada línea = precio unitario × cantidad. El total de la boleta es la suma de los subtotales. |
| RN-BO-07 | Toda cantidad es de al menos 1. |
| RN-BO-08 | En **venta en caja** solo se venden artículos activos. Los DetalleCita y los insumos de una atención se cobran aunque su servicio o artículo se haya dado de baja del catálogo después, porque ya forman parte del historial. |

### 12.3 Reglas por tipo de línea

| Código | Regla |
|---|---|
| RN-BO-09 | **Servicio:** se cobra una sola vez (cantidad = 1). |
| RN-BO-10 | **Servicio:** cada DetalleCita se cobra en **una sola línea de servicio** (DetalleBoletaServ), aunque esa línea se haya quitado o su boleta se haya anulado. **[BD]** Una boleta puede tener varias líneas de servicio, de una o varias citas. Anular una boleta no libera sus líneas: para corregir un cobro se **modifica** la boleta, no se anula. |
| RN-BO-16 | **Servicio:** solo se cobra un DetalleCita que ya tiene su atención registrada (y no anulada). |
| RN-BO-11 | **Insumo:** solo se cobran insumos marcados como **cobrables**, de una atención activa. |
| RN-BO-12 | **Insumo:** el artículo de la línea debe coincidir con el del insumo, y no se puede cobrar más cantidad que la utilizada en la atención. |
| RN-BO-13 | **Insumo:** cada insumo utilizado se cobra en **una sola línea de artículo** (DetalleBoletaArt), aunque esa línea se haya quitado o su boleta se haya anulado. **[BD]** Igual que en RN-BO-10, un error se corrige modificando la boleta. |
| RN-BO-18 | Una misma boleta no puede tener dos líneas que cobren el mismo DetalleCita o el mismo insumo. |
| RN-BO-14 | **Venta en caja:** la cantidad total pedida de un artículo (sumando todas sus líneas en la boleta) no puede superar el stock disponible. |
| RN-BO-15 | **Venta en caja:** al emitir la boleta se descuenta el stock. Al modificarla, primero se devuelve lo vendido antes y luego se descuenta lo nuevo. |
| RN-BO-17 | **Anulación:** al anular una boleta se devuelve al stock lo vendido en caja (los insumos no, porque se usaron en la atención). Una boleta anulada no se puede modificar, y sus DetalleCita e insumos no pueden volver a cobrarse (RN-BO-10, RN-BO-13). |

---

## 13. Resúmenes

### 13.1 Bloqueos para dar de baja

Qué impide dar de baja cada entidad (RN-GE-05):

| No se puede dar de baja… | …si tiene |
|---|---|
| Cuenta | Un cliente, veterinario o administrador activo que la use |
| Cliente | Mascotas con citas, o boletas |
| Mascota | Citas |
| Administrador | Ser el último activo |
| Veterinario | Citas PENDIENTE o APROBADO |
| Horario | Veterinarios o administradores activos asignados |
| Categoría | Artículos asignados |
| Cita | Algún servicio ya atendido o cobrado |
| Atención | Recetas emitidas, insumos cobrados, o la cita ya no está APROBADO (PENDIENTE si es emergencia) |

Servicios, artículos y diagnósticos **no tienen bloqueo**: pueden darse de baja en cualquier momento (RN-SE-05, RN-AR-06, RN-DI-02). Boletas y recetas tampoco; anular una boleta devuelve su venta en caja al stock (RN-BO-17).

### 13.2 Datos que no cambian una vez registrados

| Dato | Regla |
|---|---|
| Estado activo de cualquier registro (salvo al dar de baja) | RN-GE-06 |
| Mascota de una cita | RN-CI-15 |
| Si una cita es de emergencia | RN-CI-11 |
| Servicio y veterinario de un DetalleCita atendido o cobrado | RN-CI-15 |
| DetalleCita de una atención | RN-AT-15 |
| Insumo ya cobrado | RN-AT-11 |
| Atención de una receta | RN-RE-06 |
| Stock de un artículo al editar sus datos | RN-AR-07 |
| Cita en estado final | RN-CI-08 |
| Boleta anulada | RN-BO-17 |

### 13.3 Responsabilidades por rol [Rol]

| Operación | Cliente | Administrador | Veterinario |
|---|---|---|---|
| Solicitar cita | Sí (sus mascotas) | Sí (en nombre del cliente) | — |
| Modificar cita PENDIENTE | Sí | Sí | — |
| Modificar cita APROBADO / quitar servicio no realizado | — | Sí | — |
| Aprobar / rechazar cita | — | Sí | — |
| Cancelar cita | Sí (sus citas) | Sí | — |
| Completar cita | — | Sí | — |
| Registrar / modificar atención | — | — | Sí (sus DetalleCita) |
| Emitir receta | — | — | Sí (sus atenciones) |
| Emitir / modificar / anular boleta | — | Sí | — |
| Catálogos, inventario, personal y horarios | — | Sí | — |
