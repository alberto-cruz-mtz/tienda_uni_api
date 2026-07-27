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
    "avatarUrl": "https://avatar.png",
    "building": "Biblioteca"
  },
  "isVerified": true,
  "expiresAt": "2026-07-25T20:25:18Z"
}
```

```http
HTTP/1.1 200 OK
Set-Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; HttpOnly; SameSite=Strict; Secure; Max-Age=900
Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/api/auth/refresh; HttpOnly; SameSite=Strict; Secure; Max-Age=604800
```

#### Cookies de sesión

La respuesta exitosa incluye las cookies de autenticación en el encabezado `Set-Cookie`. **No** se exponen en el cuerpo
de la respuesta.

#### `accessToken`

Cookie con el token de acceso utilizado para autenticar las peticiones a la API.

| Atributo   | Valor                            |
|:-----------|:---------------------------------|
| `HttpOnly` | `true`                           |
| `SameSite` | `Strict`                         |
| `Path`     | `/`                              |
| `Max-Age`  | 15 Minutos (Igual a `expiresAt`) |
| `Secure`   | `true` (en producción)           |

#### `refreshToken`

Cookie con el token utilizado para renovar el `accessToken` cuando expira.

| Atributo   | Valor                  |
|:-----------|:-----------------------|
| `HttpOnly` | `true`                 |
| `SameSite` | `Strict`               |
| `Path`     | `/api/auth/refresh`    |
| `Max-Age`  | 7 días                 |
| `Secure`   | `true` (en producción) |

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

**Ejemplo de petición:**

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
    "name": "Emir Polito Guevara",
    "email": "emir.polito@alumnos.uni.edu.mx"
  },
  "isVerified": false,
  "expiresAt": "2026-07-25T20:25:18Z"
}
```

```http
HTTP/1.1 201 Created
Set-Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; HttpOnly; SameSite=Strict; Secure; Max-Age=900
Set-Cookie: refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/api/auth/refresh; HttpOnly; SameSite=Strict; Secure; Max-Age=604800
```

#### Cookies de sesión

