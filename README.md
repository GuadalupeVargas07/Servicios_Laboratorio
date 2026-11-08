# Proyecto 1: Préstamo de Equipo de Laboratorio (SOA)

Este repositorio contiene los servicios del sistema de **Préstamo de Equipo de Laboratorio**, desarrollados bajo una Arquitectura Orientada a Servicios (**SOA**) con **Java 21 / Spring Boot** y **PostgreSQL**, junto con sus interfaces consumidoras web (**HTML5, CSS3 y JavaScript Fetch API**).

---

## 📋 Resumen de Arquitectura y Puertos

| Servicio | Carpeta | Puerto | Base de Datos (PostgreSQL) | Responsabilidad Principal |
| :--- | :--- | :---: | :--- | :--- |
| **Servicio N° 1: Equipos** | Raíz (`/src`) | `8085` | `bd_laboratorio` | Catálogo de equipos, estado operacional y disponibilidad. |
| **Servicio N° 2: Préstamos** | `/prestamos-laboratorio` | `8086` | `bd_prestamos_laboratorio` | Reserva, registro, confirmación, consulta y cancelación de préstamos. |
| **Servicio N° 4: Mantenimiento** | `/mantenimiento` | `8088` | `mantenimiento_soa` | Reporte de fallas, bloqueo automático de equipos en revisión y liberación. |
| **Consumidor Web General** | `/consumidor-laboratorio` | `5500` | *N/A (Frontend)* | Gestión del catálogo de equipos y panel de mantenimiento/bloqueo. |

---

## 🖥️ Servicio N° 1: Gestión de Equipos de Laboratorio (Puerto `8085`)

* **Carpeta:** Raíz del repositorio (`/src`)
* **Puerto:** `8085`
* **Base de Datos:** PostgreSQL — `bd_laboratorio`
* **Consumidor Web:** `/consumidor-laboratorio` (`index.html` y `app.js`)

### 1. Configuración de Base de Datos (Servicio 1)
Ejecuta en PostgreSQL (**pgAdmin** o **psql**):
```sql
CREATE DATABASE bd_laboratorio;
```
> En `src/main/resources/application.properties`, asegúrate de colocar tu contraseña local de PostgreSQL en `spring.datasource.password` y verificar que incluya `mantenimiento.url=http://localhost:8088`.

### 2. Contrato de la API de Equipos (`/api/equipos`)

| Método | Ruta | Descripción | Respuesta / Códigos HTTP |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/equipos` | Lista el catálogo completo de equipos (sincronizado con Mantenimiento). | `200 OK` (Lista de `Equipo`) |
| `GET` | `/api/equipos/{id}` | Obtiene el detalle de un equipo por su ID. | `200 OK` / `404 Not Found` |
| `GET` | `/api/equipos/disponibles` | Lista únicamente los equipos con `disponible = true` (excluye bloqueados). | `200 OK` (Lista de `Equipo`) |
| `GET` | `/api/equipos/estado/{estado}` | Filtra equipos por su estado operacional (`DISPONIBLE`, `MANTENIMIENTO`, etc.). | `200 OK` (Lista de `Equipo`) |
| `POST` | `/api/equipos` | Registra un nuevo equipo en el catálogo. | `200 OK` (`Equipo` creado) |
| `PUT` | `/api/equipos/{id}` | Actualiza los datos o estado de un equipo existente. | `200 OK` / `404 Not Found` |

---

## 📚 Servicio N° 2: Préstamos y Reservas de Laboratorio (Puerto `8086`)

* **Carpeta:** `/prestamos-laboratorio`
* **Puerto:** `8086`
* **Base de Datos:** PostgreSQL — `bd_prestamos_laboratorio`
* **Interfaz Web integrada:** `http://localhost:8086/` (`src/main/resources/static/index.html`)
* **Comunicación saliente:** Consume al **Servicio 1 (`Equipos`)** en `http://localhost:8085` mediante `RestClient` (timeouts: 3s conexión / 5s lectura).

### 1. Configuración de Base de Datos (Servicio 2)
Ejecuta en PostgreSQL (**pgAdmin** o **psql**):
```sql
CREATE DATABASE bd_prestamos_laboratorio;
```
> En `prestamos-laboratorio/src/main/resources/application.properties`, verifica las credenciales de PostgreSQL (`spring.datasource.password`), el puerto `server.port=8086` y la propiedad `equipos.url=http://localhost:8085`.

