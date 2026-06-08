package com.rapidocourier.api_gateway.filter;

import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    private final RouteValidator validator;
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(RouteValidator validator, JwtUtil jwtUtil) {
        super(Config.class);
        this.validator = validator;
        this.jwtUtil = jwtUtil;
    }

    public static class Config {
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (HttpMethod.OPTIONS.equals(exchange.getRequest().getMethod())) {
                return chain.filter(exchange);
            }

            if (validator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta el header Authorization");
                }

                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Formato de token invalido");
                }

                try {
                    Claims claims = jwtUtil.validateToken(authHeader.substring(7));
                    String role = claims.get("role", String.class);
                    validateRole(exchange.getRequest(), role);

                    ServerHttpRequest requestWithUser = exchange.getRequest().mutate()
                            .header("X-User", claims.getSubject())
                            .header("X-Role", role)
                            .build();

                    return chain.filter(exchange.mutate().request(requestWithUser).build());
                } catch (Exception e) {
                    if (e instanceof ResponseStatusException responseStatusException) {
                        throw responseStatusException;
                    }
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido", e);
                }
            }

            return chain.filter(exchange);
        };
    }

    private void validateRole(ServerHttpRequest request, String role) {
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        if (HttpMethod.DELETE.equals(method) && !"ROLE_ADMIN".equals(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo ADMIN puede eliminar registros");
        }

        boolean writesShipping = path.startsWith("/api/v1/shippings")
                && Set.of(HttpMethod.POST, HttpMethod.PUT, HttpMethod.PATCH).contains(method);

        if (writesShipping && !Set.of("ROLE_ADMIN", "ROLE_OPERADOR").contains(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo ADMIN u OPERADOR puede crear o actualizar envios");
        }
    }
}