La respuesta exitosa incluye las mismas cookies de autenticación (`accessToken` y `refreshToken`) que
`POST /api/auth/login`. Consulta la sección de [Autenticación](#autenticación) para conocer sus atributos completos.
**No** se exponen en el cuerpo de la respuesta.

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

## Autorizar correo electrónico

Endpoint usado para autorizar el correo electrónico de un usuario registrado. Al registrarse, el sistema envía un correo
electrónico con un enlace de verificación; el usuario debe abrir dicho enlace y la aplicación cliente envía el
`token` resultante a este endpoint para activar la cuenta.

**Ruta:** `/api/auth/verify-email`
**Método:** `POST`

### Parámetros de la petición

| Campo   | Tipo     | Descripción                                                                                                                    |
|:--------|:---------|:-------------------------------------------------------------------------------------------------------------------------------|
| `token` | `string` | Token de verificación recibido en el enlace del correo electrónico (por ejemplo `https://app.tiendauni.com/verify?token=...`). |

### Reglas de validación

#### `token`

- No puede ser `null` ni una cadena vacía.
- Debe tener un formato válido: solo caracteres alfanuméricos y guiones (`-` o `_`), con una longitud de entre **36 y
  255 caracteres**.
- No debe haber sido utilizado previamente (los tokens son de un solo uso).
- No debe estar expirado (TTL recomendado: **24 horas** desde su emisión).

**Ejemplo de petición:**

```json
{
  "token": "4f1c2e9a-7b3d-4d8e-b6a1-9c2f0d4e5a3b"
}
```

### Respuesta Exitosa

**Status:** 204 No Content (sin cuerpo)

> Si el usuario no verifica su correo electrónico dentro del tiempo de vida del token, deberá solicitar un nuevo token
> de verificación y
> autenticarse con sus credenciales mediante `POST /api/auth/login`. Si el usuario no lo hace, su cuenta permanecerá
> inactiva y no podrá acceder a la API aunque posea su token de acceso

### Respuestas de Error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Token ausente o inválido

**Status:** 400 Bad Request

```json
{
  "type": "https://example.com/errors/validation",
  "title": "Validation Error",
  "status": 400,
  "detail": "Uno o más campos no cumplen con las reglas de validación.",
  "instance": "/api/auth/verify-email",
  "errors": [
    {
      "field": "token",
      "message": "El token es obligatorio."
    }
  ]
}
```

#### Token expirado

**Status:** 410 Gone

```json
{
  "type": "https://example.com/errors/verification-token-expired",
  "title": "Verification Token Expired",
  "status": 410,
  "detail": "El token de verificación ha expirado. Solicita uno nuevo para activar tu cuenta.",
  "instance": "/api/auth/verify-email"
}
```

#### Token ya utilizado

**Status:** 409 Conflict

```json
{
  "type": "https://example.com/errors/verification-token-already-used",
  "title": "Verification Token Already Used",
  "status": 409,
  "detail": "El token de verificación ya fue utilizado. Si tu cuenta no está activa, solicita un nuevo correo de verificación.",
  "instance": "/api/auth/verify-email"
}
```

#### Token no encontrado

**Status:** 404 Not Found

```json
{
  "type": "https://example.com/errors/verification-token-not-found",
  "title": "Verification Token Not Found",
  "status": 404,
  "detail": "El token de verificación no es válido.",
  "instance": "/api/auth/verify-email"
}
```

#### Correo electrónico ya verificado

**Status:** 409 Conflict

```json
{
  "type": "https://example.com/errors/email-already-verified",
  "title": "Email Already Verified",
  "status": 409,
  "detail": "El correo electrónico ya fue verificado anteriormente.",
  "instance": "/api/auth/verify-email"
}
```

#### Demasiadas solicitudes

**Status:** 429 Too Many Requests

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de intentos de verificación. Inténtalo de nuevo más tarde.",
  "instance": "/api/auth/verify-email",
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
  "instance": "/api/auth/verify-email"
}
```

## Solicitar correo de verificación

Endpoint usado para solicitar un nuevo correo de verificación cuando el token original expiró, ya fue utilizado o nunca
fue recibido. El sistema genera un nuevo token (invalidando los anteriores) y envía un correo electrónico con el enlace
de verificación.

**Ruta:** `/api/auth/verify-email/request`
**Método:** `POST`

### Parámetros de la petición

| Campo   | Tipo     | Descripción                                |
|:--------|:---------|:-------------------------------------------|
| `email` | `string` | Correo electrónico del usuario registrado. |

### Reglas de validación

#### `email`

- No puede ser `null` ni una cadena vacía.
- Debe tener un formato de correo electrónico válido (`local-part@domain`).

**Ejemplo de petición:**

```json
{
  "email": "emir.polito@alumnos.uni.edu.mx"
}
```

### Comportamiento anti-enumeración

Por seguridad, este endpoint **siempre devuelve 204 No Content** independientemente de:

- Si el correo electrónico está registrado.
- Si el correo electrónico ya fue verificado.
- Si el dominio del correo no pertenece a una universidad permitida.

Esto previene que un atacante pueda enumerar correos electrónicos registrados analizando las respuestas. El nuevo correo
solo se envía si la cuenta existe, no está verificada y el dominio es permitido; en cualquier otro caso la operación es
un no-op silencioso.

### Respuesta Exitosa

**Status:** 204 No Content (sin cuerpo)

### Respuestas de Error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Datos de entrada inválidos

**Status:** 400 Bad Request

```json
{
  "type": "https://example.com/errors/validation",
  "title": "Validation Error",
  "status": 400,
  "detail": "Uno o más campos no cumplen con las reglas de validación.",
  "instance": "/api/auth/verify-email/request",
  "errors": [
    {
      "field": "email",
      "message": "El formato del correo electrónico es inválido."
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
  "detail": "Has superado el número máximo de solicitudes de verificación. Inténtalo de nuevo más tarde.",
  "instance": "/api/auth/verify-email/request",
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
  "instance": "/api/auth/verify-email/request"
}
```

## Cerrar sesión

Endpoint usado para cerrar la sesión del usuario actual. Invalida el `refreshToken` (impidiendo su reutilización) y
limpia las cookies de autenticación del navegador.

**Ruta:** `/api/auth/logout`
**Método:** `POST`

### Envío de cookies

Este endpoint requiere la cookie `accessToken` establecida por `POST /api/auth/login`. No recibe parámetros en el cuerpo
de la petición.

**Atributos esperados de la cookie `accessToken`:**

| Atributo   | Valor                  |
|:-----------|:-----------------------|
| `HttpOnly` | `true`                 |
| `SameSite` | `Strict`               |
| `Path`     | `/`                    |
| `Secure`   | `true` (en producción) |

**Ejemplo de petición:**

```http
POST /api/auth/logout HTTP/1.1
Host: api.tiendauni.com
Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Comportamiento

- El servidor invalida el `refreshToken` asociado a la sesión, de modo que no podrá ser utilizado en futuras llamadas a
  `POST /api/auth/refresh`.
- La respuesta incluye los encabezados `Set-Cookie` con `Max-Age=0` para limpiar las cookies `accessToken` y
  `refreshToken` en el navegador.
- El endpoint es **idempotente**: si la sesión ya fue invalidada o el `accessToken` ya expiró, devuelve la misma
  respuesta sin error.

### Respuesta Exitosa

**Status:** 204 No Content (sin cuerpo)

```http
HTTP/1.1 204 No Content
Set-Cookie: accessToken=; Path=/; HttpOnly; SameSite=Strict; Secure; Max-Age=0
Set-Cookie: refreshToken=; Path=/api/auth/refresh; HttpOnly; SameSite=Strict; Secure; Max-Age=0
```

### Respuestas de Error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Demasiadas solicitudes

**Status:** 429 Too Many Requests

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de intentos de cierre de sesión. Inténtalo de nuevo más tarde.",
  "instance": "/api/auth/logout",
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
  "instance": "/api/auth/logout"
}
```

## Verificar si el usuario verifico su correo electrónico

**Ruta:** `/api/auth/me`
**Método:** `GET`

**Descripción:** Endpoint usado para verificar si el usuario actual ha verificado su correo electrónico. Devuelve un
nuevo accessToken mediante cookies.

### Envio de cookies

Este endpoint requiere la cookie `accessToken` establecida por `POST /api/auth/login`. No recibe parámetros en el cuerpo
de la petición. La cookie `accessToken` se envía automáticamente en el encabezado `Cookie` de la petición. El servidor
la lee para autenticar al usuario, pero en este endpoint al ser un token de acceso para un usuario recientemente
registrado el token posee un claim (`role`:`ROLE_UNVERFIED`) que solo permite acceso a este endpoint y deniega el acceso
a todos los demás aunque posea su token de acceso.

**Ejemplo de petición:**

```http
GET /api/auth/me HTTP/1.1
Host: api.tiendauni.com
Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Respuesta Exitosa

**Status:** 204 No Content (sin cuerpo)

```http
HTTP/1.1 204 No Content
Set-Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; HttpOnly; SameSite=Strict; Secure; Max-Age=900
```

### Respuestas de Error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Demasiadas solicitudes

**Status:** 429 Too Many Requests

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de intentos de cierre de sesión. Inténtalo de nuevo más tarde.",
  "instance": "/api/auth/me",
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
  "instance": "/api/auth/me"
}
```