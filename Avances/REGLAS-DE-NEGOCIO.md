# Reglas de Negocio — Patitas Arriba

Sistema de gestión para una clínica veterinaria de perros y gatos: clientes y sus mascotas, agenda de citas, atención médica y estética, recetas, inventario de artículos y pago en caja (boletas).

**Cómo leer este documento**

- Cada regla tiene un código (`RN-XX-nn`) para citarla en requisitos, pruebas y reportes de errores. Cada sección indica los requisitos funcionales (RF) que cubre.
- Salvo que se indique otra cosa, las reglas se validan en la capa de negocio (`patitasarriba-negocio`). Las marcadas con **[BD]** también están protegidas por una restricción de la base de datos.
- Las reglas marcadas con **[Rol]** definen **quién** puede hacer una operación. Las aplica el frontend y la capa REST; la capa de negocio no conoce al usuario que la invoca.

---

## 1. Conceptos clave

| Concepto | Qué es |
|---|---|
| **Servicio** | Elemento del **catálogo** (por ejemplo, "Consulta general" o "Baño y corte"), con tipo, precio base y duración estimada. |
| **Cita** | Solicitud de atención para **una mascota** en una fecha y hora. Agrupa uno o más servicios solicitados. |
| **DetalleCita** (servicio solicitado) | Cada servicio que se pidió dentro de una cita, con el **veterinario** asignado. |
| **Atención** | Registro de lo que se hizo al realizar **un DetalleCita**: motivo, observaciones, diagnósticos e insumos. |
| **Diagnóstico** | Enfermedad del **catálogo** de diagnósticos. En una atención se registra con su nivel de gravedad. |
| **Insumo utilizado** | Artículo del inventario consumido durante una atención. Descuenta stock y puede ser **cobrable** o no. |
| **Receta** | Medicamentos (artículos) que el veterinario prescribe en una atención médica. No mueve stock. |
| **Artículo** | Producto del inventario (medicamento, alimento, accesorio...), con precio base y stock. |
| **Boleta** | Comprobante del pago en caja de un cliente. Puede cobrar servicios atendidos, insumos cobrables y artículos vendidos en caja. |

---

## 2. Proceso de negocio

1. **Solicitud (RF10, RF11).** El cliente (o el administrador en su nombre) registra una cita para una mascota, con fecha, hora y uno o más servicios. Para cada servicio elige un veterinario que atienda en ese horario e indica si es una emergencia. La cita queda **PENDIENTE**.
2. **Revisión (RF11).** El administrador la **aprueba** o la **rechaza**. El cliente o el administrador pueden **cancelarla** mientras no tenga servicios atendidos.
3. **Atención (RF14, RF15).** El veterinario asignado registra la atención de cada servicio: motivo, observaciones, diagnósticos e insumos utilizados, que se descuentan del stock. Si corresponde, emite una **receta**.
4. **Pago en caja (RF18).** El administrador emite la **boleta** al dueño de la mascota con los servicios atendidos, los insumos cobrables y los artículos que compre en caja.
5. **Cierre (RF11).** Cuando todos los servicios de la cita tienen su atención, el administrador marca la cita como **COMPLETADO**.

```
PENDIENTE ──┬──► RECHAZADO   (administrador)
            ├──► CANCELADO   (cliente o administrador)
            └──► APROBADO ───┬──► COMPLETADO (administrador, todo atendido)
               (administrador)└──► CANCELADO  (cliente o administrador)
```

---

## 3. Reglas generales

| Código | Regla |
|---|---|
| RN-GE-01 | **Ningún registro se borra físicamente.** "Eliminar" significa dar de baja (borrado lógico): el registro queda inactivo y deja de aparecer en búsquedas y listados. |
| RN-GE-02 | No se puede dar de baja un registro mientras otros registros dependan de él, porque dejarían de poder leerse (por ejemplo, un cliente con citas). Cada caso se detalla en su sección y se resume en §14. |
| RN-GE-03 | Las operaciones que afectan varios registros a la vez (atención + stock, boleta + stock, cliente + mascotas) se ejecutan **todo o nada**: si una parte falla, no se guarda ninguna. |
| RN-GE-04 | Los valores únicos (usuario, correo, DNI, teléfono, colegiatura, número de boleta y los nombres de servicio, diagnóstico y categoría) no pueden repetirse. **[BD]** |

---

## 4. Cuentas y acceso (RF01, RF05)

