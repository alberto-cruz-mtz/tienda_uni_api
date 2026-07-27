# Universidad

Estos endpoints están diseñados para proporcionar información sobre universidades disponibles. A continuación, se
detallan los endpoints disponibles:

## Obtener universidades disponibles o registradas

**Ruta:** `/api/universities`
**Método:** `GET`

**Descripción:** Este endpoint devuelve una lista de todas las universidades disponibles o registradas en el sistema

**Parámetros de consulta:** Ninguno

**Headers requeridos:**

- `Cookie`: `accessToken=<jwt>` Debe incluir la cookie de sesión para autenticar la solicitud.
- `Content-Type`: `application/json`

**Ejemplo de solicitud:**

```http request
GET /api/universities HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

### Respuesta exitosa

**Status:** `200 OK`

```json
[
  {
    "id": "77d3c6af-dff1-4ab1-87a3-4730581e5637",
    "acronym": "IPN",
    "name": "Universidad Teconlógica del Sureste de Veracruz",
    "state": "Veracruz"
  },
  {
    "id": "77d3c6af-dff1-4ab1-87a3-4730581e5638",
    "acronym": "UNAM",
    "name": "Universidad Nacional Autónoma de México",
    "state": "Ciudad de México"
  },
  {
    "id": "77d3c6af-dff1-4ab1-87a3-4730581e5639",
    "acronym": "UPN",
    "name": "Instituto Politécnico Nacional",
    "state": "Ciudad de México"
  }
]
```

> **Nota:** La respuesta es un arreglo de objetos, pero para el MVP solo abra una universidad registrada, por lo que la
> respuesta será un arreglo con un solo objeto.

### Respuestas de error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### No autenticado

**Status:** `401 Unauthorized`

```json
{
  "type": "https://example.com/probs/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "El token de autenticación es inválido o ha expirado.",
  "instance": "/api/universities"
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
  "instance": "/api/universities",
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
  "instance": "/api/universities"
}
```