### 2. Contrato de la API de Préstamos (`/api/prestamos`)

| Método | Ruta | Descripción | Entrada (JSON) | Respuesta / Códigos HTTP |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/prestamos` | Lista todos los préstamos y reservas registrados. | *Ninguna* | `200 OK` (Lista de `Prestamo`) |
| `GET` | `/api/prestamos/{id}` | Busca un préstamo por su identificador. | *Path param:* `id` | `200 OK` / `400 Bad Request` / `404 Not Found` |
| `POST` | `/api/prestamos` | Registra un préstamo directo en estado `ACTIVO`. | `{"equipoId": 1, "matricula": "A01"}` | `201 Created` / `400 Bad Request` / `409 Conflict` |
| `POST` | `/api/prestamos/reservas` | Crea una reserva por ID de equipo (`RESERVADO`). | `{"equipoId": 1, "matricula": "A01"}` | `201 Created` / `409 Conflict` |
| `POST` | `/api/prestamos/reservas/por-nombre` | Reserva un equipo disponible buscando por su nombre. | `{"nombre": "Multímetro"}` | `201 Created` / `404 Not Found` / `409 Conflict` |
| `POST` | `/api/prestamos/{id}/confirmar` | Confirma una reserva pendiente y la pasa a `ACTIVO`. | *Path param:* `id` | `200 OK` / `409 Conflict` |
| `POST` | `/api/prestamos/{id}/cancelar` | Cancela un préstamo o reserva (`CANCELADO`). | *Path param:* `id` | `200 OK` / `404 Not Found` / `409 Conflict` |
| `POST` | `/api/prestamos/equipos` | Registra un equipo nuevo hacia el catálogo del Servicio 1. | `{"nombre": "Osciloscopio"}` | `201 Created` / `502 Bad Gateway` / `503 Unavailable` |

### 3. Reglas de Negocio Implementadas (Servicio 2)
1. **Validación de existencia y disponibilidad:** Antes de crear un préstamo o reserva, consulta al Servicio 1 (`8085`) que el equipo exista y tenga `disponible = true` (si el equipo está bloqueado por el Servicio 4 de Mantenimiento, se rechaza con `409 Conflict`).
2. **Prevención de préstamos simultáneos:** No se permite reservar ni prestar un equipo que ya tenga un registro local en estado `ACTIVO` o `RESERVADO` (`409 Conflict`).
3. **Control de nombres duplicados:** Si existen varios equipos con el mismo nombre, la reserva por nombre devuelve `409 Conflict` para evitar ambigüedad.
4. **Protección en cancelaciones:** No se permite cancelar nuevamente un préstamo que ya se encuentra en estado `CANCELADO` (`409 Conflict`).

---

## 🛠️ Servicio N° 4 (Personalizado): Mantenimiento de Equipos (Puerto `8088`)

* **Carpeta:** `/mantenimiento`
* **Puerto:** `8088`
* **Base de Datos:** PostgreSQL — `mantenimiento_soa`
* **Usuario de BD propio:** `mantenimiento_app`
* **Problema que resuelve:** Permite reportar fallas técnicas en los equipos de laboratorio, bloquear automáticamente su disponibilidad para impedir que un equipo averiado sea reservado o prestado, y liberarlo una vez concluida su revisión o reparación.

### 1. Configuración de Base de Datos para Mantenimiento (Servicio 4)
Abre **pgAdmin** o **psql** y ejecuta el siguiente script SQL para crear la base de datos, el usuario propio y la tabla `reportes_mantenimiento`:

```sql
-- 1. Crear la base de datos y el usuario propio del Servicio 4
CREATE DATABASE mantenimiento_soa;
CREATE USER mantenimiento_app WITH PASSWORD '1234]';

-- 2. Conectarse a la base de datos mantenimiento_soa y crear la tabla
\c mantenimiento_soa

CREATE TABLE IF NOT EXISTS public.reportes_mantenimiento (
    id SERIAL PRIMARY KEY,
    equipo_id INTEGER NOT NULL,
    descripcion_falla VARCHAR(500) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_reporte TIMESTAMPTZ NOT NULL
);

