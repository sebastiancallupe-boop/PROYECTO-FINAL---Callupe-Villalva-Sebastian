package com.rapidocourier.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GlobalTransactionFilter implements GlobalFilter, Ordered {

    private final Logger logger = LoggerFactory.getLogger(GlobalTransactionFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String transactionId = UUID.randomUUID().toString();

        logger.info("Nueva petición capturada. Generando ID de Transacción: {}", transactionId);

        // 1. Lo enviamos al microservicio (Request)
        // 1. Header al microservicio (Request)
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Transaction-Id", transactionId)
                        .build())
                .build();

        // 2. Header al cliente (Response) - se establece ANTES de que se escriba la respuesta
        mutatedExchange.getResponse().getHeaders().set("X-Transaction-Id", transactionId);

        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
