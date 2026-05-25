# Respuestas Conceptuales

## 1. Por que microservicios

Se separo el sistema por dominios: autenticacion, clientes, envios y tracking. Esto evita que la logica de RENIEC, JWT, tarifa y estados quede mezclada en un solo servicio.

## 2. Comunicacion entre servicios

`ms-shipping` consulta a `ms-customer` usando Feign de forma sincronica porque antes de registrar un envio necesita confirmar que remitente y destinatario existen.

## 3. Por que API Gateway

El gateway es el punto unico de entrada. Centraliza validacion JWT, control basico por roles y enrutamiento hacia servicios registrados en Eureka.

## 4. Por que Config Server

El Config Server centraliza puertos, conexion a base de datos y propiedades de infraestructura. Asi los microservicios no duplican configuracion.

## 5. Por que Circuit Breaker

`ms-shipping` depende de `ms-customer`. El circuit breaker evita que una falla de clientes bloquee permanentemente el registro de envios y permite una respuesta degradada.
