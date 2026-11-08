
# Proyecto 2: Servicio de Préstamos de Equipos de Laboratorio (SOA)

Este repositorio contiene el servicio de **Préstamos y Reservas de Equipos de Laboratorio**, desarrollado con **Java 21 / Spring Boot** y **PostgreSQL**, junto con una interfaz web construida con **HTML5, CSS3 y JavaScript**.

El servicio permite registrar equipos en el catálogo existente, realizar reservas, consultar préstamos y cancelarlos.

Para funcionar correctamente, consume la API del proyecto **`laboratorio`**, encargado de gestionar los equipos.

---

## 🖥️ Servicio de Préstamos de Laboratorio (Puerto `8086`)

* **Proyecto:** `prestamos-laboratorio`
* **Puerto:** `8086`
* **Base de Datos:** PostgreSQL — `bd_prestamos_laboratorio`
* **Lenguaje:** Java 21
* **Framework:** Spring Boot
* **IDE utilizado:** Apache NetBeans 31
* **Consumidor Web:** HTML5, CSS3 y JavaScript Fetch API
* **Interfaz:** `src/main/resources/static/index.html`
* **Servicio externo requerido:** `laboratorio` (Puerto `8085`)

### 1. Requisitos para Ejecutar el Proyecto

Antes de iniciar el servicio, es necesario contar con las siguientes herramientas:

1. Java JDK 21.
2. Apache NetBeans 31 o un entorno compatible con proyectos Maven.
3. PostgreSQL instalado y ejecutándose.
4. pgAdmin 4 (opcional, para administrar las bases de datos).
5. Proyecto `laboratorio` configurado y funcionando.
6. Proyecto `prestamos-laboratorio`.

**Importante:** El servicio de préstamos utiliza una base de datos independiente de la utilizada por el servicio de equipos.

---

## 🗄️ 2. Configuración de Base de Datos

Abre **pgAdmin** o **psql** y ejecuta el siguiente script SQL para crear la base de datos del servicio de préstamos:

```sql
CREATE DATABASE bd_prestamos_laboratorio;
```

El proyecto utiliza la base de datos:

`bd_prestamos_laboratorio`

El servicio `laboratorio` utiliza una base de datos diferente:

`bd_laboratorio`

Las dos bases de datos deben existir en PostgreSQL.

### Configuración de Spring Boot

Abre el archivo:

`src/main/resources/application.properties`

Verifica que la configuración de conexión apunte a la base de datos `bd_prestamos_laboratorio`.

También debes configurar las credenciales correspondientes a tu instalación local de PostgreSQL.

> **Nota:** Utiliza el usuario y la contraseña configurados en tu instalación. No es necesario modificar la base de datos del proyecto `laboratorio`.

Comprueba además que el servicio de préstamos tenga configurado el puerto:

```properties
server.port=8086
```

Y que la dirección del servicio de equipos sea:

```properties
equipos.url=http://localhost:8085
```

Con esta configuración, `prestamos-laboratorio` puede consumir los endpoints del servicio `laboratorio`.

Las demás propiedades de conexión y persistencia deben conservar los valores compatibles con el proyecto existente.

---

## 🔗 3. Integración entre Servicios (Puerto `8085` ↔ Puerto `8086`)

El sistema utiliza dos servicios independientes:

| Servicio | Puerto | Base de Datos | Función |
| :--- | :--- | :--- | :--- |
| `laboratorio` | `8085` | `bd_laboratorio` | Gestión del catálogo de equipos |
| `prestamos-laboratorio` | `8086` | `bd_prestamos_laboratorio` | Gestión de préstamos y reservas |

### Funcionamiento de la Integración

1. El usuario accede a la interfaz web de `prestamos-laboratorio`.
2. Cuando registra un equipo, el servicio de préstamos envía una solicitud HTTP al servicio `laboratorio`.
3. El servicio `laboratorio` registra el equipo y devuelve la información correspondiente.
4. Cuando el usuario solicita una reserva, `prestamos-laboratorio` consulta el catálogo de equipos mediante HTTP.
5. El sistema busca un equipo cuyo nombre coincida con el nombre proporcionado.
6. Si el equipo existe, está disponible y no tiene préstamos activos o reservados registrados en el servicio de préstamos, se genera una nueva reserva.
7. La reserva se almacena en la base de datos `bd_prestamos_laboratorio`.

