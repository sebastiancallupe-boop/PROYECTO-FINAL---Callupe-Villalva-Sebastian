# RapidoCourier S.A.C.

Backend con microservicios para gestion de clientes, autenticacion, envios y tracking.

## Microservicios

| Servicio | Responsabilidad | Entidades principales | Comunicacion |
|---|---|---|---|
| ms-auth | Registro/login y emision JWT | User | Independiente |
| ms-customer | Registro de clientes por DNI y consulta RENIEC | Customer | Expone datos de cliente a ms-shipping |
| ms-shipping | Registro de paquetes/envios, tarifa, estados, historial y categorias | Shipping, PackageCategory, ShippingStatusHistory | Consulta ms-customer con Feign |
| ms-tracking | Eventos de tracking externos al envio | Tracking | Independiente |

Todos usan PostgreSQL porque el dominio es transaccional y requiere consistencia en registros de clientes, envios e historiales.

## Seguridad

Roles implementados:

```text
ROLE_ADMIN
ROLE_OPERADOR
ROLE_CLIENTE
```

Usuarios iniciales:

| Usuario | Password | Rol |
|---|---|---|
| admin | Admin123 | ROLE_ADMIN |
| operador | Operador123 | ROLE_OPERADOR |
| cliente | Cliente123 | ROLE_CLIENTE |

El JWT se valida en `api-gateway`. El gateway permite login y registro sin token; el resto requiere `Authorization: Bearer TOKEN`. `ADMIN` puede eliminar; `ADMIN` y `OPERADOR` pueden crear o actualizar envios.

## Regla de tarifa

La tarifa se calcula automaticamente al registrar un envio:

```text
tarifa = 8.00 + pesoKg * 4.50 + valorDeclarado * 0.01 + recargoRuta
```

Recargos de ruta:

| Ruta | Recargo |
|---|---:|
| Misma sucursal | S/ 5.00 |
| Lima - Arequipa | S/ 12.00 |
| Lima - Cusco | S/ 15.00 |
| Arequipa - Cusco | S/ 10.00 |
| Otra ruta | S/ 18.00 |

Se eligio esta regla porque depende de peso, valor declarado y ruta, que son los factores indicados por el caso del examen.

## Estados del envio

Estados validos:

```text
REGISTRADO -> EN_TRANSITO -> EN_REPARTO -> ENTREGADO
```

Cancelacion permitida:

```text
REGISTRADO -> CANCELADO
EN_TRANSITO -> CANCELADO
EN_REPARTO -> CANCELADO
```

Las transiciones invalidas se rechazan con `409 Conflict`. Cada cambio se guarda en `shipping_status_history` con estado, fecha/hora y usuario recibido en el header `X-User`.

## Endpoints principales por Gateway

Base URL:

```text
http://localhost:8080
```

Registrar envio:

```http
POST /api/v1/shippings
```

```json
{
  "description": "Documentos legales",
  "weightKg": 1.5,
  "declaredValue": 200.00,
  "originBranch": "Lima",
  "destinationBranch": "Arequipa",
  "senderDni": "60805658",
  "recipientDni": "60805658"
}
```

Actualizar estado:

```http
PATCH /api/v1/shippings/{id}/status
```

```json
{
  "newStatus": "EN_TRANSITO"
}
```

Consultar historial:

```http
GET /api/v1/shippings/{id}/history
```

Buscar por codigo de rastreo o nombre:

```http
GET /api/v1/shippings?search=texto
```

Filtrar por sucursal y estado:

```http
GET /api/v1/shippings?branch=Lima&status=EN_TRANSITO
```

Asignar categorias:

```http
POST /api/v1/shippings/{id}/categories
```

```json
{
  "categoryNames": ["DOCUMENTOS", "FRAGIL"]
}
```

CRUD basico:

```text
GET    /api/v1/customers
GET    /api/v1/customers/{id}
POST   /api/v1/customers
PUT    /api/v1/customers/{id}
DELETE /api/v1/customers/{id}

GET    /api/v1/shippings
GET    /api/v1/shippings/{id}
POST   /api/v1/shippings
PUT    /api/v1/shippings/{id}
DELETE /api/v1/shippings/{id}
```

Todas las respuestas de negocio usan:

```json
{
  "success": true,
  "message": "mensaje",
  "data": {}
}
```

## Orden de arranque local

1. PostgreSQL
2. config-server
3. eureka-server
4. ms-auth
5. ms-customer
6. ms-shipping
7. ms-tracking
8. api-gateway

## Actuator y Config Refresh

`ms-shipping` expone circuit breakers:

```bash
curl http://localhost:8082/actuator/circuitbreakers
```

Para demostrar refresh de configuracion en `ms-shipping`:

```bash
curl -X POST http://localhost:8082/actuator/refresh
```

Circuit breaker configurado:

```text
customerServiceCB
failureRateThreshold: 50
waitDurationInOpenState: 10s
slidingWindowSize: 5
```

Retry configurado:

```text
customerServiceRetry
maxAttempts: 3
waitDuration: 500ms
```

## Pruebas

Hay pruebas unitarias con Mockito en:

```text
ms-customer/src/test/java/.../CustomerServiceTest.java
ms-shipping/src/test/java/.../ShippingServiceTest.java
```

Ejecutar:

```bash
mvn test
```

## Documentacion Swagger

```text
ms-auth      http://localhost:8084/swagger-ui/index.html
ms-customer  http://localhost:8081/swagger-ui/index.html
ms-shipping  http://localhost:8082/swagger-ui/index.html
ms-tracking  http://localhost:8083/swagger-ui/index.html
```