-- 3. Otorgar permisos únicamente sobre su esquema/tabla
GRANT CONNECT ON DATABASE mantenimiento_soa TO mantenimiento_app;
GRANT USAGE ON SCHEMA public TO mantenimiento_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE public.reportes_mantenimiento TO mantenimiento_app;
GRANT USAGE, SELECT ON SEQUENCE public.reportes_mantenimiento_id_seq TO mantenimiento_app;
```
*(Nota: Si en `mantenimiento/src/main/resources/application.properties` usas la contraseña `1234` sin corchete, créalo con `PASSWORD '1234'`).*

### 2. Contrato de la API de Mantenimiento (`/api/mantenimiento`)

| Método | Ruta | Descripción | Entrada (JSON) | Respuesta / Códigos HTTP |
| :--- | :--- | :--- | :--- | :--- |
| `POST` | `/api/mantenimiento/reportes` | Registra una falla y bloquea el equipo (`PENDIENTE`). | `{"equipoId": 1, "descripcionFalla": "No enciende"}` | `201 Created` (`ReporteRespuesta`)<br>`400 Bad Request` (datos inválidos)<br>`409 Conflict` (ya tiene reporte activo) |
| `GET` | `/api/mantenimiento/reportes` | Lista todos los reportes registrados. | *Ninguna* | `200 OK` (Lista de `ReporteRespuesta`) |
| `GET` | `/api/mantenimiento/equipos/{equipoId}/estado` | Consulta si un equipo está bloqueado por mantenimiento. | *Path param:* `equipoId` | `200 OK` (`{"equipoId": 1, "bloqueado": true, "motivo": "..."}`)<br>`400 Bad Request` (`equipoId <= 0`) |
| `PUT` | `/api/mantenimiento/reportes/{reporteId}/liberacion` | Libera el reporte (`LIBERADO`) y desbloquea el equipo. | *Path param:* `reporteId` | `200 OK` (`ReporteRespuesta`)<br>`404 Not Found` (reporte no existe) |

### 3. Reglas de Negocio Implementadas (Servicio 4)
1. `equipoId` debe ser un entero positivo mayor a `0` (`400 Bad Request`).
2. `descripcionFalla` es obligatoria, no puede estar en blanco y no puede exceder 500 caracteres (`400 Bad Request`).
3. **Bloqueo activo sin duplicados:** No se permite abrir un nuevo reporte de falla para un equipo que ya se encuentra en estado `PENDIENTE`, `EN_REVISION` o `BLOQUEADO` (`409 Conflict`).
4. **Idempotencia en liberación:** Si se solicita liberar un reporte que ya estaba en estado `LIBERADO`, el servicio devuelve `200 OK` con el estado actual sin generar error.

---

## 🔗 Flujo de Integración entre los Servicios (`Servicio 2 ↔ Servicio 1 ↔ Servicio 4`)

1. **Orden de encendido:**
   * Iniciar PostgreSQL (`5432`) con las bases `bd_laboratorio`, `bd_prestamos_laboratorio` y `mantenimiento_soa`.
   * Iniciar **Servicio 4 (`mantenimiento`)** en `http://localhost:8088`.
   * Iniciar **Servicio 1 (`laboratorio`)** en `http://localhost:8085`.
   * Iniciar **Servicio 2 (`prestamos-laboratorio`)** en `http://localhost:8086`.
   * Abrir el consumidor web en `http://localhost:5500/index.html` (para Catálogo y Mantenimiento) y `http://localhost:8086/` (para Préstamos y Reservas).
2. **Comunicación en cadena:**
   * Cuando un usuario solicita un préstamo o reserva en el **Servicio 2 (`8086`)**, este consulta vía HTTP `RestClient` al **Servicio 1 (`8085`)** para verificar la disponibilidad del equipo.
   * A su vez, el **Servicio 1 (`8085`)** consulta vía HTTP `RestClient` al **Servicio 4 (`8088`)** (`GET /api/mantenimiento/equipos/{id}/estado`) con tiempos de espera configurados (3s conexión / 5s lectura).
   * Si el **Servicio 4 (`Mantenimiento`)** indica `"bloqueado": true`, el **Servicio 1** marca dinámicamente el equipo como `estado = "MANTENIMIENTO"` y `disponible = false`, provocando que el **Servicio 2 (`Préstamos`)** impida prestar o reservar el equipo averiado (`409 Conflict`).
   * Al liberar el reporte en el **Servicio 4** (`PUT /api/mantenimiento/reportes/{id}/liberacion`), el equipo recupera automáticamente su disponibilidad en el **Servicio 1** y ya puede ser reservado en el **Servicio 2**.
