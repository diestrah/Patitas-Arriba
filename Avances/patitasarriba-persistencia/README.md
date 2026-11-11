# Capa DAO de Patitas Arriba

## Objetivo de la refactorización

La persistencia comparte la ejecución del CRUD, pero conserva explícitos los procedimientos, parámetros, mapeos y pasos particulares de cada entidad. El mapeo principal tiene siempre esta firma:

```java
protected Entidad mapear(ResultSet rs) throws SQLException
```

Cada DAO crea su objeto dentro de `mapear`. No se intenta instanciar un tipo genérico con `new T()`, ni se necesitan `crearModelo()` o adaptadores `mapearModelo()`. Los contratos públicos mantienen las operaciones `void insert`, `void update` y `void delete`.

## Organización

```text
patitasarriba/
  dao/                         Contratos públicos, agrupados por área
    DAO.java                   CRUD genérico
    ComandoDAO.java             Operación que recibe una conexión y puede devolver un resultado
    TransaccionDAO.java         Variante de escritura con conexión explícita
    transacciones/
      TransactionsManager.java Propietario de la transacción asociada al hilo
  daoimpl/
    BaseDAOImpl.java           Ejecución común del CRUD
    ConexionDAO.java            Selección y cierre de conexiones propias
    TransaccionDAOImpl.java    Ejecución sobre una conexión explícita
    producto/
      ProductoDAOImpl.java     Mapeo de los campos comunes de artículo y servicio
    atencion/, boleta/, cita/, horario/, mascota/, receta/, usuario/
```

Las interfaces internas de detalles permanecen junto a sus implementaciones y conservan su visibilidad de paquete. No se hacen públicas para cambiar su ubicación. RegistroPendiente y TokenRecuperacion tienen consultas específicas; no se les fuerza a implementar un CRUD completo. Se conserva la retirada de `RegistroDAOImpl`: cada mapper carga `activo`, salvo artículo y servicio que lo cargan mediante el auxiliar de producto.

## Qué hace BaseDAOImpl

`BaseDAOImpl<T extends Registro>` implementa `DAO<T, Integer>` y proporciona:

| Método público heredado | Responsabilidad |
| --- | --- |
| `findAll()` | Ejecuta `comandoListar`, recorre sus filas y llama a `mapear(rs)` para cada una. Devuelve una lista vacía si no hay filas. |
| `findById(id)` | Ejecuta `comandoBuscar`; devuelve `null` si no existe una fila. |
| `insert(modelo)` | Ejecuta el comando y llama a `procesarResultadoInsertar` para recuperar el resultado. |
| `update(modelo)` | Ejecuta el comando de actualización. |
| `delete(id)` | Ejecuta el comando de baja. Los procedimientos conservan sus reglas de borrado lógico. |

Los comandos y resultados se cierran con try-with-resources. Los ejecutores de actualización y eliminación devuelven un booleano de filas afectadas, pero el contrato público sigue siendo `void`: no se convierte automáticamente cero filas en una excepción. El BL continúa siendo responsable de las validaciones que ya realizaba.

## Qué implementa un DAO concreto

Los 14 DAO principales usan la base directamente o, para artículo y servicio, a través de `ProductoDAOImpl`:

- Diagnóstico, categoría, servicio, horario, mascota, cuenta y cliente heredan el CRUD común.
- Artículo, administrador, veterinario, cita, atención, receta y boleta mantienen sus escrituras compuestas explícitas. Reutilizan los ejecutores de la base para la cabecera y conservan los pasos de sus relaciones o detalles.
- Cita conserva además sus lecturas explícitas para cargar los detalles con la conexión disponible y resolver la cabecera sin entrar en recursión.

Cada uno implementa:

```java
comandoInsertar(Connection conn, Entidad modelo)
comandoActualizar(Connection conn, Entidad modelo)
comandoEliminar(Connection conn, Integer id)
comandoBuscar(Connection conn, Integer id)
comandoListar(Connection conn)
mapear(ResultSet rs)
procesarResultadoInsertar(CallableStatement cmd, Entidad modelo)
```

Los métodos `comando...` preparan el procedimiento y sus parámetros; no deben ejecutar SQL ni abrir otra conexión. Devuelven un comando abierto que luego administra el ejecutor. No es necesario sobrescribir `findAll`, `findById` o los ejecutores solo para llamar a `super`.