| Código | Regla |
|---|---|
| RN-CU-01 | Toda cuenta requiere nombre de usuario, contraseña y un correo con formato válido (`usuario@dominio.ext`). |
| RN-CU-02 | El nombre de usuario y el correo son únicos, aunque la cuenta esté dada de baja. **[BD]** |
| RN-CU-03 | La contraseña nunca se guarda en texto plano: se almacena su hash SHA-256. Al modificar una cuenta, la contraseña solo se vuelve a cifrar si cambió. |
| RN-CU-04 | Para iniciar sesión se requieren usuario y contraseña. Solo ingresan cuentas activas de una persona activa. Al ingresar, el sistema devuelve la persona dueña de la cuenta, cuyo tipo es su rol: cliente, veterinario o administrador. |
| RN-CU-05 | Ante un acceso fallido el sistema responde siempre "Usuario o contraseña incorrectos", sin indicar cuál de los dos falló. |
| RN-CU-06 | No se puede dar de baja una cuenta que use un cliente, veterinario o administrador activo. |
| RN-CU-07 | **[Rol]** Cerrar sesión y restringir los módulos según el rol lo hacen el frontend y la capa REST. Solo el administrador gestiona las cuentas. |

---

## 5. Personas (RF02, RF04)

### 5.1 Reglas comunes

| Código | Regla |
|---|---|
| RN-PE-01 | Toda persona debe tener una cuenta existente y activa. |
| RN-PE-02 | El DNI es obligatorio y tiene exactamente 8 dígitos numéricos. |
| RN-PE-03 | Los nombres son obligatorios. En los clientes también el apellido paterno. |
| RN-PE-04 | El DNI y el teléfono son únicos dentro de cada tipo de persona. **[BD]** |

### 5.2 Cliente

| Código | Regla |
|---|---|
| RN-CL-01 | No se puede dar de baja a un cliente si alguna de sus mascotas tiene citas o si tiene boletas emitidas. |
| RN-CL-02 | Al dar de baja a un cliente, también se dan de baja todas sus mascotas. |
| RN-CL-03 | **[Rol]** El administrador gestiona los clientes; el veterinario solo los consulta. Un cliente solo ve sus propias mascotas y citas. |

### 5.3 Veterinario

| Código | Regla |
|---|---|
| RN-VE-01 | El número de colegiatura es obligatorio y único. **[BD]** |
| RN-VE-02 | Un veterinario recibe citas según sus horarios asignados (RN-CI-05). |
| RN-VE-03 | No se puede dar de baja a un veterinario asignado a alguna cita. |

### 5.4 Administrador

| Código | Regla |
|---|---|
| RN-AD-01 | El administrador es quien aprueba o rechaza las citas (RN-CI-07). |
| RN-AD-02 | Siempre debe existir al menos un administrador activo: no se puede dar de baja al último. |
| RN-AD-03 | El primer administrador lo registra la clínica al instalar el sistema (script `sql/datos-iniciales.sql`); los demás usuarios los crea un administrador. |
| RN-AD-04 | **[Rol]** Solo el administrador gestiona el personal (veterinarios y administradores). |

---

## 6. Mascotas (RF03)

| Código | Regla |
|---|---|
| RN-MA-01 | La clínica atiende **perros y gatos**. |
| RN-MA-02 | Toda mascota tiene nombre, sexo (macho / hembra), tipo, peso mayor a 0 y un dueño. La raza y la fecha de nacimiento son opcionales. |
| RN-MA-03 | El dueño debe ser un cliente existente y activo. |
| RN-MA-04 | La fecha de nacimiento no puede ser futura. |
| RN-MA-05 | No se puede dar de baja una mascota que tenga citas. |
| RN-MA-06 | Se pueden consultar las mascotas activas de un cliente. **[Rol]** El cliente solo consulta las suyas; el veterinario solo consulta. |

---

## 7. Horarios del personal (RF09)

| Código | Regla |
|---|---|
| RN-HO-01 | Un horario define un día de la semana (lunes a domingo), una hora de inicio y una hora de fin. |
| RN-HO-02 | La hora de inicio debe ser anterior a la hora de fin. |
| RN-HO-03 | Un horario puede asignarse a veterinarios y administradores. En los veterinarios determina cuándo pueden recibir citas (RN-CI-05). |
| RN-HO-04 | No se puede dar de baja un horario asignado a algún veterinario o administrador activo. |

---

## 8. Catálogos e inventario (RF06, RF07, RF08)

### 8.1 Servicios

