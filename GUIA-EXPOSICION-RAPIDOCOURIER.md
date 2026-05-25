# Guia de exposicion - RapidoCourier S.A.C.

## Descripcion general

RapidoCourier S.A.C. es un backend de gestion logistica construido con microservicios. El sistema cubre autenticacion, registro de clientes, registro de envios, calculo de tarifa, estados del envio, historial y eventos de tracking.

La idea central es separar responsabilidades por dominio: `ms-auth` maneja usuarios y JWT, `ms-customer` maneja clientes y validacion RENIEC, `ms-shipping` maneja envios y reglas logisticas, y `ms-tracking` registra eventos de seguimiento. Todos se coordinan mediante `api-gateway`, `eureka-server` y `config-server`.

## Componentes

| Componente | Puerto | Responsabilidad |
|---|---:|---|
| `api-gateway` | 8080 | Entrada unica, validacion JWT, roles, trazabilidad y enrutamiento |
| `config-server` | 8888 | Configuracion centralizada de servicios |
| `eureka-server` | 8761 | Registro y descubrimiento de microservicios |
| `ms-auth` | 8084 | Registro/login, roles y emision de JWT |
| `ms-customer` | 8081 | Clientes, DNI, email y consulta RENIEC |
| `ms-shipping` | 8082 | Envios, tarifa, estado, categorias e historial |
| `ms-tracking` | 8083 | Eventos de tracking por `shippingId` |
| PostgreSQL | 5432 | Persistencia transaccional |

## Como se comunican

1. El cliente consume siempre el gateway: `http://localhost:8080`.
2. El gateway revisa si la ruta es publica o protegida.
3. Si esta protegida, valida `Authorization: Bearer TOKEN`.
4. El gateway extrae usuario y rol del JWT, agrega `X-User`, `X-Role` y `X-Transaction-Id`.
5. El gateway enruta con `lb://nombre-servicio`, usando Eureka.
6. El microservicio ejecuta su logica y persiste en PostgreSQL.

La comunicacion mas importante entre servicios es:

```text
ms-shipping -> Feign -> ms-customer
```

Antes de registrar un envio, `ms-shipping` consulta:

```text
GET /api/v1/customers/exists/{dni}
```

Si el remitente no existe, el envio no se registra. Si `ms-customer` falla, `ms-shipping` usa Resilience4j con retry y circuit breaker para evitar guardar informacion inconsistente.

## Seguridad

`ms-auth` genera tokens JWT. El gateway valida esos tokens y aplica reglas por rol:

- `ROLE_ADMIN`: puede eliminar registros.
- `ROLE_ADMIN` y `ROLE_OPERADOR`: pueden crear o actualizar envios.
- `ROLE_CLIENTE`: rol de menor privilegio.

Los endpoints abiertos son:

```text
POST /api/v1/auth/register
POST /api/v1/auth/login
```

Todo lo demas requiere token.

## Reglas principales de negocio

La tarifa del envio se calcula automaticamente:

```text
tarifa = 8.00 + pesoKg * 4.50 + valorDeclarado * 0.01 + recargoRuta
```

Estados validos:

```text
REGISTRADO -> EN_TRANSITO -> EN_REPARTO -> ENTREGADO
```

Tambien se permite cancelar desde:

```text
REGISTRADO, EN_TRANSITO, EN_REPARTO
```

Cada cambio de estado se guarda en `shipping_status_history` con fecha y usuario.

## Demo sugerida

1. Levantar servicios en orden: PostgreSQL, config-server, eureka-server, ms-auth, ms-customer, ms-shipping, ms-tracking y api-gateway.
2. Hacer login en `/api/v1/auth/login`.
3. Copiar el JWT y usarlo como `Authorization: Bearer TOKEN`.
4. Crear un cliente en `/api/v1/customers`.
5. Crear un envio en `/api/v1/shippings`.
6. Cambiar estado con `/api/v1/shippings/{id}/status`.
7. Consultar historial con `/api/v1/shippings/{id}/history`.
8. Registrar o consultar tracking con `/api/v1/trackings`.

## Frase final

RapidoCourier no es solo un conjunto de CRUDs; es una arquitectura coordinada donde cada microservicio cumple una parte del flujo logistico y el gateway centraliza seguridad, entrada y trazabilidad.
