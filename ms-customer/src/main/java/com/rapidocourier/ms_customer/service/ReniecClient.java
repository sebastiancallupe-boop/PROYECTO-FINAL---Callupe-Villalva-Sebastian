package com.rapidocourier.ms_customer.service;

import com.rapidocourier.ms_customer.dto.ReniecResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

// URL de ejemplo para el examen
@FeignClient(name = "reniec-client", url = "https://api.decolecta.com/v1/reniec")
public interface ReniecClient {

    @GetMapping("/dni")
    ReniecResponse obtenerDatosPorDni(@RequestParam("numero") String dni,
                                      @RequestHeader("Authorization") String authorization);
}
