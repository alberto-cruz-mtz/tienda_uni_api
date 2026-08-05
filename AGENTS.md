# TiendaUniApi — Guía para agentes

Convenciones, reglas y políticas acordadas para el backend de TiendaUni. Antes de editar el código, leé este documento.

> **Convención de idioma**: código, identificadores y nombres de paquetes en **inglés**; documentación y mensajes de
> cara al usuario en **español**.

---

## 1. Reglas no negociables

| Área        | Regla                                                                                                                                                                                                                                                                  |
|-------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Commits     | Conventional Commits con scope en inglés (`feat(auth): ...`); **nunca** incluir `Co-Authored-By` ni firmas de IA; agregar mensajes claros y descriptivos; Debe incluir Body si son varios los cambios hechos o necesita explicación mas extensa que la de Description. |
| Mensajes    | Presente y concisos. Sin mayúsculas sostenidas, sin emojis.                                                                                                                                                                                                            |
| Excepciones | Dominio tipado en `service/exception/`. **Nunca** `throw new RuntimeException(...)`.                                                                                                                                                                                   |
| Secretos    | Siempre por variable de entorno. **Nunca** commitear `.env`, claves ni tokens reales.                                                                                                                                                                                  |
| SQL         | Archivos de inicialización con **prefijo numérico** (`01_schema.sql`, `02_roles.sql`...).                                                                                                                                                                              |
| Inyección   | Por constructor. Sin `@Autowired` en campos.                                                                                                                                                                                                                           |
| DTOs        | Records inmutibles. Sin setters públicos.                                                                                                                                                                                                                              |
| Logs        | Sin secretos, sin emails completos, sin contraseñas.                                                                                                                                                                                                                   |

---

## 2. Estructura de paquetes

Cada feature sigue una variante de **screaming architecture**. Para una feature nueva, usá este esqueleto:

```
src/main/java/tienda/uni/api/<feature>/
├── presentation/
│   ├── controller/      # @RestController
│   ├── dto/             # Records de entrada/salida
│   └── advice/          # @RestControllerAdvice (feature-local)
├── persistence/
│   ├── entity/          # @Entity
│   ├── model/           # Enums y tipos de dominio
│   └── repository/      # Spring Data JPA
├── service/
│   ├── interfaces/      # Contratos
│   ├── implementation/  # Lógica
│   └── exception/       # Excepciones de dominio
├── configuration/       # @Configuration feature-local
└── util/                # Helpers puros
```

Lo **transversal** vive en `app/{advice,configuration,dto}`:

- `app/configuration/security/` → `SecurityConfiguration`, filtros, JWT.
- `app/advice/` → handlers globales (no feature-locales).

---

## 3. Spring Security y roles

- **Roles**: usar siempre `Role.authority()` (incluye el prefijo `ROLE_`) en `hasAuthority()`, `hasRole()` y
  `@PreAuthorize`. **Nunca** `Role.name()`.
- **Identidad**: el `GrantedAuthority` se construye desde `Role.authority()` en el `UserDetails`.
- **Auth endpoints**:
    - `/auth/login` y `/auth/refresh` son los **únicos** que emiten access tokens.
    - Endpoints de **status** (ej. `/auth/verify-email-status`) devuelven booleanos o códigos, **nunca** un token nuevo.
- **Refresh tokens**: rotativos, en cookie `HttpOnly` + `Secure` (en `prod` y `docker`).

```java
@PreAuthorize("hasAuthority(Role.ADMIN.authority())")   // OK
@PreAuthorize("hasAuthority(Role.ADMIN.name())")        // NUNCA — nunca matchea
```

---

## 4. JPA / persistencia

- **Colecciones manejadas por Hibernate son mutables**. Agregar con `getRoles().add(item)`. **Nunca** reemplazar con
  `Set.of(...)` / `List.of(...)` / `Collections.unmodifiableXxx(...)`.
- **Entidades** con IDs de negocio en `UUID` (`@GeneratedValue` con `UUID.randomUUID()` o asignados por la app).
  `BIGSERIAL`/`SERIAL` solo para tags y logs.