Las búsquedas propias, como `findByNombreEnfermedad`, `findByDni`, `findByCuenta`, `findByCategoriaId` o los filtros de mascota, siguen en el DAO correspondiente. Utilizan `ejecutarComando` para obtener una conexión y `mapear(rs)` para convertir resultados.

## Mapeo y campos específicos

Ejemplo de diagnóstico:

```java
@Override
protected Diagnostico mapear(ResultSet rs) throws SQLException {
    Diagnostico diagnostico = new Diagnostico();
    diagnostico.setActivo(rs.getBoolean("activo"));
    diagnostico.setId(rs.getInt("id_diagnostico"));
    diagnostico.setNombreEnfermedad(rs.getString("nombre_enfermedad"));
    diagnostico.setDescripcion(rs.getString("descripcion"));
    return diagnostico;
}
```

Se conservan los nombres reales de columnas de cada procedimiento: no se supone que todos los identificadores se llamen `id` en SQL. Tampoco se exige agregar un ID a `Registro`, porque algunas relaciones no lo tienen.

### Producto, artículo y servicio

`ProductoDAOImpl` no crea un `Producto` genérico ni devuelve un objeto anónimo que podría no ser del tipo T. Expone únicamente el auxiliar:

```java
protected void mapearProducto(ResultSet rs, T producto) throws SQLException
```

El mapper principal sigue recibiendo un parámetro:

```java
@Override
protected Articulo mapear(ResultSet rs) throws SQLException {
    Articulo articulo = new Articulo();
    mapearProducto(rs, articulo);
    // ID, stock y categorías se cargan aquí.
    return articulo;
}
```

El auxiliar comparte `activo`, nombre, precio y descripción. Servicio completa ID, tipo y duración opcional. Artículo completa ID, stock y categorías. Mascota conserva sus relaciones, fecha, sexo y foto. Cuenta conserva sus campos de acceso y fecha de creación.

### Detalles y relaciones internas

Insumos, diagnósticos por atención, detalles de receta y líneas de boleta crean sus entidades dentro de `mapear(rs)`. Los DAO que no ofrecen un CRUD completo permanecen especializados y no heredan la base solo para reutilizar una asignación de `activo`.

En DetalleCita, el mapper crea el detalle y carga sus campos. El llamador asigna después la cita que ya tiene en memoria. La carga de una cabecera sin sus detalles se conserva para evitar el ciclo cita -> detalles -> cita -> detalles.

## Recursos y excepciones

`SQLException` se propaga al llamador. No se reemplaza por una impresión en consola que permita continuar como si la operación hubiera funcionado.

Si falla un parámetro antes de devolver el comando, el método preparador debe cerrarlo porque el ejecutor todavía no lo ha recibido:

```java
CallableStatement cmd = conn.prepareCall(sql);
try {
    cmd.setInt("p_id", id);
    return cmd;
} catch (SQLException | RuntimeException ex) {
    try {
        cmd.close();
    } catch (SQLException cierre) {
        ex.addSuppressed(cierre);
    }
    throw ex;
}
```

`addSuppressed` conserva el error de cierre junto al original. En una consulta que crea y usa el comando dentro del mismo try-with-resources no se necesita agregar ese catch solo para cerrarlo.

Los DAO principales recuperan el identificador generado en `procesarResultadoInsertar`. Una entidad o relación sin ID generado no necesita leer `p_id` por obligación de la base; el procesamiento del resultado corresponde a su implementación.

## Conexiones y transacciones

### Mecanismo utilizado actualmente por el proyecto

`ConexionDAO.ejecutarComando` sigue esta regla:

1. Si `TransactionsManager.activa()` es verdadero, utiliza la conexión de esa transacción. No la cierra ni hace commit o rollback.
2. Si no existe una transacción activa, abre una conexión propia y la cierra al terminar.

Esto conserva la participación de las consultas y escrituras de los DAO sencillos en una transacción iniciada por el BL. El callback se ejecuta de forma síncrona; no crea otro hilo. La conexión de ThreadLocal no se comparte automáticamente con tareas lanzadas en otros hilos.

### Operaciones compuestas preservadas

