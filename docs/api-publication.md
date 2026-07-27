# API Publicaciones

Estos endpoints están diseñados para proporcionar información sobre publicaciones disponibles, Realizar publicaciones o
actualizarlas. A continuación, se detallan los endpoints disponibles:

## Obtener publicaciones disponibles

**Ruta:** `/api/posts`
**Método:** `GET`

**Descripción:** Este endpoint devuelve una lista de todas las publicaciones disponibles.

**Headers requeridos:**

- `Cookie: accessToken=<jwt>`: Cookie con el token de acceso establecida por `POST /api/auth/login`. Se envía
  automáticamente en el encabezado `Cookie`.
- `Content-Type`: `application/json`

**Parámetros de consulta:**

| Campo          | Tipo      | Requerido | Default | Descripción                                                                       |
|:---------------|:----------|:----------|:--------|:----------------------------------------------------------------------------------|
| `page`         | `number`  | No        | `1`     | Número de página para la paginación.                                              |
| `limit`        | `number`  | No        | `10`    | Número de publicaciones por página.                                               |
| `search`       | `string`  | No        | —       | Término de búsqueda para filtrar publicaciones por título.                        |
| `isOutOfStock` | `boolean` | No        | —       | Filtra las publicaciones por su estado de existencia. Puede ser `true` o `false`. |

### Reglas de validación

#### `page`

- No puede ser nulo.
- Debe ser un número entero mayor o igual a 1.
- Si el valor excede el número total de páginas disponibles, la respuesta será un arreglo `posts` vacío sin error.

#### `limit`

- No puede ser nulo.
- Debe ser un número entero mayor o igual a 1.
- Debe ser un número entero menor o igual a 100.

#### `search`

- Si está presente, no puede ser una cadena vacía.
- Debe tener una longitud máxima de 120 caracteres.
- El término de búsqueda se compara de forma parcial (case-insensitive) contra el `title` de cada publicación.

#### `isOutOfStock`

- Si está presente, debe ser un valor booleano (`true` o `false`).
- Si es `true`, devuelve únicamente las publicaciones cuyo producto está agotado.
- Si es `false`, devuelve únicamente las publicaciones cuyo producto aún tiene existencias.

**Ejemplo de petición:**

#### Sin parametros

```http request
GET /api/posts HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

#### Con parametro `page`

```http request
GET /api/posts?page=4 HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

#### Con parametro `limit`

```http request
GET /api/posts?limit=5 HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

#### Con parametros `page` y `limit`

```http request
GET /api/posts?page=2&limit=20 HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

#### Con parametro `search`

```http request
GET /api/posts?page=1&limit=10&search=example HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

### Con parametro `isOutOfStock`

```http request
GET /api/posts?page=1&limit=10&isOutOfStock=true HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

### Todos los parametros

```http request
GET /api/posts?page=1&limit=10&search=example&isOutOfStock=false HTTP/1.1
Host: api.example.com
Cookie: accessToken=<jwt>
Content-Type: application/json
```

### Respuesta exitosa

**Status:** `200 OK`

```json
{
  "metadata": {
    "page": 1,
    "limit": 10,
    "totalPages": 5,
    "totalPosts": 50,
    "next": "/api/posts?page=2&limit=10"
  },
  "posts": [
    {
      "id": "77d3c6af-dff1-4ab1-87a3-4730581e5640",
      "title": "Venta de Hamburguesas",
      "description": "Hamburguesas artesanales de res, pollo y vegetariana. ¡Deliciosas y frescas!",
      "mediaContent": [
        {
          "url": "https://example.com/image1.jpg",
          "type": "IMAGE",
          "position": 0
        },
        {
          "url": "https://example.com/video1.mp4",
          "type": "VIDEO",
          "position": 1
        }
      ],
      "product": {
        "quantity": 100,
        "price": 19.99,
        "typeSale": "BY_QUANTITY",
        "allowsLayaway": true,
        "isOutOfStock": true
      },
      "postedAt": "2024-06-01T12:00:00Z",
      "updatedAt": "2024-06-01T12:00:00Z"
    }
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
  "instance": "/api/posts",
  "errors": [
    {
      "field": "page",
      "message": "El número de página debe ser un entero mayor o igual a 1."
    },
    {
      "field": "limit",
      "message": "El límite de publicaciones por página debe ser un entero entre 1 y 100."
    },
    {
      "field": "search",
      "message": "El término de búsqueda no puede estar vacío y debe tener como máximo 120 caracteres."
    },
    {
      "field": "isOutOfStock",
      "message": "El valor de isOutOfStock debe ser un booleano (true o false)."
    }
  ]
}
```

