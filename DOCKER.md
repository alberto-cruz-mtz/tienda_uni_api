# TiendaUniApi · Guía de Docker

Instrucciones para levantar el entorno de desarrollo con Docker Compose y para construir la imagen lista para producción.

> Documento complementario al [`README.md`](README.md). La explicación de arquitectura, modelo de datos y stack vive allí.

---

## Tabla de contenidos

- [Requisitos previos](#requisitos-previos)
- [Desarrollo con Docker Compose](#desarrollo-con-docker-compose)
  - [Variables de entorno](#variables-de-entorno)
  - [Levantar los contenedores](#levantar-los-contenedores)
  - [Acceso a los servicios](#acceso-a-los-servicios)
  - [Carga inicial de la base de datos](#carga-inicial-de-la-base-de-datos)
  - [Comandos útiles](#comandos-útiles)
  - [Resetear el entorno](#resetear-el-entorno)
- [Imagen para producción](#imagen-para-producción)
  - [Construir la imagen](#construir-la-imagen)
  - [Ejecutar la imagen en local](#ejecutar-la-imagen-en-local)
  - [Publicar en un registry](#publicar-en-un-registry)
  - [Checklist de despliegue](#checklist-de-despliegue)
- [Solución de problemas](#solución-de-problemas)

---

## Requisitos previos

- **Docker Engine 24+** y **Docker Compose v2** (integrado en `docker compose`, no requiere binario legacy).
- **Git** para clonar el repositorio.
- No hace falta JDK ni Maven local: la imagen multi-stage compila dentro de Docker.

Verificá la versión:

```bash
docker --version
docker compose version
```

---

## Desarrollo con Docker Compose

El archivo [`docker-compose.dev.yaml`](docker-compose.dev.yaml) orquesta dos servicios:

| Servicio  | Imagen base                  | Puerto host → contenedor | Rol                                      |
|:----------|:-----------------------------|:-------------------------|:-----------------------------------------|
| `backend` | build local desde `Dockerfile` | `8080 → 8080`            | API Spring Boot                          |
| `postgres`| `postgres:18-alpine`         | `5435 → 5432`            | Base de datos PostgreSQL 18              |

> El perfil de Spring activo en este entorno es `docker` (ver `src/main/resources/application-docker.yaml`), que lee credenciales desde variables de entorno y permite cookies seguras en `false` para trabajar sobre HTTP plano.

### Variables de entorno

Tomá como base el template:

```bash
cp .env.example .env
```

Editá `.env` y ajustá al menos:

| Variable               | Descripción                                                                                   |
|:-----------------------|:----------------------------------------------------------------------------------------------|
| `SPRING_PROFILES_ACTIVE` | Dejá `docker` para compose. Cambiá a `prod` solo cuando ejecutes la imagen final.          |
| `DB_USER`              | Usuario de PostgreSQL dentro del contenedor.                                                  |
| `DB_PASSWORD`          | Contraseña de PostgreSQL. Generá una con `openssl rand -base64 24`.                            |
| `DB_NAME`              | Nombre de la base (por defecto `tienda_uni`).                                                 |
| `JWT_SECRET`           | Secreto de firma JWT. Largo, aleatorio. `openssl rand -base64 48`.                            |
| `FRONTEND_URL`         | Origen permitido por CORS (ej. `http://localhost:3000`).                                      |
| `ERROR_URL`            | URL a la que el backend redirige ante errores de autenticación.                                |

`DB_HOST` y `DB_PORT` están fijados en el compose (`postgres` y `5432` respectivamente) y no requieren edición.

### Levantar los contenedores

Desde la raíz del repositorio:

```bash
docker compose -f docker-compose.dev.yaml --env-file .env up --build -d
```

- `--build` reconstruye la imagen del backend la primera vez (o cuando cambia `pom.xml` / código fuente).
- `-d` corre en segundo plano.

Para seguir los logs en vivo:

```bash
docker compose -f docker-compose.dev.yaml logs -f backend
docker compose -f docker-compose.dev.yaml logs -f postgres
```

### Acceso a los servicios

| Servicio        | URL / Comando                                              |
|:----------------|:------------------------------------------------------------|
| API REST        | `http://localhost:8080`                                     |
| Health / liveness | `http://localhost:8080/actuator/health`                   |
| PostgreSQL host | `localhost:5435` (usuario/contraseña de `.env`, db `tienda_uni`) |

Conexión rápida a la base desde el host:

```bash
psql -h localhost -p 5435 -U "$DB_USER" -d tienda_uni
```

### Carga inicial de la base de datos

El servicio `postgres` monta [`src/main/resources/sql`](src/main/resources/sql) en
`/docker-entrypoint-initdb.d:ro`. Los archivos se ejecutan **alfabéticamente** la primera vez que se inicializa el volumen
nombrado `postgres_data`:

```
01_tienda_uni_schema.sql
02_roles_and_permissions.sql
03_universities.sql
```

Solo corren cuando el volumen está vacío. Si cambiás los scripts y querés re-ejecutarlos, [reseteá el entorno](#resetear-el-entorno).

### Comandos útiles

```bash
# Estado de los servicios
docker compose -f docker-compose.dev.yaml ps

# Entrar al contenedor del backend
docker compose -f docker-compose.dev.yaml exec backend sh

# Entrar al postgres (psql ya disponible)
docker compose -f docker-compose.dev.yaml exec postgres psql -U "$DB_USER" -d tienda_uni

# Reconstruir solo el backend tras un cambio de código
docker compose -f docker-compose.dev.yaml up --build -d backend

# Bajar los contenedores (conservando el volumen)
docker compose -f docker-compose.dev.yaml down
```

### Resetear el entorno

Para volver a correr los scripts de inicialización:

```bash
docker compose -f docker-compose.dev.yaml down -v
docker compose -f docker-compose.dev.yaml --env-file .env up --build -d
```

`-v` borra el volumen `postgres_data`. **Cuidado**: esto destruye todos los datos locales.

---

## Imagen para producción

El [`Dockerfile`](Dockerfile) es **multi-stage**:

1. **Stage `builder`** (`maven:3-eclipse-temurin-21-alpine`): descarga dependencias offline y compila el JAR.
2. **Stage runtime** (`eclipse-temurin:21-jre-alpine`): copia el JAR final y lo ejecuta con `java -jar`. No incluye Maven ni el JDK completo.

Build result: una imagen liviana basada en JRE Alpine lista para ejecutarse con el perfil `prod`.

### Construir la imagen

```bash
docker build -t tiendauni/api:0.0.1 .
```

- `tiendauni/api` es el namespace/nombre sugerido; ajustalo a tu registry (`ghcr.io/tu-org/tiendauni-api`, `tu-usuario/tiendauni-api`, etc.).
- `0.0.1` debe coincidir con la versión declarada en `pom.xml`.

Para etiquetar también `latest`:

```bash
docker build -t tiendauni/api:0.0.1 -t tiendauni/api:latest .
```

### Ejecutar la imagen en local

Necesitás un PostgreSQL accesible (RDS, Cloud SQL, instancia autoadministrada, etc.) y todas las variables de `application-prod.yaml`. Ejemplo mínimo:

```bash
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=db.tu-host.example \
  -e DB_PORT=5432 \
  -e DB_NAME=tienda_uni \
  -e DB_USER="$DB_USER" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  -e JWT_SECRET="$JWT_SECRET" \
  -e FRONTEND_URL="https://tiendauni.example" \
  -e ERROR_URL="https://tiendauni.example/errors" \
  tiendauni/api:0.0.1
```

> En producción el esquema debe estar migrado por un mecanismo externo (Flyway / Liquibase / migración SQL controlada). La imagen **no** ejecuta scripts de inicialización automáticamente.

### Publicar en un registry

```bash
docker login                            # una vez por registry
docker tag tiendauni/api:0.0.1 ghcr.io/tu-org/tiendauni-api:0.0.1
docker push ghcr.io/tu-org/tiendauni-api:0.0.1
```

### Checklist de despliegue

Antes de promover una imagen a producción, verificá:

- [ ] `SPRING_PROFILES_ACTIVE=prod` en el entorno destino.
- [ ] `JWT_SECRET` con entropía suficiente (≥ 48 bytes aleatorios).
- [ ] `DB_*` apuntando al servicio gestionado y la base con el esquema migrado.
- [ ] `FRONTEND_URL` y `ERROR_URL` con los orígenes HTTPS reales.
- [ ] El puerto `8080` (o el que definas con `SERVER_PORT`) expuesto solo hacia el balanceador interno.
- [ ] El `healthcheck` configurado contra `/actuator/health`.
- [ ] Logs centralizados accesibles (la aplicación escribe a `stdout`/`stderr`, capturá desde el orquestador).
- [ ] Escaneo de vulnerabilidades de la imagen previo al despliegue (Trivy, Docker Scout, Snyk, etc.).

---

## Solución de problemas

| Síntoma                                                | Causa probable                                                | Solución                                                                                  |
|:-------------------------------------------------------|:--------------------------------------------------------------|:------------------------------------------------------------------------------------------|
| `backend` queda reiniciando y `pg_isready` falla       | Credenciales de `.env` no coinciden con `postgres`             | Verificá `DB_USER` / `DB_PASSWORD`; reventá el volumen si lo cambiaste.                   |
| Los scripts SQL no se ejecutan                         | El volumen `postgres_data` ya estaba inicializado              | `docker compose down -v` y volver a `up`.                                                 |
| `413 Request Entity Too Large` o error de CORS         | `FRONTEND_URL` mal configurada                                | Ajustá `FRONTEND_URL` con el origen exacto (esquema + host + puerto).                     |
| Cambios en código no se reflejan                       | Imagen cacheada                                               | `docker compose -f docker-compose.dev.yaml up --build -d backend`.                        |
| Cambios en `pom.xml` no se reflejan                    | La cache de `dependency:go-offline` quedó obsoleta             | `docker compose -f docker-compose.dev.yaml build --no-cache backend` y volver a `up`.     |
| Puerto `5435` ocupado en el host                       | Otra instancia local de Postgres usa `5435`                   | Cambiá `"5435:5432"` por otro puerto host en `docker-compose.dev.yaml`.                   |