| DAO | Coordinación que no debe eliminarse al heredar el CRUD |
| --- | --- |
| Administrador y veterinario | Guardar o reemplazar los horarios; retirar sus asociaciones al dar de baja. Pueden iniciar una transacción propia o participar en una existente. |
| Artículo | Guardar las categorías y mantener la variante de actualización con conexión explícita usada al modificar stock. |
| Cita | Guardar sus detalles y conservar sus identificadores al actualizar. |
| Atención | Guardar diagnósticos e insumos; conservar sus pasos de actualización y baja. |
| Receta | Guardar sus medicamentos y reemplazar sus detalles cuando corresponde. |
| Boleta | Guardar líneas por tipo, validar líneas existentes y conservar su identidad al actualizar. |

Citas, atenciones, recetas y boletas mantienen el requisito previo de una transacción activa en sus escrituras, iniciada por el BL. No se sustituyen sus métodos por una llamada a `super.insert` que guarde solo la cabecera. Las operaciones de varias tablas usan la misma conexión y el propietario de la transacción confirma o revierte el conjunto.

### Variante con conexión explícita

`TransaccionDAO` y `TransaccionDAOImpl` conservan los nombres actuales `crear`, `actualizar` y `eliminar`, reciben Connection y mantienen retorno void con `throws SQLException`.

Esta base ejecuta los comandos sobre la conexión recibida sin abrirla, cerrarla ni confirmarla. Está disponible para implementaciones que necesiten ese contrato. No sustituye automáticamente a TransactionsManager ni se expone indiscriminadamente en los agregados: una implementación compuesta debe incluir sus detalles en cualquier variante de escritura que publique.

## Cambios aplicados

1. Unificado el mapeo principal a `mapear(ResultSet)` en todos los DAO.
2. Eliminados adaptadores de dos parámetros, `mapearModelo` y sobrescrituras que solo delegaban a super.
3. Completados los comandos y mapeos que estaban vacíos en la refactorización en curso, restaurando las operaciones completas de administrador y atención.
4. Extendida la reutilización de BaseDAOImpl a los DAO principales, conservando las sobrescrituras justificadas de operaciones compuestas.
5. Compartidos los campos de artículo y servicio mediante ProductoDAOImpl.
6. Conservadas las relaciones de las entidades, la visibilidad de los contratos internos y los procedimientos almacenados existentes.
7. Centralizada la selección de conexión en ConexionDAO y mantenido el control de la transacción en su propietario.
8. Añadidas comprobaciones JDBC sin conexión a MySQL para detectar omisiones de comandos, detalles y cierres de recursos.

No se cambian las reglas del BL, los contratos REST, el esquema de la BD ni las pantallas C# como parte de esta refactorización.

## Verificación

Se requiere un JDK compatible con el proyecto (compilación configurada para Java 25) y Maven. Desde `Avances`:

```powershell
mvn -pl patitasarriba-negocio -am test
mvn -pl patitasarriba-rest -am compile
```

Las comprobaciones de persistencia están en `src/test/java/patitasarriba/daoimpl/DAORefactorChecks.java`. No requieren JUnit, credenciales ni base de datos real. Son un programa de comprobación con main: Maven lo compila, pero no lo ejecuta automáticamente. Después del primer comando:

```powershell
java -cp "patitasarriba-persistencia/target/test-classes;patitasarriba-persistencia/target/classes;patitasarriba-modelo/target/classes;patitasarriba-dbmanager/target/classes" patitasarriba.daoimpl.DAORefactorChecks
```

La batería verifica el CRUD de los siete DAO sencillos; lecturas vacías en los 14 DAO principales; IDs generados; mapeo de diagnósticos y producto; propagación de excepciones y errores suprimidos; cierre de recursos; conexión compartida; commit/rollback; operaciones compuestas con detalles; y ejecución explícita para una entidad de prueba sin atributo ID.

Estas pruebas usan JDBC simulado. Confirman las llamadas y el manejo de recursos, pero no validan la ejecución real de los procedimientos, las restricciones de MySQL ni el comportamiento concurrente. Antes de desplegar, probar con la base de desarrollo los CRUD y los recorridos completos de cita, atención, receta y boleta, incluidos casos de rollback y recarga desde REST.
