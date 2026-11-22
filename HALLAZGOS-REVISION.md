# Hallazgos de la revisión del 22/11/2026

Revisión hecha sobre la rama `develop` (commit `3084dd1`) antes de pasar los cambios a `main`.

Se hicieron cuatro revisiones:

| Revisión | Tipo | Alcance |
|---|---|---|
| Capa REST | Pruebas por HTTP | Los 16 recursos más el reporte de agenda diaria (unas 350 verificaciones) |
| Flujo del Administrador | Pruebas en el navegador | 25 pantallas del rol Administrador, en escritorio y en móvil |
| Código del backend | Revisión estática | Negocio, persistencia, REST, SQL y reportes |
| Código del frontend | Revisión estática | Páginas, componentes, ViewModels, servicios REST y cálculos de agenda |

Cada hallazgo indica su origen. Los marcados como **comprobado** se verificaron leyendo el código o probándolos. Los marcados como **posible** son sospechas que no se reprodujeron.

---

## 1. Lo que funciona bien

- **Emergencia en Registrar cita.**
  - Al marcar la casilla, la fecha pasa a la hora actual y se sugiere el servicio «Atencion de emergencia».
  - Al desmarcar, la fecha se mantiene y el selector de servicio se reinicia.
  - La sugerencia no pisa un servicio elegido a mano.
  - Una emergencia con más de 1 hora antes o después de la hora actual se rechaza en el formulario y en la API.
  - Al editar una cita, la casilla queda deshabilitada.
- **Menú superior colapsable.** Se colapsa en un botón por debajo de 1400 px, se cierra al navegar y se ve completo en pantallas anchas.
- **Citas en la API.** Se cumplen todas las reglas RN-CI probadas:
  - cita en el pasado;
  - veterinario fuera de su horario;
  - cruce con otra cita, también si la otra es una emergencia;
  - transiciones de estado;
  - administrador obligatorio al aprobar o rechazar.
- **Datos únicos.** Correo, DNI, teléfono y colegiatura repetidos devuelven «... ya se encuentra en uso», también cuando el registro existente está dado de baja.
- **Reporte de agenda diaria.** Devuelve un PDF correcto. Solo la primera llamada tarda unos 2 s, por el arranque de JasperReports; las siguientes tardan menos de 100 ms.
- **Rendimiento de la API.** Todos los demás endpoints responden en menos de 120 ms.
- **Persistencia.** Los 171 stored procedures coinciden con las llamadas de los DAO en nombre y parámetros, y las columnas que se leen existen en el DDL.
- **Seguridad básica.** No hay SQL armado por concatenación (todo usa `CallableStatement`), y el hash de la contraseña no se devuelve en las respuestas.
- **Transacciones.** Las operaciones de varios pasos (RN-GE-03) se hacen en una sola transacción.
- **Frontend.** Todas las páginas tienen la política de autorización correcta, los eventos se desuscriben y `StateHasChanged` se llama con `InvokeAsync`.

---

## 2. Arreglar antes del merge a main

Son errores que dejan datos incorrectos o bloquean un flujo.

### 2.1 Una misma cuenta se puede asignar a personas de roles distintos

- **Estado:** resuelto en la rama `fix/hallazgos-revision`. Al registrar o modificar un cliente, veterinario o administrador se revisa que la cuenta no sea de otra persona, de cualquier rol (`CuentaLibre`); responde 400 «La cuenta ya pertenece a otra persona», también en el caso del mismo rol. Regla nueva RN-CU-08.
- **Origen:** pruebas REST (comprobado).
- **Dónde:** `validarCuentaActiva` en `ClienteBLImpl`, `VeterinarioBLImpl` y `AdministradorBLImpl`. Solo revisa que la cuenta exista y esté activa, no que esté libre. El UNIQUE de `id_cuenta` es por tabla.
- **Escenario:** `POST /veterinarios` con la cuenta de un cliente devuelve 201.
  - El login de esa cuenta solo reconoce uno de los roles.
  - Al dar de baja a una de las personas se da de baja la cuenta compartida. La otra persona queda con la cuenta inactiva y los listados de clientes, veterinarios, administradores, citas, atenciones y mascotas responden 500 «cuenta no puede ser nula».
