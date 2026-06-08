# Guia Docker - RapidoCourier S.A.C.

Esta guia explica como levantar todo el backend con Docker Compose.

## Que se agrego

- `Dockerfile`: construye cualquier microservicio Spring Boot del proyecto.
- `docker-compose.yml`: levanta PostgreSQL, Eureka, Config Server, los microservicios, el API Gateway y el frontend.
- `.dockerignore`: evita copiar archivos pesados o innecesarios al construir las imagenes.

## Por que se hizo asi

El proyecto tiene varios microservicios separados. En vez de crear 7 Dockerfiles casi iguales, se creo un solo `Dockerfile` reutilizable. Cada servicio indica su carpeta con `SERVICE_DIR`.

Tambien se cambiaron las rutas `localhost` por variables con valor por defecto. Esto es importante porque dentro de Docker `localhost` significa "este mismo contenedor", no la PC ni otro servicio. Por ejemplo:

- Fuera de Docker sigue usando `http://localhost:8888`.
- Dentro de Docker usa `http://config-server:8888`.

PostgreSQL se agrego al Compose para que otra PC no tenga que instalar la base de datos aparte. Docker crea la base `rapidocourier_db` automaticamente.

## Ejecutar en esta u otra PC

Requisitos:

- Docker Desktop instalado.
- Docker Desktop abierto y funcionando.

Pasos:

```bash
docker compose up --build
```

La primera vez demora porque descarga imagenes y dependencias Maven.

Cuando termine de arrancar, usa:

```text
Frontend:    http://localhost:5173
API Gateway: http://localhost:8080
Eureka:      http://localhost:8761
PostgreSQL:  localhost:5432
```

Swagger por servicio:

```text
ms-auth:      http://localhost:8084/swagger-ui/index.html
ms-customer:  http://localhost:8081/swagger-ui/index.html
ms-shipping:  http://localhost:8082/swagger-ui/index.html
ms-tracking:  http://localhost:8083/swagger-ui/index.html
```

## Comandos utiles

Ver contenedores:

```bash
docker compose ps
```

Levantar todo en segundo plano:

```bash
docker compose up --build -d
```

Ver logs:

```bash
docker compose logs -f
```

Ver logs de un servicio:

```bash
docker compose logs -f api-gateway
```

Apagar sin borrar la base de datos:

```bash
docker compose down
```

Apagar y borrar la base de datos guardada por Docker:

```bash
docker compose down -v
```

## Si falla al iniciar

1. Verifica que Docker Desktop este abierto.
2. Verifica que los puertos `5173`, `5432`, `8080`, `8081`, `8082`, `8083`, `8084`, `8761` y `8888` no esten ocupados.
3. Ejecuta `docker compose logs -f` para ver que servicio fallo.
4. Si cambiaste codigo Java, vuelve a ejecutar `docker compose up --build`.
