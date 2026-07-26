# API Authentication

Autenticación es requerida para acceder à la API. Puedes autenticarte usando un token.

## Autenticación

Endpoint usado para autenticar a un usuario y obtener un token de acceso y refresco.

**Ruta:** `/api/auth/login`
**Método:** `POST`

### Parámetros de la petición

| Campo      | Tipo     | Descripción                     |
|:-----------|:---------|:--------------------------------|
| `email`    | `string` | Correo electrónico del usuario. |
| `password` | `string` | Contraseña en texto plano.      |

### Reglas de validación

#### `email`

- No puede ser `null` ni una cadena vacía.
- Debe tener un formato de correo electrónico válido (`local-part@domain`).

#### `password`

- No puede ser `null` ni una cadena vacía.
- Debe tener entre **8 y 25 caracteres**.
- Debe contener al menos:
    - Una letra **minúscula** (`a-z`).
    - Una letra **mayúscula** (`A-Z`).
    - Un **dígito numérico** (`0-9`).
    - Un **símbolo** (carácter especial no alfanumérico, por ejemplo `!@#$%^&*`).

**Ejemplo de Petición:**

```json
{
  "email": "usuario@alumnos.uni.edu.mx",
  "password": "miPasswordSeguro123"
}
```

### Respuesta Exitosa

**Status:** 200 OK

```json
{
  "id": "77d3c6af-dff1-4ab1-87a3-4730581e5638",
  "user": {
    "name": "emir polito guevara",
    "avatarUrl": "https://avatar.png"
  },
  "expiresAt": "2026-07-25T20:25:18Z"
}
```

#### Cookies de sesión

La respuesta exitosa incluye las cookies de autenticación en el encabezado `Set-Cookie`. **No** se exponen en el cuerpo
de la respuesta.

#### `accessToken`

Cookie con el token de acceso utilizado para autenticar las peticiones a la API.

| Atributo   | Valor                  |
|:-----------|:-----------------------|
| `HttpOnly` | `true`                 |
| `SameSite` | `Strict`               |
| `Path`     | `/`                    |
| `Max-Age`  | Igual a `expiresAt`    |
| `Secure`   | `true` (en producción) |

#### `refreshToken`

Cookie con el token utilizado para renovar el `accessToken` cuando expira.

| Atributo   | Valor                  |
|:-----------|:-----------------------|
| `HttpOnly` | `true`                 |
| `SameSite` | `Strict`               |
| `Path`     | `/api/auth/refresh`    |
| `Max-Age`  | 7 días                 |
| `Secure`   | `true` (en producción) |

**Ejemplo de encabezado `Set-Cookie`:**

```http
Set-Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; HttpOnly; SameSite=Strict; Secure; Max-Age=900
Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/api/auth/refresh; HttpOnly; SameSite=Strict; Secure; Max-Age=604800
```

> Por seguridad, las cookies **no son accesibles desde JavaScript** (`HttpOnly`) y solo se envían en peticiones del
> mismo sitio (`SameSite=Strict`), mitigando ataques XSS y CSRF.

### Respuesta de Error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Credenciales inválidas

**Status:** 401 Unauthorized

```json
{
  "type": "https://example.com/errors/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "Las credenciales proporcionadas no son válidas.",
  "instance": "/api/auth"
}
```

#### Datos de entrada inválidos

**Status:** 400 Bad Request

```json
{
  "type": "https://example.com/errors/validation",
  "title": "Validation Error",
  "status": 400,
  "detail": "Uno o más campos no cumplen con las reglas de validación.",
  "instance": "/api/auth",
  "errors": [
    {
      "field": "email",
      "message": "El formato del correo electrónico es inválido."
    },
    {
      "field": "password",
      "message": "La contraseña es obligatoria."
    }
  ]
}
```

#### Demasiadas solicitudes