- **Relacionado:** si la cuenta ya pertenece a otra persona del mismo rol, la respuesta es 500 en vez de 400, porque el error lo da el UNIQUE de la base.
- **Esperado:** 400 «La cuenta ya pertenece a otra persona».

### 2.2 Anular una atención deja la cita sin poder completarse

- **Origen:** revisión de código del frontend (comprobado).
- **Dónde:** `ListarCitasPage.razor` (`PuedeCompletarse`) y `Datos/AgendaVeterinario.cs`. La lista de servicios atendidos sale de `GET /atenciones`, y el procedure `listar_atenciones` no devuelve las anuladas.
- **Escenario:** una cita con dos servicios; se anula la atención del primero y se atiende el segundo.
  - El botón «Completar» no aparece nunca y la cita queda APROBADA.
  - En «Mi agenda» el servicio anulado vuelve a mostrar «Atender», y el backend lo rechaza.
- **Regla afectada:** RN-CI-10 y RN-AT-02. Un servicio con la atención anulada cuenta como no realizado y no impide completar la cita.
- **Estado:** resuelto en la rama `fix/hallazgos-revision`. El front sabe qué servicios tienen atención, activa o anulada, con `GET /atenciones/estados` (procedure `listar_estados_atencion`, clase `AtencionesDeCitas`). «Completar» aparece cuando todos los servicios tienen atención y al menos una está activa. Si todas se anularon, aparece «Cancelar» aunque ya haya pasado la hora de la cita. En las agendas, el servicio anulado se ve como «Anulado» y ya no ofrece «Atender». El backend ya aplicaba bien la regla y no cambió.

### 2.3 Se puede anular una atención cuyo servicio ya fue cobrado

- **Estado:** resuelto en la rama `fix/hallazgos-revision`. `AtencionBLImpl.delete` rechaza con 400 si el servicio aparece en una boleta (`boletaConServicio`), y la lista de atenciones (`ListarAtencionesPage.razor`) ya no muestra «Anular» en ese caso. RN-AT-10 actualizada.
- **Origen:** revisión de código del backend (comprobado).
- **Dónde:** `AtencionBLImpl.delete`. Revisa la receta y los insumos cobrados (`boletaConInsumo`), pero no el servicio (`boletaConServicio`).
- **Escenario:** se emite la boleta con el servicio atendido y después se anula la atención.
  - La boleta activa sigue cobrando un servicio que ahora cuenta como no realizado.
  - Al modificar la boleta, el backend rechaza esa línea. Quitarla tampoco sirve: el UNIQUE de `detalle_boleta_serv` impide volver a cobrarla.
- **Regla afectada:** RN-AT-10 (no lo contempla) y RN-BO-07.

### 2.4 Lo cobrado en una boleta anulada vuelve a aparecer como pendiente de cobro

- **Estado:** resuelto en la rama `fix/hallazgos-revision`. Lo pendiente lo decide el backend con `GET /boletas/pendientes?cliente={id}` (procedures `listar_servicios_por_cobrar_por_cliente` y `listar_insumos_por_cobrar_por_cliente`), con la misma regla que valida al emitir: algo que ya estuvo en una boleta no vuelve a pendiente, aunque la boleta se haya anulado. También cubre un caso que no estaba en el hallazgo: una línea quitada al modificar una boleta. `CobrosPendientes.cs` ya no descarga `GET /atenciones`, `GET /boletas` ni `GET /servicios`. RN-BO-07 y RN-BO-08 actualizadas.
- **Origen:** revisión de código del frontend (comprobado).
- **Dónde:** `Servicios/CobrosPendientes.cs`. Arma lo ya cobrado a partir de `GET /boletas`, y el procedure `listar_boletas` solo devuelve boletas activas.
- **Escenario:** se anula una boleta y después, en Caja, Cobrar, se busca al cliente.
  - Los servicios e insumos de la boleta anulada aparecen otra vez marcados como pendientes.
  - Al generar la boleta, el backend la rechaza y el cajero no sabe qué líneas desmarcar.
- **Regla afectada:** RN-BO-07 y RN-BO-08. Cada servicio o insumo se cobra una sola vez, aunque su boleta se haya anulado.

