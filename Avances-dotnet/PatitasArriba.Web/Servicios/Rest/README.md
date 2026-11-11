# Servicios REST explícitos

Cada clase implementa su interfaz directamente. FindAll, FindById, Insert, Update y Delete muestran la URL, el DTO y la llamada a HttpUtils. ToViewModels, ToViewModel y ToDto están en el mismo archivo, siguiendo el estilo del profesor. No hay clase base de implementación ni mapper compartido.

La configuración sigue siendo RestApiBaseUrl y el registro de dependencias sigue en AddPatitasRestServices.

Se conserva el comportamiento actual de Java: registro de personas por /con-cuenta, login con correo y password, número de boleta asignado por el backend, fotos de mascotas y duraciones opcionales. FindById devuelve null ante 404; HttpUtils conserva los errores del backend.

Las consultas adicionales de citas (atenciones y recetas) y de propietarios de cuentas se ven directamente en los métodos públicos. Son necesarias para completar los campos de los ViewModels que no llegan en el DTO principal. Las conversiones de colecciones usan foreach.

Las pantallas conservan su funcionamiento actual. No se modifican las reglas de negocio ni se sustituye la sesión de demostración como parte de esta reorganización.