| Código | Regla |
|---|---|
| RN-SE-01 | Todo servicio tiene nombre, descripción, tipo y precio base mayor a 0. |
| RN-SE-02 | Tipos de servicio: **Consulta médica, Operación, Vacunación, Emergencia** (servicios **médicos**) y **Estética** (servicio **no médico**). Esta distinción determina qué se registra en la atención (§10). |
| RN-SE-03 | La duración estimada es opcional; si se indica, debe ser mayor a 0. |
| RN-SE-04 | El nombre del servicio es único. **[BD]** |
| RN-SE-05 | No se puede dar de baja un servicio incluido en alguna cita. |

### 8.2 Categorías de artículo

| Código | Regla |
|---|---|
| RN-CA-01 | Toda categoría tiene nombre y descripción. |
| RN-CA-02 | El nombre de la categoría es único. **[BD]** |
| RN-CA-03 | No se puede dar de baja una categoría que tenga artículos asignados. |

### 8.3 Artículos

| Código | Regla |
|---|---|
| RN-AR-01 | Todo artículo tiene nombre y precio base mayor a 0. |
| RN-AR-02 | Todo artículo pertenece a **al menos una** categoría existente; puede pertenecer a varias. |
| RN-AR-03 | El stock actual y el stock mínimo no pueden ser negativos. |
| RN-AR-04 | El administrador actualiza el stock al modificar el artículo. Además, el stock baja al usar el artículo como insumo en una atención (RN-AT-06) y al venderlo en caja (RN-BO-10). |
| RN-AR-05 | No se puede dar de baja un artículo usado en atenciones, recetas o boletas. |

### 8.4 Diagnósticos

| Código | Regla |
|---|---|
| RN-DI-01 | Todo diagnóstico tiene un nombre de enfermedad, que es único. **[BD]** |
| RN-DI-02 | No se puede dar de baja un diagnóstico registrado en alguna atención. |

**[Rol]** El administrador gestiona los tres catálogos; el veterinario solo los consulta.

---

## 9. Citas (RF10, RF11, RF13)

| Código | Regla |
|---|---|
| RN-CI-01 | Una cita es para **una mascota** existente, en una **fecha y hora**, e indica si es una emergencia. |
| RN-CI-02 | Una cita incluye **uno o más servicios** (DetalleCita), cada uno con su servicio y **un veterinario** asignado. Una cita puede involucrar a varios veterinarios. |
| RN-CI-03 | La fecha y hora no puede estar en el pasado al registrar o reprogramar la cita. |
| RN-CI-04 | Toda cita nueva se registra en estado **PENDIENTE**. |
| RN-CI-05 | El veterinario de cada servicio debe existir y tener un horario que incluya el día y la hora de la cita. Se vuelve a validar al reprogramar. |
| RN-CI-06 | Solo se permiten las transiciones del diagrama de §2. **RECHAZADO, CANCELADO y COMPLETADO son estados finales**: la cita ya no puede modificarse. |
| RN-CI-07 | Aprobar o rechazar una cita requiere indicar **el administrador** que toma la decisión. |
| RN-CI-08 | Una cita **solo se completa** cuando **todos** sus servicios tienen su atención registrada. |
| RN-CI-09 | Una cita **no puede cancelarse ni eliminarse** si alguno de sus servicios ya fue atendido. |
| RN-CI-10 | Se pueden consultar las citas de un cliente (ordenadas por fecha) y las citas de un día (ordenadas por hora), opcionalmente solo las de un veterinario. El reporte de agenda diaria (RF13) se arma con estas últimas. |
| RN-CI-11 | **[Rol]** El cliente registra, modifica y cancela las citas de sus mascotas. El administrador además las aprueba, rechaza y completa. El veterinario solo consulta y filtra su agenda diaria. |

---

## 10. Atención (RF14)

| Código | Regla |
|---|---|
| RN-AT-01 | Toda atención está asociada a un DetalleCita existente y activo, y tiene fecha y hora. |
| RN-AT-02 | Cada DetalleCita tiene como máximo una atención. **[BD]** |
| RN-AT-03 | En servicios **médicos**, el **motivo de consulta es obligatorio**. |
| RN-AT-04 | En servicios **estéticos** no se registran diagnósticos. |
| RN-AT-05 | Cada diagnóstico registrado indica la enfermedad del catálogo y su **nivel de gravedad**: Leve, Moderado, Grave o Crítico. Cada insumo indica el artículo, una cantidad mayor a 0 y si es **cobrable** o no. |
| RN-AT-06 | Al registrar la atención, los insumos **se descuentan del stock**; si no hay stock suficiente, la atención no se registra. Al modificarla, se devuelve al stock lo que tenía y se descuenta lo nuevo. Los insumos retirados se dan de baja. |
| RN-AT-07 | No se puede eliminar una atención que tenga recetas emitidas. |
| RN-AT-08 | **[Rol]** Solo el veterinario registra, modifica y elimina atenciones. |