La comunicación con el servicio de equipos utiliza `RestClient`, con los siguientes tiempos de espera:

* **Conexión:** 3 segundos.
* **Lectura:** 5 segundos.

**Importante:** Actualmente las reservas se controlan mediante los registros locales del servicio de préstamos. Reservar o cancelar un préstamo no modifica automáticamente el campo `disponible` del servicio `laboratorio`.

---

## 🛠️ 4. Contrato de la API de Préstamos (`/api/prestamos`)

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/prestamos` | Lista los préstamos registrados |
| `GET` | `/api/prestamos/{id}` | Busca un préstamo por su identificador |
| `POST` | `/api/prestamos` | Registro de préstamo mediante el contrato anterior |
| `POST` | `/api/prestamos/reservas` | Reserva mediante el contrato anterior |
| `POST` | `/api/prestamos/reservas/por-nombre` | Reserva un equipo existente mediante su nombre |
| `POST` | `/api/prestamos/{id}/confirmar` | Confirma una reserva mediante la API anterior |
| `POST` | `/api/prestamos/{id}/cancelar` | Cancela un préstamo registrado |

Las rutas anteriores de registro, reserva y confirmación se conservan por compatibilidad, aunque no todas aparecen en la interfaz web actual.

### Registrar Equipo

**Método:**

`POST /api/prestamos/equipos`

**Entrada JSON:**

```json
{
  "nombre": "Osciloscopio de prueba"
}
```

El servicio envía la solicitud correspondiente al catálogo de equipos.

El equipo se registra con:

* Nombre proporcionado.
* Estado `DISPONIBLE`.
* Disponibilidad `true`.

**Respuesta esperada:** `201 Created`, con información del equipo registrado.

### Reservar Equipo por Nombre

**Método:**

`POST /api/prestamos/reservas/por-nombre`

**Entrada JSON:**

```json
{
  "nombre": "Osciloscopio de prueba"
}
```

El servicio busca el equipo en el catálogo y crea un préstamo con estado `RESERVADO`, siempre que cumpla las validaciones establecidas.

**Respuesta esperada:** `201 Created`.

### Consultar Préstamos

**Método:**

`GET /api/prestamos`

Devuelve los préstamos registrados, incluyendo su identificador, equipo asociado y estado.

### Buscar Préstamo por ID

**Método:**

`GET /api/prestamos/{id}`

Ejemplo:

`GET http://localhost:8086/api/prestamos/18`

El número `18` es solamente un ejemplo. Debe utilizarse el identificador real del préstamo que se desea consultar.

### Cancelar Préstamo

**Método:**

`POST /api/prestamos/{id}/cancelar`

La operación cambia el estado del préstamo a `CANCELADO` y registra su fecha de cancelación.

---

## 📋 5. Reglas de Negocio Implementadas

1. **Registro de equipos:** El nombre del equipo es obligatorio y no puede estar vacío.

2. **Reserva por nombre:** El equipo debe existir previamente en el catálogo del servicio `laboratorio`.

3. **Nombres repetidos:** Si varios equipos tienen el mismo nombre, la búsqueda por nombre devuelve `409 Conflict` para evitar seleccionar un equipo ambiguo.

4. **Disponibilidad:** El equipo debe tener `disponible = true` para permitir la reserva.

5. **Prevención de reservas simultáneas:** No se permite reservar un equipo que ya tenga un préstamo local en estado `ACTIVO` o `RESERVADO`.

6. **Cancelación:** Un préstamo cancelado cambia su estado a `CANCELADO` y conserva su información registrada.

7. **Cancelación repetida:** Si se intenta cancelar nuevamente un préstamo ya cancelado, el servicio devuelve `409 Conflict`.

8. **Identificadores:** El ID del equipo y el ID del préstamo son identificadores distintos.

9. **Persistencia:** Los préstamos se gestionan mediante Spring Data JPA y PostgreSQL.

### Estados de los Préstamos

| Estado | Descripción |
| :--- | :--- |
| `ACTIVO` | Préstamo activo |
| `RESERVADO` | Equipo reservado |
| `CANCELADO` | Préstamo cancelado |

---

## 🚀 6. Cómo Ejecutar el Proyecto en NetBeans

### Paso 1. Iniciar PostgreSQL

Comprueba que PostgreSQL esté ejecutándose y que existan ambas bases de datos:

* `bd_laboratorio`
* `bd_prestamos_laboratorio`

### Paso 2. Ejecutar el Servicio de Equipos

Abre el proyecto `laboratorio` en NetBeans.

Ejecuta el proyecto y comprueba que el servicio responda en:

`http://localhost:8085/api/equipos`

Esta dirección debe permitir consultar el catálogo de equipos.

### Paso 3. Ejecutar el Servicio de Préstamos

Abre el proyecto `prestamos-laboratorio` en NetBeans.

Verifica la configuración de `application.properties`.

Ejecuta el proyecto utilizando **Run Project**.

Comprueba que Spring Boot inicie correctamente en el puerto `8086`.

### Paso 4. Abrir la Interfaz Web

Abre tu navegador y escribe:

`http://localhost:8086/`

La aplicación mostrará las opciones para:

* Consultar préstamos.
* Buscar préstamos por ID.
* Registrar equipos.
* Reservar equipos existentes.
* Cancelar préstamos.

### Paso 5. Probar una Reserva

1. Escribe el nombre de un equipo nuevo.
2. Pulsa **Registrar Equipo** una sola vez.
3. Conserva el mismo nombre.
4. Pulsa **Reservar equipo**.
5. Comprueba que el sistema genere un préstamo con estado `RESERVADO`.
6. Anota el identificador del préstamo.
7. Utiliza ese identificador en la sección **Cancelar Préstamo**.
8. Comprueba que el estado cambie a `CANCELADO`.
9. Sin registrar nuevamente el equipo, utiliza **Reservar equipo** para comprobar que puede generarse una nueva reserva.

**Importante:** No vuelvas a pulsar Registrar Equipo para repetir una reserva. Ese botón crea un nuevo equipo en el catálogo.

---

## ⚠️ 7. Errores Comunes

| Error | Posible causa |
| :--- | :--- |
| `404 Not Found` | El equipo o préstamo solicitado no existe |
| `409 Conflict` | El equipo ya tiene un préstamo activo o reservado, existen nombres duplicados o se intenta cancelar un préstamo ya cancelado |
| Error de conexión al puerto `8085` | El servicio `laboratorio` no está ejecutándose o no es accesible |
| Error de conexión a PostgreSQL | La base de datos o las credenciales de conexión no son correctas |
| La interfaz no refleja cambios | El navegador conserva una versión anterior de los archivos estáticos |

Después de modificar código Java, es necesario detener y volver a ejecutar el proyecto `prestamos-laboratorio`.

Después de modificar la interfaz web, recarga la página utilizando:

`Ctrl + F5`

**Nota:** Obtener `BUILD SUCCESS` confirma que la compilación terminó correctamente, pero no demuestra por sí solo que la aplicación esté funcionando correctamente.

---

## ✅ 8. Pruebas Funcionales

Se han comprobado las siguientes operaciones:

* Registro de equipos disponibles mediante la interfaz.
* Creación de reservas por nombre.
* Prevención de reservas repetidas para un equipo con préstamo activo o reservado.
* Respuesta `404` al reservar un equipo inexistente.
* Cancelación de préstamos.
* Consulta de préstamos cancelados.
* Respuesta `409` cuando existen varios equipos con el mismo nombre.

### Verificaciones Pendientes

Antes de considerar el servicio completamente validado, se debe comprobar:

1. Que un equipo pueda reservarse nuevamente después de cancelar su préstamo anterior, sin registrarlo otra vez.
2. Que los préstamos permanezcan almacenados después de reiniciar `prestamos-laboratorio`.
3. Que las operaciones principales sigan funcionando conjuntamente después de los ajustes finales.

---

## 📌 9. Consideraciones Finales

El proyecto `prestamos-laboratorio` funciona como un servicio independiente y consume la API existente del servicio `laboratorio`.

Cada servicio utiliza su propia base de datos y su propio puerto.

No es necesario modificar el código del proyecto `laboratorio` para ejecutar las operaciones documentadas.

La interfaz web permite administrar las operaciones principales de préstamos y reservas, mientras que el backend conserva algunas rutas anteriores por compatibilidad.

**Limitación conocida:** La disponibilidad almacenada en el catálogo del servicio `laboratorio` no se sincroniza automáticamente con las reservas y cancelaciones registradas en `prestamos-laboratorio`.