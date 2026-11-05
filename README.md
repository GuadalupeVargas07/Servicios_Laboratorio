# Proyecto 1: Préstamo de Equipo de Laboratorio (SOA)

Este repositorio contiene los servicios del sistema de **Préstamo de Equipo de Laboratorio**, desarrollados con **Java 21 / Spring Boot** y **PostgreSQL**, junto con su interfaz consumidora web (`consumidor-laboratorio`).

---

## 🖥️ Servicio N° 1: Gestión de Equipos de Laboratorio (Puerto `8085`)

* **Carpeta:** Raíz del repositorio (`/src`)
* **Puerto:** `8085`
* **Base de Datos:** PostgreSQL — `bd_laboratorio`
* **Consumidor Web:** HTML5, CSS3, JavaScript Fetch API (`/consumidor-laboratorio`)

### Configuración de Base de Datos (Servicio 1)
Ejecuta en PostgreSQL (pgAdmin o psql):
```sql
CREATE DATABASE bd_laboratorio;
```
> En `src/main/resources/application.properties`, asegúrate de colocar tu contraseña local de PostgreSQL en `spring.datasource.password`.

---

## 🛠️ Servicio N° 4 (Personalizado): Mantenimiento de Equipos (Puerto `8088`)

* **Carpeta:** `/mantenimiento`
* **Puerto:** `8088`
* **Base de Datos:** PostgreSQL — `mantenimiento_soa`
* **Usuario de BD propio:** `mantenimiento_app`
* **Problema que resuelve:** Permite reportar fallas en los equipos de laboratorio, bloquear automáticamente su disponibilidad para impedir que un equipo averiado sea prestado, y liberarlo una vez concluida su revisión/reparación.

### 1. Configuración de Base de Datos para Mantenimiento (Ejecutar antes de correr el Servicio 4)
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

## 🔗 Cómo funciona la Integración actual (Servicio 1 ↔ Servicio 4)

1. Levantar primero el proyecto **`mantenimiento`** (corre en `http://localhost:8088`).
2. Levantar el proyecto **`laboratorio`** (corre en `http://localhost:8085`).
3. En `EquipoService.java` (Servicio 1), cada vez que se consulta el catálogo (`GET /api/equipos`), un equipo por ID (`GET /api/equipos/{id}`) o los equipos disponibles (`GET /api/equipos/disponibles`), el **Servicio 1 consulta vía HTTP `RestClient` al Servicio 4** (`GET http://localhost:8088/api/mantenimiento/equipos/{id}/estado`) con tiempos de espera configurados (3s conexión / 5s lectura).
4. Si el Servicio 4 indica `"bloqueado": true`:
   * El Servicio 1 cambia dinámicamente el equipo a `estado = "MANTENIMIENTO"` y `disponible = false`.
   * En la página web (`consumidor-laboratorio/index.html`), el equipo aparece bloqueado y no se incluye en el filtro de disponibles hasta que su reporte sea liberado en el Servicio 4 (`PUT /api/mantenimiento/reportes/{id}/liberacion`).