---

## 11. Recetas (RF15, RF17)

| Código | Regla |
|---|---|
| RN-RE-01 | Toda receta pertenece a una atención existente y activa, y tiene fecha de emisión e indicaciones. |
| RN-RE-02 | **No se emiten recetas en servicios estéticos.** |
| RN-RE-03 | Una receta tiene **al menos un** medicamento. Cada uno indica artículo activo, dosis, frecuencia, duración en días y cantidad total válidos. |
| RN-RE-04 | Emitir una receta **no descuenta stock**: lo recetado se compra en caja como venta (§13). |
| RN-RE-05 | **[Rol]** Solo el veterinario registra, modifica, elimina e imprime recetas (el reporte de receta se arma con la receta y sus detalles). |

---

## 12. Historial clínico (RF16)

| Código | Regla |
|---|---|
| RN-HI-01 | El historial clínico de una mascota son sus atenciones en orden cronológico, cada una con sus diagnósticos, insumos y las recetas emitidas en ella. |
| RN-HI-02 | **[Rol]** Lo generan el administrador y los veterinarios. |

---

## 13. Pago en caja: boletas (RF18)

Una boleta puede combinar tres tipos de línea:

| Tipo de línea | Qué cobra | ¿Mueve stock? |
|---|---|---|
| **Servicio** | Un DetalleCita de una cita | No |
| **Insumo** | Un insumo cobrable usado en una atención | No (ya se descontó en la atención) |
| **Venta en caja** | Un artículo comprado en caja | Sí |

| Código | Regla |
|---|---|
| RN-BO-01 | Toda boleta tiene cliente, fecha, método de pago, número y al menos una línea. |
| RN-BO-02 | Métodos de pago: **Efectivo, Tarjeta de crédito, Tarjeta de débito, Yape**. |
| RN-BO-03 | El número tiene el formato `B001-000123` y es único, incluso frente a boletas anuladas. **[BD]** |
| RN-BO-04 | Si la boleta cobra servicios o insumos, el cliente debe ser **el dueño de la mascota** atendida. |
| RN-BO-05 | **Los precios los fija el sistema**: el precio unitario es el precio base del servicio o artículo en el catálogo, que debe estar activo. Subtotal = precio × cantidad; el total es la suma de los subtotales. |
| RN-BO-06 | Toda cantidad es de al menos 1; un servicio se cobra con cantidad 1. |
| RN-BO-07 | Cada DetalleCita se cobra una sola vez, aunque su boleta se haya anulado. **[BD]** |
| RN-BO-08 | Un insumo solo se cobra si es **cobrable** y su atención está activa. El artículo de la línea debe coincidir con el del insumo y no se cobra más de lo usado. Cada insumo se cobra una sola vez, aunque su boleta se haya anulado. **[BD]** |
| RN-BO-09 | **Venta en caja:** la cantidad pedida de un artículo, sumando todas sus líneas, no puede superar el stock disponible. |
| RN-BO-10 | **Venta en caja:** al emitir la boleta se descuenta el stock. Al modificarla, se devuelve lo vendido antes y se descuenta lo nuevo. |
| RN-BO-11 | Anular una boleta la da de baja. |
| RN-BO-12 | **[Rol]** Solo el administrador emite, imprime, modifica y anula boletas. |

---

## 14. Resumen: bloqueos para dar de baja

| No se puede dar de baja… | …si tiene |
|---|---|
| Cuenta | Un cliente, veterinario o administrador activo que la use |
| Cliente | Mascotas con citas, o boletas |
| Mascota | Citas |
| Veterinario | Citas |
| Administrador | Ser el último activo |
| Horario | Veterinarios o administradores activos asignados |
| Servicio | Citas que lo incluyan |
| Categoría | Artículos asignados |
| Artículo | Atenciones, recetas o boletas que lo usen |
| Diagnóstico | Atenciones que lo registren |
| Cita | Algún servicio ya atendido |
| Atención | Recetas emitidas |

---

## 15. Fuera de la capa de negocio

| Requisito | Dónde se resuelve |
|---|---|
| RF01 (cerrar sesión y restringir módulos por rol) | Frontend y capa REST, con la persona que devuelve el login (RN-CU-04). |
| RF12 (recordatorios por correo 24 h antes) | No implementado. Requiere un proceso programado y un servicio de correo; puede usar las citas de un día (RN-CI-10). |
| RF13, RF16, RF17, RF18 (reportes e impresión) | Capa de presentación, con las consultas de la capa de negocio. |
