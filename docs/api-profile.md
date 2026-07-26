# Perfil de Usuario

Este endpoint permite obtener y actualizar la información del perfil del usuario.

## Obtener perfil de usuario

**Ruta:** `/api/profiles`
**Método:** `GET`

**Descripción:** Este endpoint devuelve la información del perfil del usuario autenticado.

**Headers requeridos:**

- `Authorization`: Token de autenticación Bearer.
- `Content-Type`: `application/json`
- **Parámetros de consulta:** Ninguno

**Ejemplo de petición:**

```http
GET /api/profiles HTTP/1.1
Host: api.tiendauni.com
Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Respuesta exitosa

**Status:** `200 OK`

```json
{
  "id": "77d3c6af-dff1-4ab1-87a3-4730581e5638",
  "avatarUrl": "https://example.com/avatar.jpg",
  "name": "John Doe"
}
```

### Respuestas de error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Perfil no encontrado

**Status:** `404 Not Found`

```json
{
  "type": "https://example.com/errors/profile-not-found",
  "title": "Perfil no encontrado",
  "status": 404,
  "detail": "No se encontró el perfil del usuario autenticado.",
  "instance": "/api/profile"
}
```

#### Error de autenticación

**Status:** `401 Unauthorized`

```json
{
  "type": "https://example.com/errors/unauthorized",
  "title": "No autorizado",
  "status": 401,
  "detail": "El token de autenticación es inválido o ha expirado.",
  "instance": "/api/profile"
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
  "instance": "/api/profile",
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
  "instance": "/api/profile"
}
```

## Actualizar avatar/foto del perfil de usuario

**Ruta:** `/api/profiles/avatar`
**Método:** `PATCH`

**Descripción:** Este endpoint permite actualizar el avatar/foto del perfil del usuario autenticado.

**Headers requeridos:**

- `Authorization`: Token de autenticación Bearer.
- `Content-Type`: `application/json`

### Parámetros de la petición

| Campo       | Tipo     | Descripción                 |
|:------------|:---------|:----------------------------|
| `avatarUrl` | `string` | URL del avatar del usuario. |

### Reglas de validación

#### `avatarUrl`

- Debe ser una URL válida.
- Debe ser una URL que apunte a un recurso accesible públicamente (por ejemplo, una imagen en Cloudflare R2).
- Debe ser una URL que apunte a un recurso que cumpla con los requisitos de tamaño y formato de imagen (por ejemplo,
  JPEG, PNG, etc.).
- No debe ser una URL que apunte a un recurso que requiera autenticación para acceder.

**Ejemplo de petición:**

```http
PATCH /api/profiles/avatar HTTP/1.1
Host: api.tiendauni.com
Cookie: accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Nota Arquitectónica: ¿Por qué este endpoint recibe una URL y no un Multipart/File?

Este endpoint (`PATCH /api/profile/avatar`) está diseñado para recibir un JSON con la URL final de la imagen en lugar de
procesar el archivo físico mediante `multipart/form-data`.

Esta decisión se basa en el patrón de **Direct-to-Cloud Uploads** (Subidas directas a la nube) utilizando URLs
Prefirmadas (Pre-signed URLs) hacia Cloudflare R2. Implementamos esta arquitectura por las siguientes razones:

1. **Rendimiento y Baja Latencia:** Evitamos el "doble salto" (Cliente ➔ Backend ➔ Cloudflare). Al subir el archivo
   directamente desde el cliente a Cloudflare R2, aprovechamos su red global (CDN) para una carga mucho más rápida.
2. **Escalabilidad y Estabilidad del Servidor:** Evitamos que nuestro servidor principal actúe como un "cuello de
   botella" de red y memoria. El backend no tiene que lidiar con el streaming de bytes de múltiples usuarios subiendo
   fotos simultáneamente.
3. **Desacoplamiento:** Separamos la responsabilidad de "almacenamiento de archivos" de la "actualización de datos del
   perfil", manteniendo nuestra API ligera y rápida.

### Respuesta Exitosa

**Status:** `204 No Content`

```json
{}
```

### Respuestas de error

#### Datos de entrada inválidos

**Status:** `400 Bad Request`

```json
{
  "type": "https://example.com/errors/invalid-avatar-url",
  "title": "URL de avatar inválida",
  "status": 400,
  "detail": "La URL proporcionada para el avatar no es válida o no cumple con los requisitos.",
  "instance": "/api/profiles/avatar",
  "errors": [
    {
      "field": "avatarUrl",
      "message": "La URL debe ser una URL válida y accesible públicamente."
    }
  ]
}
```

#### Error de autenticación

**Status:** `401 Unauthorized`

```json
{
  "type": "https://example.com/errors/unauthorized",
  "title": "No autorizado",
  "status": 401,
  "detail": "El token de autenticación es inválido o ha expirado.",
  "instance": "/api/profiles/avatar"
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
  "instance": "/api/profiles/avatar",
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
  "instance": "/api/profiles/avatar"
}
```