Los puntos 2.2, 2.3 y 2.4 tratan el mismo tema (las anulaciones de atenciones y boletas) y pueden resolverse juntos.

### 2.5 Cambios rápidos

- **Tablas con scroll horizontal en móvil.**
  - Origen: pruebas en el navegador (comprobado).
  - En Citas, Boletas y Artículos, y en la vista «Tabla» de Clientes, Mascotas, Veterinarios, Administradores y Horarios, la tabla QuickGrid se sale del ancho y arrastra toda la página (ancho de 870 a 992 px en una pantalla de 375 px).
  - Las tablas no están dentro de un contenedor con desplazamiento horizontal (`.table-responsive`).
  - Estado: descartado. La aplicación no está orientada a móvil.
- **Enter no filtra en Gestión de citas.**
  - Origen: pruebas en el navegador (comprobado).
  - En `ListarCitasPage.razor` el buscador «Mascota o dueño» no tiene `@onkeydown`. Es el único listado que faltó cuando se agregó Enter a los buscadores.
  - Estado: resuelto en la rama `fix/hallazgos-revision`. El buscador filtra con Enter (`TeclaEnFiltro`), como el resto de listados.
- **`procedures-reportes.sql` no figura en la instalación.**
  - Origen: revisión previa al merge (comprobado).
  - No aparece en `instalar-desde-cero.sql`, en `BackEnd/sql/README.md` ni en el README principal. Sin ese script, `GET /reportes/agenda-diaria` falla porque los procedures no existen.
  - Estado: resuelto en la rama `fix/hallazgos-revision`. Se agregó su sección a `instalar-desde-cero.sql`, entre `registro_pendiente` y `servicio`, y se probó en un esquema temporal: se crean los 5 procedures `reporte_*`. Los dos README lo mencionan y advierten que sin él fallan los tres reportes: agenda diaria, historial clínico y boleta. Las bases ya instaladas deben ejecutarlo una vez; la base local de la revisión todavía tenía los procedures de receta, ya retirados.

---

## 3. Gravedad media (para después del merge)

### Backend

- **Alta de personas sin validar formato.** Cuando el administrador da de alta clientes, veterinarios o administradores, no se valida el formato del teléfono ni de los nombres: acepta un teléfono de 8 dígitos o con letras, y nombres con números. El autorregistro sí lo valida. (Pruebas REST, comprobado.)
- **La emergencia a futuro no se puede atender hasta que llegue su hora.** `AtencionBLImpl.validarFechaAtencion` exige que la atención no sea anterior a la cita ni futura. Una emergencia registrada a +45 minutos no tiene una hora de atención válida durante ese rato, contra RN-AT-04. Como el formulario ya pone la hora actual, casi no ocurre. (Código del backend, comprobado.)
- **Stock sin bloqueo.** El stock se actualiza leyendo, calculando y escribiendo el valor absoluto, sin bloqueo:
  - dos atenciones o boletas simultáneas pueden perder un descuento;
  - en boletas, la validación del stock ocurre antes de iniciar la transacción;
  - `stock_actual` es UNSIGNED, así que un segundo descuento puede dar 500 en lugar de 400.

  Archivos: `AtencionBLImpl.moverStock` y `BoletaBLImpl.descontarStock`. (Código del backend, posible.)
- **Boleta con líneas repetidas da 500 en vez de 400.** Una boleta nueva con dos líneas del mismo servicio o insumo pasa la validación, porque `validarNoRepetidos` solo consulta la base, y falla después por el UNIQUE. (Código del backend.)
- **Contraseñas.** Se guardan con SHA-256 sin sal, y el login no limita los intentos (`HashUtil`, `CuentaBLImpl.login`). (Código del backend.)
- **Recuperación de contraseña.** Se puede averiguar qué correos están registrados midiendo el tiempo de respuesta, porque solo para los correos existentes se envía el correo SMTP. Va contra RN-AC-04. (Código del backend, posible.)
- **Hora sin zona horaria.** Se usa `LocalDateTime.now()` sin zona horaria. Si el servidor corriera en UTC, las validaciones de fecha (citas en el pasado, margen de emergencias) quedarían desfasadas 5 horas. En local no afecta. (Código del backend, posible.)

