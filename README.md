# TiendaUniApi

Backend del marketplace universitario verificado **TiendaUni**: una plataforma cerrada por universidad donde los
estudiantes compran y venden productos dentro de su propia institución, con acceso verificado mediante correo
institucional.

> Este repositorio contiene únicamente la API REST. El frontend se desarrolla en un repositorio independiente
> ([TiendaUni · Frontend](https://github.com/EmirPolito/tienda_uni_front)).

---

## Tabla de contenidos

- [Descripción del proyecto](#descripción-del-proyecto)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura y modelo de datos](#arquitectura-y-modelo-de-datos)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Configuración local](#configuración-local)
- [Ejecución](#ejecución)
- [Docker (desarrollo y despliegue)](#docker-desarrollo-y-despliegue)
- [Empaquetado y despliegue](#empaquetado-y-despliegue)
- [Pruebas](#pruebas)
- [Documentación API (REST Docs)](#documentación-api-rest-docs)
- [Seguridad y autenticación](#seguridad-y-autenticación)
- [Notificaciones en tiempo real (SSE)](#notificaciones-en-tiempo-real-sse)
- [Convenciones del proyecto](#convenciones-del-proyecto)
- [Proyecto relacionado (Frontend)](#proyecto-relacionado-frontend)
- [Equipo](#equipo)

---

## Descripción del proyecto

TiendaUni reemplaza la compraventa desorganizada que hoy ocurre en grupos de WhatsApp y Facebook. Cada universidad opera
como un **espacio cerrado (multi-tenant)** al que solo acceden estudiantes con correo institucional válido.

Funcionalidades núcleo del backend:

- Registro y verificación de usuarios por dominio de correo institucional.
- Publicaciones con vigencia temporal (24 a 48 h) y control de inventario flexible.
- Sistema de preventa y reservas.
- Reputación por publicación y acumulada por vendedor (estrellas).
- Mensajería privada comprador–vendedor y chat general por universidad.
- Notificaciones en tiempo real vía **Server-Sent Events (SSE)**.
- Moderación comunitaria mediante reportes.

El procesamiento de pagos queda fuera del alcance: todas las transacciones son presenciales.

## Stack tecnológico

| Capa           | Tecnología                                                         |
|:---------------|:-------------------------------------------------------------------|
| Lenguaje       | Java 21                                                            |
| Framework      | Spring Boot **4.1.0**                                              |
| Persistencia   | Spring Data JPA + Hibernate                                        |
| Base de datos  | PostgreSQL                                                         |
| Seguridad      | Spring Security (JWT + Refresh Tokens en cookies `HTTP-Only`)      |
| Validación     | Spring Boot Validation (Jakarta Bean Validation)                   |
| Observabilidad | Spring Boot Actuator                                               |
| Utilidades     | Lombok                                                             |
| Empaquetado    | WAR (compatible con Tomcat externo gracias a `ServletInitializer`) |
| Build          | Maven (Maven Wrapper incluido)                                     |
| Testing        | JUnit 5, Spring Boot Test, Spring REST Docs (Asciidoctor → HTML)   |

## Arquitectura y modelo de datos

- **Multi-tenant por universidad**: el aislamiento se garantiza a nivel de fila propagando `university_id` a todas las
  entidades transaccionales.
- **Normalización 3NF** con particionamiento vertical estratégico (`users` ↔ `profiles`, `publications` ↔ `products`).
- **Identificadores** UUID v4 para entidades de negocio; `BIGSERIAL`/`SERIAL` únicamente para tags y logs.
- **Enums** definidos como `CREATE TYPE` en PostgreSQL (`MediaType`, `SaleType`, `TagType`, `NotificationType`).
- **Verificación** por dominio de correo institucional validado con expresión regular.
- Contraseñas almacenadas con `bcrypt`.

El detalle completo del esquema (20 tablas + 4 ENUMs, índices, constraints y reglas de negocio) se encuentra en [
`internal-doc/diseno_de_base_de_datos.md`](internal-doc/diseno_de_base_de_datos.md).

## Estructura del proyecto

```
TiendaUniApi/
├── pom.xml
├── mvnw / mvnw.cmd              # Maven Wrapper
├── src/
│   ├── main/
│   │   ├── java/tienda/uni/api/
│   │   │   ├── TiendaUniApiApplication.java   # Entry point
│   │   │   └── ServletInitializer.java        # Despliegue en contenedor externo
│   │   └── resources/
│   │       └── application.yaml                # Configuración por defecto
│   └── test/
│       └── java/tienda/uni/api/
│           └── TiendaUniApiApplicationTests.java
└── internal-doc/                 # Documentación interna (no se publica en el repo)
    ├── tiendauni_documento_general.md
    ├── tiendauni_propuestas.md
    ├── tiendauni_tecnologica.md
    └── diseno_de_base_de_datos.md
```

> El directorio `internal-doc/` está ignorado por Git. Conserva los borradores y decisiones de diseño que no forman
> parte de la documentación pública.

## Requisitos previos

- **JDK 21** (Temurin, Zulu o equivalente).
- **Maven 3.9+** o usar el `mvnw` provisto.
- **PostgreSQL 14+** accesible desde el entorno de desarrollo.
- Cliente HTTP para probar los endpoints (curl, HTTPie, Postman, etc.).

## Configuración local

`src/main/resources/application.yaml` solo define el nombre de la aplicación. Los valores sensibles (credenciales de BD,
secretos JWT, orígenes permitidos, etc.) se sobrescriben mediante variables de entorno o un perfil externo en entornos
reales.

Ejemplo de configuración para desarrollo local usando variables de entorno:

```bash
export SPRING_PROFILES_ACTIVE=dev

# Base de datos
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/tiendauni
export SPRING_DATASOURCE_USERNAME=tiendauni
export SPRING_DATASOURCE_PASSWORD=changeme

# JPA
export SPRING_JPA_HIBERNATE_DDL_AUTO=validate
export SPRING_JPA_PROPERTIES_HIBERNATE_DEFAULT_SCHEMA=public

# Servidor
export SERVER_PORT=8080
```

> Ajusta los nombres de variables según el perfil real de Spring que utilices (`application-dev.yaml`, secretos, etc.).

## Ejecución

### Modo desarrollo

```bash
./mvnw spring-boot:run
```

La API quedará escuchando en `http://localhost:8080`.

### Ejecutar tests

```bash
./mvnw test
```

### Verificar el build completo

```bash
./mvnw clean verify
```

## Docker (desarrollo y despliegue)

El proyecto incluye un `Dockerfile` multi-stage y un `docker-compose.dev.yaml` listos para usar. Para instrucciones
detalladas de **cómo levantar los contenedores de desarrollo y construir la imagen de producción** consultá la guía
dedicada:

- [`DOCKER.md`](DOCKER.md) — variables de entorno, `docker compose up`, build de la imagen, publicación en registry y
  checklist de despliegue.

## Empaquetado y despliegue

El artefacto se empaqueta como **WAR** para poder desplegarse en un Tomcat externo (u otro contenedor compatible con
Spring Boot 4.x):

```bash
./mvnw clean package
```

El archivo se genera en `target/TiendaUniApi-0.0.1-SNAPSHOT.war`. La clase `ServletInitializer` hereda de
`SpringBootServletInitializer` y permite el despliegue tradicional junto a la ejecución embebida vía `main`.

Despliegue en Tomcat externo:

```bash
cp target/TiendaUniApi-0.0.1-SNAPSHOT.war $CATALINA_HOME/webapps/
```

## Documentación API (REST Docs)

El proyecto incluye **Spring REST Docs**: los snippets se generan durante la fase de tests y se compilan con el plugin
**Asciidoctor** para producir documentación HTML en la fase `prepare-package`.

```bash
./mvnw clean verify
```

La documentación resultante se publica en `target/generated-docs/`.

## Seguridad y autenticación

- **Spring Security** como capa principal.
- Autenticación basada en **JWT** con **Refresh Tokens** almacenados en cookies `HTTP-Only` (mitigación de XSS).
- Verificación de pertenencia por **dominio de correo institucional** (ej. `@alumnos.utsv.edu.mx`). El sistema deduce la
  universidad a partir del dominio.
- Contraseñas hasheadas con `bcrypt` o `argon2`.
- Restricciones `CHECK` a nivel BD para evitar auto-mensajes y duplicados en reportes.

## Notificaciones en tiempo real (SSE)

Se utiliza **Server-Sent Events** en lugar de WebSockets por ser un mecanismo más ligero, nativo sobre HTTP y suficiente
para notificaciones unidireccionales (mensajes nuevos, reservas, eventos del flujo de encuentro, etc.). Mantén la
conexión SSE dentro del mismo dominio y detrás de la misma configuración de Spring Security que el resto de la API.

## Convenciones del proyecto

- **Lenguaje**: código, identificadores y nombres de paquetes en **inglés**; documentación y mensajes de cara al usuario
  en **español**.
- **Base de datos**: `snake_case`, tablas en plural, columnas en singular.
- **Java**: respetar el estilo por defecto de Spring Boot; preferir inyección por constructor y records para DTOs
  inmutables.
- **Lombok**: usado para reducir boilerplate (`@Getter`, `@Setter`, `@Builder`, etc.); el plugin de Maven lo configura
  como procesador de anotaciones.
- **Git**: mensajes de commit en presente y siguiendo el formato `tipo: descripción` (ej.
  `feat: add publication endpoints`).

---

## Proyecto relacionado (Frontend)

La aplicación cliente que consume esta API vive en un repositorio aparte. Mantenlos enlazados desde aquí para facilitar
el seguimiento del trabajo entre equipos.

| Repositorio  | Descripción                                                                                       |
|:-------------|:--------------------------------------------------------------------------------------------------|
| **Backend**  | _Este repositorio_ — API REST en Spring Boot.                                                     |
| **Frontend** | [TiendaUni · Frontend](https://github.com/EmirPolito/tienda_uni_front) |

## Equipo

Este proyecto es resultado del trabajo colaborativo entre el equipo de **Backend** y el de **Frontend**.

<table align="center">
  <tr>
    <td align="center" width="50%">
      <a href="https://github.com/alberto-cruz-mtz">
        <img
          style="border-radius: 100%;"
          src="https://github.com/alberto-cruz-mtz.png?size=120"
          width="120"
          alt="Avatar de alberto-cruz-mtz"
        />
      </a>
      <br /><br />
      <a href="https://github.com/alberto-cruz-mtz">
        <strong>Jose Alberto Cruz Martínez</strong>
      </a>
      <br /><br />
      <a href="https://github.com/alberto-cruz-mtz">
        <img
          src="https://img.shields.io/badge/GitHub-@alberto--cruz--mtz-181717?style=for-the-badge&logo=github&logoColor=white"
          alt="GitHub"
        />
      </a>
      <br /><br />
      <img
        src="https://img.shields.io/badge/Rol-Backend-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"
        alt="Rol: Backend"
      />
      <p><em>Spring Boot · PostgreSQL · Seguridad</em></p>
    </td>
    <td align="center" width="50%">
      <a href="https://github.com/EmirPolito">
        <img
          src="https://github.com/EmirPolito.png?size=120"
          width="120"
          style="border-radius: 100%;"
          alt="Avatar de EmirPolito"
        />
      </a>
      <br /><br />
      <a href="https://github.com/EmirPolito">
        <strong>Emir Polito Guevara</strong>
      </a>
      <br /><br />
      <a href="https://github.com/EmirPolito">
        <img
          src="https://img.shields.io/badge/GitHub-@EmirPolito-181717?style=for-the-badge&logo=github&logoColor=white"
          alt="GitHub"
        />
      </a>
      <br /><br />
      <img
        src="https://img.shields.io/badge/Rol-Frontend-61DAFB?style=for-the-badge&logo=react&logoColor=white"
        alt="Rol: Frontend"
      />
      <p><em>UI/UX · Integración con la API</em></p>
    </td>
  </tr>
</table>