#### Error de autenticación

**Status:** `401 Unauthorized`

```json
{
  "type": "https://example.com/errors/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "El token de autenticación es inválido o ha expirado.",
  "instance": "/api/posts"
}
```

**Status:** `429 Too Many Requests`

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de solicitudes. Inténtalo de nuevo más tarde.",
  "instance": "/api/posts",
  "retryAfter": 60
}
```

#### Error interno del servidor

**Status:** `500 Internal Server Error`

```json
{
  "type": "https://example.com/errors/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Ocurrió un error inesperado en el servidor. Por favor, inténtalo de nuevo más tarde.",
  "instance": "/api/posts"
}
```

## Crear una nueva publicación

**Ruta:** `/api/posts`
**Método:** `POST`

**Descripción:** Este endpoint permite crear una nueva publicación en el sistema.

**Headers requeridos:**

- `Cookie: accessToken=<jwt>`: Cookie con el token de acceso establecida por `POST /api/auth/login`. Se envía
  automáticamente en el encabezado `Cookie`.
- `Content-Type`: `application/json`

### Parámetros de la petición

| Campo          | Tipo             | Descripción                                                                                          |
|:---------------|:-----------------|:-----------------------------------------------------------------------------------------------------|
| `title`        | `string`         | Título de la publicación.                                                                            |
| `description`  | `string`         | Contenido de la publicación.                                                                         |
| `mediaContent` | `MediaContent[]` | Objetos con la URL, tipo y orden del contenido multimedia por subir                                  |
| `product`      | `Product`        | Objeto (precio, ?cantidad, tipo_de_venta) con la información del producto asociado a la publicación. |

**MediaContent**:

```ts
interface MediaContent {
    "url": string,
    "type": "IMAGE" | "VIDEO",
    "position": number
}
```

- `url`: URL de la imagen o video de la publicación
- `type`: Que tipo de contenido es una imagen o un video. Util para saber que renderizar en el frontend
- `position`: Número de posición o el orden en que deben ir los archivos. Util para que los compradores vean un
  contenido visual especifico desde el inicio, despues y al final

**Product**:

```ts
interface Product {
    "quantity": number,
    "price": number,
    "typeSale": "BY_QUANTITY" | "UNTIL_SOLD_OUT" | "MADE_TO_ORDER",
    "allowsLayaway": boolean
}
```

- `quantity`: Cantidad del producto a vender 10, 20, 30 piezas. Si el tipo de venta es diferente de `BY_QUANTITY` es
  obligatorio que su valor sea -1.
- `price`: Precio del producto a vender
- `typeSale`: Indica como sera la venta del producto por cantidades/cifras exactas (ej: Hamburguesas 10 piezas
  disponibles), hasta agotar existencias (ej: Los esquites se vende hasta que se acabe el elote) o por pedido (ej:
  Pasteles solo por encargo)
- `allowsLayaway`: Indica si el vendedor acepta apartados/reservas (ej: el vendedor lleva 10 hamburgesas y un estudiante
  quiere apartar 4, solo le quedarán 6 hamburguesas al vendedor)

### Reglas de validación

Las validaciones descritas a continuación se aplican a nivel de **request/body** antes del procesamiento interno del
endpoint. Se implementan mediante anotaciones de Jakarta Validation (`@NotBlank`, `@Size`, `@Min`, `@Max`, etc.) o de
forma programática cuando la regla depende de otro campo.

#### `title`

- No puede ser nulo ni una cadena vacía.
- Debe tener una longitud mínima de 3 caracteres y máxima de 120 caracteres.

#### `description`

- No puede ser nula ni una cadena vacía.
- Debe tener una longitud mínima de 10 caracteres y máxima de 2000 caracteres.

#### `mediaContent`

- No puede ser nulo ni un arreglo vacío.
- El arreglo no puede contener más de 10 elementos.
- Cada elemento del arreglo debe cumplir las reglas de validación definidas en `MediaContent`.

#### `product`

- No puede ser nulo.
- Cada campo del objeto debe cumplir las reglas de validación definidas en `Product`.

---

#### `MediaContent`

Reglas aplicadas a cada elemento del arreglo `mediaContent`.

##### `url`

- No puede ser nula ni una cadena vacía.
- Debe ser una URL válida.
- Debe ser una URL que apunte a un recurso accesible públicamente (por ejemplo, una imagen o video en Cloudflare R2).
- No debe ser una URL que apunte a un recurso que requiera autenticación para acceder.

##### `type`

- No puede ser nulo.
- Debe ser uno de los valores permitidos: `IMAGE` o `VIDEO`.

##### `position`

- No puede ser nulo.
- Debe ser un número entero mayor o igual a 0.
- Debe ser un número entero menor o igual a 9.
- Indica el orden visual en que se mostrará el contenido multimedia de la publicación (por ejemplo: primero va esta
  imagen, seguido de un video y después más imágenes). Los valores aceptados van del `0` al `9`, lo que permite un
  máximo de 10 elementos multimedia por publicación.

---

#### `Product`

Reglas aplicadas al objeto `product`.

##### `quantity`

- No puede ser nulo.
- Si `typeSale` es `BY_QUANTITY`, debe ser un número entero mayor o igual a 1 y menor o igual a 1,000,000.
- Si `typeSale` es diferente de `BY_QUANTITY` (`UNTIL_SOLD_OUT` o `MADE_TO_ORDER`), su valor debe ser obligatoriamente
  `-1`.

##### `price`

- No puede ser nulo.
- Debe ser un número mayor o igual a `0.01`.
- Debe ser un número menor o igual a `9,999,999.99`.
- Debe tener como máximo 2 decimales.

##### `typeSale`

- No puede ser nulo.
- Debe ser uno de los valores permitidos: `BY_QUANTITY`, `UNTIL_SOLD_OUT` o `MADE_TO_ORDER`.

##### `allowsLayaway`

- No puede ser nulo.
- Debe ser un valor booleano (`true` o `false`).

**Ejemplod de petición:**

```json
{
  "title": "Venta de Hamburguesas",
  "description": "Hamburguesas artesanales de res, pollo y vegetariana. ¡Deliciosas y frescas!",
  "mediaContent": [
    {
      "url": "https://example.com/image1.jpg",
      "type": "IMAGE",
      "position": 0
    },
    {
      "url": "https://example.com/video1.mp4",
      "type": "VIDEO",
      "position": 1
    }
  ],
  "product": {
    "quantity": 100,
    "price": 19.99,
    "typeSale": "BY_QUANTITY",
    "allowsLayaway": true
  }
}
```

### Respuesta exitosa

**Status:** `201 Created`

```json
{
  "id": "77d3c6af-dff1-4ab1-87a3-4730581e5640",
  "title": "Venta de Hamburguesas",
  "description": "Hamburguesas artesanales de res, pollo y vegetariana. ¡Deliciosas y frescas!",
  "mediaContent": [
    {
      "url": "https://example.com/image1.jpg",
      "type": "IMAGE",
      "position": 0
    },
    {
      "url": "https://example.com/video1.mp4",
      "type": "VIDEO",
      "position": 1
    }
  ],
  "product": {
    "quantity": 100,
    "price": 19.99,
    "typeSale": "BY_QUANTITY",
    "allowsLayaway": true
  },
  "createdAt": "2024-06-01T12:00:00Z",
  "updatedAt": "2024-06-01T12:00:00Z"
}
```

```http
HTTP/1.1 201 Created
Content-Type: application/json
Location: /api/posts/77d3c6af-dff1-4ab1-87a3-4730581e5640
```

> El cuerpo de la respuesta se muestra en el bloque JSON superior.

> El tipo de respuesta `201 Created` indica que la publicación se ha creado correctamente y se devuelve el objeto de la
> publicación creada con todos sus campos y una URL para acceder a la publicación recién creada (Header `Location`).

### Respuestas de error

Las respuestas de error siguen el
estándar [RFC 9457: Problem Details for HTTP APIs](https://www.rfc-editor.org/info/rfc9457/).

#### Datos inválidos

**Status:** `400 Bad Request`

```json
{
  "type": "https://example.com/errors/validation",
  "title": "Validation Error",
  "status": 400,
  "detail": "Uno o más campos no cumplen con las reglas de validación.",
  "instance": "/api/posts",
  "errors": [
    {
      "field": "title",
      "message": "El título es obligatorio y debe tener entre 3 y 120 caracteres."
    },
    {
      "field": "description",
      "message": "La descripción es obligatoria y debe tener entre 10 y 2000 caracteres."
    },
    {
      "field": "mediaContent",
      "message": "El arreglo no puede estar vacío y debe contener como máximo 10 elementos."
    },
    {
      "field": "mediaContent[0].url",
      "message": "La URL debe ser una URL válida y accesible públicamente."
    },
    {
      "field": "product.quantity",
      "message": "La cantidad debe ser un número entero mayor o igual a 1 cuando el tipo de venta es BY_QUANTITY."
    },
    {
      "field": "product.price",
      "message": "El precio es obligatorio y debe tener como máximo 2 decimales."
    }
  ]
}
```

#### Error de autenticación

**Status:** `401 Unauthorized`

```json
{
  "type": "https://example.com/errors/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "El token de autenticación es inválido o ha expirado.",
  "instance": "/api/posts"
}
```

#### Demasiadas solicitudes

**Status:** `429 Too Many Requests`

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de publicaciones. Inténtalo de nuevo más tarde.",
  "instance": "/api/posts",
  "retryAfter": 60
}
```