### Frontend

- **Doble clic en Aprobar, Completar o Rechazar.** Los botones no se bloquean mientras se envía la petición, así que se pueden mandar dos PUT. Si fallan, la pantalla puede quedar mostrando un estado distinto del real (`Servicios/EstadosCita.cs`).
- **Cita APROBADA vencida.** Si la hora de una cita APROBADA ya pasó y no tiene atenciones, no se puede cancelar ni completar (por ejemplo, cuando el cliente no se presentó). `PuedeCancelarse` exige que la fecha sea futura, y RN-CI-11 no lo pide.
- **Inicio del administrador.** Dice «Hoy tiene N citas por aprobar», pero cuenta todas las citas pendientes, de cualquier fecha (`InicioAdministrador.razor`).
- **Cruce de turnos.** Solo se valida en la pantalla Horarios. Desde la ficha de un veterinario o de un administrador se pueden marcar dos turnos solapados del mismo día.
- **Edición de artículos.** Envía el stock leído al abrir la pantalla. Si mientras tanto una atención o boleta descontó stock, ese descuento se pierde. (Posible.)
- **Errores de formato no controlados.** `ErrorServicio` no captura `FormatException`, que lanzan los `Parse` de fechas y enums en los servicios REST. Un registro con un dato inesperado hace que la pantalla muestre «Ocurrió un error inesperado» en lugar de un mensaje controlado. (Posible.)
- **Historial clínico.** Al cambiar rápido de mascota, una respuesta que llega tarde puede mostrar el historial de la mascota anterior. (Posible.)
- **Descargas repetidas de citas.** Abrir «Atender» descarga `GET /citas` completo tres veces, por cómo `AtencionRestService` y `RecetaRestService` cruzan los datos.
- **Edad inconsistente.** Se calcula distinto en el portal del cliente (`Datos/PortalCliente.cs`) y en la lista del administrador, y el portal escribe «1 meses».

### Documentación

- **`REGLAS-DE-NEGOCIO.md` no recoge reglas que el código aplica:**
  - una cita APROBADA que se modifica vuelve a PENDIENTE (el código la llama RN-CI-16);
  - los turnos de horario van entre 08:00 y 20:00 y duran de 1 a 12 horas (el código la llama RN-HO-05).
- **Validaciones del frontend sin respaldo en el documento:** la duración de un servicio entre 5 y 600 minutos y el peso máximo de 150 kg.
- **RN-MA-02 dice que la fecha de nacimiento de la mascota es opcional,** pero la base de datos, el modelo y el mapper la exigen. Por la regla de que manda la base de datos, hay que corregir el documento.

---

## 4. Gravedad baja

### Backend

- `PUT /cuentas/999999` con un nombre de usuario existente responde 400 en lugar de 404, porque se validan los únicos antes de comprobar que la cuenta existe.
- Los mensajes técnicos en inglés llegan al usuario ante fechas o enums mal formados, por ejemplo «could not be parsed at index 0» o «No enum constant ...».
- Un DNI o nombre de usuario en blanco en los filtros da 500 en vez de 400 en Clientes, Veterinarios y Cuentas. En Administradores la misma entrada da 404.
- Una cita con detalles repetidos o de otra cita, o una atención con insumos de otra atención, da 500 en vez de 400.
- Los textos que superan el largo de la columna, o un peso fuera de DECIMAL(5,2), dan 500 «Data too long» en vez de 400.
- Un veterinario con turnos contiguos (por ejemplo 08-12 y 12-16) no puede recibir un servicio que cruce el cambio de turno, porque `CitaBLImpl.cubre` exige que un solo horario cubra todo el tramo.
- Si se da de baja a un administrador que aprobó citas, esas citas quedan sin administrador y no se pueden volver a guardar como APROBADAS.
- `CitaBLImpl.insert` no exige que una cita nueva llegue sin administrador.
- El número de boleta se asigna fuera de la transacción. Dos emisiones simultáneas pueden calcular el mismo número, y la segunda falla con 500.
- Un correo con los caracteres `<` o `>` pasa la validación, pero el envío SMTP lo rechaza con 500.
- Al cambiar la duración de un servicio o un horario no se revisan las citas ya registradas. (Posible.)
- El reporte de agenda diaria agrupa por el nombre del veterinario, así que dos veterinarios con el mismo nombre se mezclan. Con un veterinario inexistente devuelve un PDF casi vacío.
- `GET /citas?veterinario=1` sin el parámetro `fecha` ignora el filtro, y `?veterinario=abc` responde 404.
- La conexión al RDS usa `useSSL=false` (`DBManager`).
- `ErrorInesperadoMapper` devuelve el mensaje interno de la excepción, que puede contener texto de SQL o nombres de clases.
- Los importes de dinero se manejan con `double`.
- El `.jrxml` del reporte tiene como valor por defecto del logo una ruta absoluta de otra computadora.
- Código sin uso: `TransaccionDAO` y su implementación, y unos 29 procedures sin llamador, como `reporte_receta`, `reporte_detalle_receta` y `listar_boletas_por_cliente`.
- La capa de negocio tiene lambdas y streams en `CitaBLImpl`, `AtencionBLImpl`, `BoletaBLImpl` y `RecetaBLImpl`. Se anota solo porque el profesor no usa lambdas en esta capa.

