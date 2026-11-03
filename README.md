# Servicio N° 1: Gestión de Equipos de Laboratorio

Este repositorio contiene el **Servicio N° 1 (Equipos)** del sistema de gestión de laboratorios, desarrollado con **Spring Boot 3** y **PostgreSQL**, junto con su interfaz de consumo web estática (HTML5 / JavaScript Fetch API).

---

## 🛠️ Especificaciones Técnicas y Arquitectura

* **Base de Datos:** PostgreSQL — Base de datos `bd_laboratorio`
* **Consumidor Web:** HTML5, CSS3, JavaScript ES6 / Fetch API (Puerto `5500`)
* **Persistencia:** JPA / Hibernate con PostgreSQL Driver

---

## 🗄️ Configuración de la Base de Datos (PostgreSQL)

Antes de iniciar el servicio backend, es necesario crear la base de datos en PostgreSQL.

### 1. Crear la base de datos
Ejecuta la siguiente instrucción en tu cliente de PostgreSQL (pgAdmin, DBeaver o psql):

```sql
CREATE DATABASE bd_laboratorio;