#### Error interno del servidor

**Status:** `500 Internal Server Error`

```json
{
  "type": "https://example.com/errors/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Ocurrió un error inesperado en el servidor. Por favor, inténtalo de nuevo más tarde.",
  "instance": "/api/posts"
}
```

## Obtener una publicación por ID

**Ruta:** `/api/posts/{postId}`
**Método:** `GET`

**Descripción:** Este endpoint devuelve los detalles de una publicación específica identificada por su ID.

**Headers requeridos:**

- `Cookie: accessToken=<jwt>`: Cookie con el token de acceso establecida por `POST /api/auth/login`. Se envía
  automáticamente en el encabezado `Cookie`.
- `Content-Type`: `application/json`

**Paramétros de ruta:**

- `postId` (requerido): ID de la publicación que se desea obtener.

### Regla de validación

#### `postId`

- No puede ser nulo ni una cadena vacía.
- Debe ser un UUID válido.

### Respuesta exitosa

**Status:**: 200 OK

```json
{
  "id": "77d3c6af-dff1-4ab1-87a3-4730581e5640",
  "title": "Venta de Hamburguesas",
  "description": "Hamburguesas artesanales de res, pollo y vegetariana. ¡Deliciosas y frescas!",
  "mediaContent": [
    {
      "url": "https://example.com/image1.jpg",
      "type": "IMAGE",
      "position": 0
    },
    {
      "url": "https://example.com/video1.mp4",
      "type": "VIDEO",
      "position": 1
    }
  ],
  "product": {
    "quantity": 100,
    "price": 19.99,
    "typeSale": "BY_QUANTITY",
    "allowsLayaway": true,
    "isOutOfStock": false
  },
  "postedAt": "2024-06-01T12:00:00Z",
  "updatedAt": "2024-06-01T12:00:00Z"
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
  "instance": "/api/posts/{postId}",
  "errors": [
    {
      "field": "postId",
      "message": "El ID de la publicación proporcionado no es válido."
    }
  ]
}
```

