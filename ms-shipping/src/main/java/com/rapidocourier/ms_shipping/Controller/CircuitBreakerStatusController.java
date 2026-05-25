package com.rapidocourier.ms_shipping.Controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class CircuitBreakerStatusController {
    private final CircuitBreakerRegistry registry;

    public CircuitBreakerStatusController(CircuitBreakerRegistry registry) {
        this.registry = registry;
    }

    @GetMapping("/actuator/circuitbreakers")
    public Map<String, String> circuitBreakers() {
        return registry.getAllCircuitBreakers().stream()
                .collect(Collectors.toMap(CircuitBreaker::getName, cb -> cb.getState().name()));
    }
}