- **Enums** en Postgres como `CREATE TYPE`. Mapeo con `@Enumerated(EnumType.STRING)` o convertidor custom.
- **Validación** siempre en dos puntos: DTO (Jakarta Bean Validation) + constraint `CHECK` en BD cuando aplique.
- **Transacciones** en la capa de servicio (`@Transactional`). Repository sólo lectura.

---

## 5. Mail / SMTP

- **Perfiles**: `base` (defaults), `dev`, `docker`, `prod`. Cada perfil tiene su `application-*.yaml`.
- **Variables** con sintaxis `${VAR:default}`:
    - `dev`: defaults de Gmail (puerto 587, starttls).
    - `prod`: **fail-fast** — sin defaults, un valor faltante debe romper el arranque.
    - `docker`: defaults razonables para el compose.
- **Record `MailProperties`** consume `spring.mail.smtp.*` y `spring.mail.debug` directamente. No usar
  `spring.mail.properties.mail.smtp.*`.
- **Envío asíncrono** vía `ThreadPoolTaskExecutor` (`@Async("emailExecutor")`).

---

## 6. Docker / base de datos

- **Imagen**: multi-stage, artefacto **standalone JRE jar** (no WAR con Tomcat externo).
- **Compose dev**: `docker-compose.dev.yaml` + `Dockerfile`. Levanta Postgres + backend.
- **Init SQL**: archivos en `src/main/resources/sql/` con **prefijo numérico**, montados en
  `/docker-entrypoint-initdb.d:ro`. Postgres los ejecuta en orden alfabético en la primera inicialización.
- **Perfil `docker`**: distinto de `prod`. Permite `cookie.secure=false` y CORS abierto para host-local. No reusar
  `prod` (fuerza HTTPS).

```bash
# Levantar entorno dev
docker compose -f docker-compose.dev.yaml --env-file .env.example up -d
```

---

## 7. Variables de entorno

Plantilla en `.env.example`. Valores reales en `.env` (ignorado por git).

| Variable                                                                           | Uso                                                                  |
|------------------------------------------------------------------------------------|----------------------------------------------------------------------|
| `SPRING_PROFILES_ACTIVE`                                                           | `dev`, `docker` o `prod`.                                            |
| `DB_HOST` / `DB_PORT` / `DB_NAME` / `DB_USER` / `DB_PASSWORD`                      | Conexión Postgres.                                                   |
| `JWT_SECRET`                                                                       | Firma de tokens. Mínimo 48 bytes random (`openssl rand -base64 48`). |
| `EMAIL_HOST` / `EMAIL_PORT` / `EMAIL_USERNAME` / `EMAIL_PASSWORD` / `EMAIL_SENDER` | SMTP.                                                                |
| `FRONTEND_URL` / `ERROR_URL`                                                       | Redirects del flujo de auth.                                         |

---

## 8. Commits y PRs

```text
feat(auth): add password reset endpoint
fix(mail): send verification email asynchronously
chore(docker): prefix init SQL files
docs(readme): link DOCKER.md
```

- Una unidad de trabajo por commit.
- Scope = carpeta o área (`auth`, `mail`, `docker`, `config`, `security`, `docs`).
- Antes de stagear, revisar `git status` y `git diff` — sin cambios colaterales.
- PRs con descripción breve: qué cambia, por qué, cómo verificarlo.

---

## 9. Build y verificación

```bash
./mvnw clean verify          # Compila, corre tests, genera REST Docs
./mvnw -DskipTests package   # Solo artefacto
./mvnw spring-boot:run       # Levanta en localhost:8080
```

> `clean verify` requiere Postgres en `localhost:5432`. Para tests sin BD local, aislar o mockear.

---

## 10. Checklist antes de un PR

- [ ] ¿El cambio sigue la estructura de paquetes de la sección 2?
- [ ] ¿Las excepciones nuevas son de dominio (no `RuntimeException`)?
- [ ] ¿Los chequeos de rol usan `authority()`?
- [ ] ¿Las colecciones JPA se mutan con `add()`?
- [ ] ¿Se respetó la convención de env vars (Gmail solo en `dev`, fail-fast en `prod`)?
- [ ] ¿No hay secretos, `.env` ni credenciales en el diff?
- [ ] ¿Los commits son Conventional Commits sin atribución de IA?
- [ ] ¿El README y `DOCKER.md` siguen siendo consistentes con los cambios?