#### Error de autenticación

**Status:** `401 Unauthorized`

```json
{
  "type": "https://example.com/errors/unauthorized",
  "title": "Unauthorized",
  "status": 401,
  "detail": "El token de autenticación es inválido o ha expirado.",
  "instance": "/api/posts/{postId}"
}
```

#### Publicación no encontrada

**Status:** `404 Not Found`

```json
{
  "type": "https://example.com/errors/not-found",
  "title": "Not Found",
  "status": 404,
  "detail": "La publicación con el ID proporcionado no fue encontrada.",
  "instance": "/api/posts/{postId}"
}
```

#### Demasiadas solicitudes

**Status:** `429 Too Many Requests`

```json
{
  "type": "https://example.com/errors/rate-limit",
  "title": "Too Many Requests",
  "status": 429,
  "detail": "Has superado el número máximo de solicitudes. Inténtalo de nuevo más tarde.",
  "instance": "/api/posts/{postId}",
  "retryAfter": 60
}
```

#### Error interno del servidor

**Status:** `500 Internal Server Error`

```json
{
  "type": "https://example.com/errors/internal-server-error",
  "title": "Internal Server Error",
  "status": 500,
  "detail": "Ocurrió un error inesperado en el servidor. Por favor, inténtalo de nuevo más tarde.",
  "instance": "/api/posts/{postId}"
}
```