### Frontend

- El modal de reconexión de Blazor está en inglés («Rejoining the server...», «Retry») (`Components/Layout/ReconnectModal.razor`).
- «Generar reporte» en la Agenda diaria no conserva la fecha ni el veterinario elegidos.
- El mensaje de validación de la emergencia solo aparece cuando los demás campos ya son válidos (comportamiento normal de `IValidatableObject`).
- Al eliminar una mascota con citas, el mensaje muestra el estado en bruto: «(CANCELADO)».
- Al crear o editar un cliente o una cita no aparece mensaje de éxito, como sí ocurre en las demás pantallas.
- Textos:
  - «Revisada por:» queda vacío en las citas pendientes;
  - «Hoy tiene 1 cita por aprobar (una es emergencia)»;
  - en los formularios de alta, «Le enviaremos un correo ... Usted no la conoce» va dirigido al administrador cuando el correo lo recibe la persona registrada;
  - «Sin horarios asignados» aparece dos veces;
  - comillas rectas en un aviso de horarios.
- En el portal del cliente, «Se le avisará cuando se confirme» promete un aviso que no existe.
- `DetalleCita.razor` rotula «Por atender» los servicios de citas rechazadas, canceladas o completadas.
- «Página 1 de 1» es un texto fijo en el reporte impreso.
- Los medicamentos de una receta se filtran por el nombre literal de la categoría «Medicamentos».
- En la tabla de diagnósticos, «Agregar» sin diagnóstico elegido no avisa nada y permite duplicados.
- Accesibilidad:
  - etiquetas sin `for`;
  - selectores sin `aria-label`;
  - modales sin nombre accesible ni cierre con Esc.
- Código duplicado o sin uso:
  - el constructor de los servicios REST se repite en 13 archivos;
  - `Iniciales` aparece cuatro veces;
  - algunos métodos de servicio no se llaman desde ninguna parte.

---

## 5. Conocido, para decidir con el equipo

- **La API REST no tiene autenticación ni autorización.** Cualquiera puede llamarla directamente, sin pasar por el frontend, y por ejemplo:
  - cambiar la contraseña de una cuenta con `PUT /cuentas/{id}`;
  - dar de baja clientes;
  - emitir boletas;
  - listar DNI, teléfonos y correos de todas las personas.

  Hoy RN-CU-07 deja el control de roles en el frontend. Es el punto de seguridad más grave, pero cambiarlo es un cambio de diseño grande. Conviene preguntarle al jefe de práctica si se exige.
- **El DNI es único por rol, no en todo el sistema.** Un cliente puede tener el mismo DNI que un veterinario. Hay que confirmar si eso es lo que se quiere según RN-GE-04.
- **Hallazgos ya aceptados que siguen igual:**
  - el filtro de veterinario de la agenda usa el nombre y no el id;
  - el historial clínico mantiene la mascota anterior;
  - «Ver ficha» no hace scroll en móvil;
  - el campo de fecha y hora de una cita normal acepta cualquier hora (el backend la valida al guardar);
  - un cuerpo nulo o un enum faltante en el JSON da 500.
