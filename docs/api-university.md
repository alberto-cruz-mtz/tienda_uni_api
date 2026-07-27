# API Universidad

Estos endpoints están diseñados para proporcionar información sobre universidades disponibles. A continuación, se
detallan los endpoints disponibles:

## Obtener universidades disponibles o registradas

**Ruta:** `/api/universities`
**Método:** `GET`

**Descripción:** Este endpoint devuelve una lista de todas las universidades disponibles o registradas en el sistema

**Parámetros de consulta:** Ninguno

**Headers requeridos:**

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
    "acronym": "UTSV",
    "name": "Universidad Tecnológica del Sureste de Veracruz",
    "logoUrl": "https://example.com/logos/utsv.png",
    "state": "Veracruz",
    "city": "Nanchital"
  },
  {
    "id": "77d3c6af-dff1-4ab1-87a3-4730581e5638",
    "acronym": "UNAM",
    "name": "Universidad Nacional Autónoma de México",
    "logoUrl": "https://example.com/logos/unam.png",
    "state": "Ciudad de México",
    "city": "Ciudad de México"
  },
  {
    "id": "77d3c6af-dff1-4ab1-87a3-4730581e5639",
    "acronym": "IPN",
    "name": "Instituto Politécnico Nacional",
    "logoUrl": "https://example.com/logos/ipn.png",
    "state": "Ciudad de México",
    "city": "Ciudad de México"
  }
]
```

> **Nota:** La respuesta es un arreglo de objetos, pero para el MVP solo abra una universidad registrada, por lo que la
> respuesta será un arreglo con un solo objeto.

### Respuestas de error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

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

## Obtener dominios universitarios disponibles o registrados

**Ruta:** `/api/universities/domains`
**Método:** `GET`

**Descripción:** Este endpoint devuelve una lista de todos los dominios universitarios disponibles o registrados en el
sistema

**Parámetros de consulta:**

- `universityId` (opcional):  Filtra los dominios por la universidad especificada.

**Headers requeridos:**

- `Content-Type`: `application/json`
- `Cookie: accessToken=<jwt>`: Cookie con el token de acceso establecida por `POST /api/auth/login`. Se envía
  automáticamente en el encabezado `Cookie`.

**Ejemplo de petición:**

#### Sin parametros

Recuperar todos los dominios universitarios disponibles o registrados en el sistema.

```http request
GET /api/universities/domains HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

#### Con parametros

Recuperar todos los dominios universitarios disponibles o registrados en el sistema filtrados por la universidad

```http request
GET /api/universities/domains?universityId=77d3c6af-dff1-4ab1-87a3-4730581e5637 HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

### Respuesta exitosa

**Status:** `200 OK`

```json
{
  "UTSV": [
    "alumnos.utsv.edu.mx",
    "administrativos.utsv.edu.mx",
    "docentes.utsv.edu.mx"
  ],
  "UNAM": [
    "alumnos.unam.mx",
    "administrativos.unam.mx",
    "docentes.unam.mx"
  ],
  "IPN": [
    "alumnos.ipn.mx",
    "administrativos.ipn.mx",
    "docentes.ipn.mx"
  ]
}
```

### Respuestas de error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Error de validación

**Status:** `400 Bad Request`

```json
{
  "type": "https://example.com/errors/validation",
  "title": "Validation Error",
  "status": 400,
  "detail": "Uno o más campos no cumplen con las reglas de validación.",
  "instance": "/api/universities/domains",
  "errors": [
    {
      "field": "universityId",
      "message": "El ID de la universidad proporcionado no es válido."
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
  "instance": "/api/universities/domains"
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
  "instance": "/api/universities/domains",
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
  "instance": "/api/universities/domains"
}
```