**Status:** 429 Too Many Requests

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de intentos de autenticación. Inténtalo de nuevo más tarde.",
  "instance": "/api/auth",
  "retryAfter": 60
}
```

#### Correo electrónico no registrado

**Status:** 404 Not Found

```json
{
  "type": "https://example.com/errors/email-not-registered",
  "title": "Email Not Registered",
  "status": 404,
  "detail": "El correo electrónico proporcionado no está registrado.",
  "instance": "/api/auth"
}
```

#### Error interno del servidor

**Status:** 500 Internal Server Error

```json
{
  "type": "https://example.com/errors/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Ocurrió un error inesperado en el servidor. Por favor, inténtalo de nuevo más tarde.",
  "instance": "/api/auth"
}
```

## Registro

Endpoint usado para registrar a un nuevo usuario y obtener un token de acceso y refresco.

**Ruta:** `/api/auth/signup`
**Método:** `POST`

### Parámetros de la petición

| Campo       | Tipo     | Descripción                     |
|:------------|:---------|:--------------------------------|
| `firstName` | `string` | Nombre completo del usuario.    |
| `lastName`  | `string` | Apellido del usuario.           |
| `email`     | `string` | Correo electrónico del usuario. |
| `password`  | `string` | Contraseña en texto plano.      |

### Reglas de validación

- `firstName` y `lastName` deben tener entre 1 y 60 caracteres.
- `email` debe ser un formato de correo electrónico válido, no debe estar vacío.
- `password` debe tener entre 8 y 25 caracteres, debe contener (caracteres especiales, minúsculas, mayúsculas y números)
  y no debe estar vacío.

**Ejemplo de peticion:**

```json
{
  "firstName": "Emir",
  "lastName": "Polito Guevara",
  "email": "emir.polito@alumnos.uni.edu.mx",
  "password": "SecurePass123!",
  "universityId": "77d3c6af-dff1-4ab1-87a3-4730581e5638"
}
```

### Respuesta Exitosa

**Status:** 201 Created

```json
{
  "message": "Usuario registrado correctamente. Por favor, verifica tu correo electrónico.",
  "user": {
    "id": "77d3c6af-dff1-4ab1-87a3-4730581e5638",
    "email": "emir.polito@alumnos.uni.edu.mx"
  }
}
```

### Respuestas de Error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Correo electrónico ya registrado

**Status:** 409 Conflict

```json
{
  "type": "https://example.com/errors/email-already-registered",
  "title": "Email Already Registered",
  "status": 409,
  "detail": "El correo electrónico proporcionado ya está registrado.",
  "instance": "/api/auth/signup"
}
```

#### Dominio de correo electrónico no permitido

**Status:** 403 Forbidden

```json
{
  "type": "https://example.com/errors/email-domain-not-allowed",
  "title": "Email Domain Not Allowed",
  "status": 403,
  "detail": "El dominio del correo electrónico proporcionado no está permitido.",
  "instance": "/api/auth/signup"
}
```

#### Universidad no encontrada

**Status:** 404 Not Found

```json
{
  "type": "https://example.com/errors/university-not-found",
  "title": "University Not Found",
  "status": 404,
  "detail": "La universidad proporcionada no fue encontrada.",
  "instance": "/api/auth/signup"
}
```

#### Datos de entrada inválidos

**Status:** 400 Bad Request

```json
{
  "type": "https://example.com/errors/validation",
  "title": "Validation Error",
  "status": 400,
  "detail": "Uno o más campos no cumplen con las reglas de validación.",
  "instance": "/api/auth/signup",
  "errors": [
    {
      "field": "email",
      "message": "El formato del correo electrónico es inválido."
    },
    {
      "field": "password",
      "message": "La contraseña es obligatoria."
    }
  ]
}
```

#### Demasiadas solicitudes

**Status:** 429 Too Many Requests

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de intentos de registro. Inténtalo de nuevo más tarde.",
  "instance": "/api/auth/signup",
  "retryAfter": 60
}
```

#### Error interno del servidor

**Status:** 500 Internal Server Error

```json
{
  "type": "https://example.com/errors/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Ocurrió un error inesperado en el servidor. Por favor, inténtalo de nuevo más tarde.",
  "instance": "/api/auth/signup"
}
```

## Refrescar Token

Endpoint usado para renovar el `accessToken` cuando expira, utilizando el `refreshToken`.

**Ruta:** `/api/auth/refresh`
**Método:** `POST`

### Envío de cookies

Este endpoint no recibe parámetros en el cuerpo de la petición. El `refreshToken` se envía automáticamente como una
cookie HttpOnly con `Path=/api/auth/refresh`, establecida previamente por `/api/auth/login`.

El navegador envía la cookie en el encabezado `Cookie` de la petición. El servidor la lee para validar el token y emitir
un nuevo `accessToken`.

**Atributos esperados de la cookie `refreshToken`:**

| Atributo   | Valor                  |
|:-----------|:-----------------------|
| `HttpOnly` | `true`                 |
| `SameSite` | `Strict`               |
| `Path`     | `/api/auth/refresh`    |
| `Secure`   | `true` (en producción) |

**Ejemplo de petición:**

```http
POST /api/auth/refresh HTTP/1.1
Host: api.tiendauni.com
Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Respuesta Exitosa

**Status:** 200 OK

La respuesta incluye un nuevo `accessToken` en el encabezado `Set-Cookie`. El `refreshToken` se rota (se emite uno
nuevo)
y se reemplaza la cookie existente.

```http
HTTP/1.1 200 OK
Set-Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; HttpOnly; SameSite=Strict; Secure; Max-Age=900
Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/api/auth/refresh; HttpOnly; SameSite=Strict; Secure; Max-Age=604800
```

```json
{
  "expiresAt": "2026-07-25T20:25:18Z"
}
```

### Respuestas de Error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Refresh token ausente

**Status:** 401 Unauthorized

```json
{
  "type": "https://example.com/errors/refresh-token-missing",
  "title": "Refresh Token Missing",
  "status": 401,
  "detail": "No se proporcionó el refresh token. Asegúrate de que la cookie esté presente.",
  "instance": "/api/auth/refresh"
}
```

#### Refresh token inválido o expirado

**Status:** 401 Unauthorized

```json
{
  "type": "https://example.com/errors/refresh-token-invalid",
  "title": "Refresh Token Invalid",
  "status": 401,
  "detail": "El refresh token es inválido, expiró o ya fue utilizado. Vuelve a iniciar sesión.",
  "instance": "/api/auth/refresh"
}
```

#### CSRF token inválido

**Status:** 403 Forbidden

```json
{
  "type": "https://example.com/errors/csrf-invalid",
  "title": "CSRF Token Invalid",
  "status": 403,
  "detail": "El token CSRF no es válido o está ausente.",
  "instance": "/api/auth/refresh"
}
```

#### Demasiadas solicitudes

**Status:** 429 Too Many Requests

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de refrescos. Inténtalo de nuevo más tarde.",
  "instance": "/api/auth/refresh",
  "retryAfter": 60
}
```

#### Error interno del servidor

**Status:** 500 Internal Server Error

```json
{
  "type": "https://example.com/errors/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Ocurrió un error inesperado en el servidor. Por favor, inténtalo de nuevo más tarde.",
  "instance": "/api/auth/refresh"
}
```