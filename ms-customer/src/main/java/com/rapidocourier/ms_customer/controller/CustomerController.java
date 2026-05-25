package com.rapidocourier.ms_customer.controller;

import com.rapidocourier.ms_customer.dto.ApiResponse;
import com.rapidocourier.ms_customer.dto.CustomerRequest;
import com.rapidocourier.ms_customer.model.Customer;
import com.rapidocourier.ms_customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> create(@Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.ok("Cliente registrado", service.registerCustomer(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Clientes encontrados", service.getAllCustomers()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Cliente encontrado", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> update(@PathVariable UUID id, @Valid @RequestBody CustomerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Cliente actualizado", service.updateCustomer(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/exists/{dni}")
    public ResponseEntity<ApiResponse<Boolean>> existsByDni(@PathVariable String dni) {
        return ResponseEntity.ok(ApiResponse.ok("Consulta realizada", service.existsByDni(dni)));
    }

    @GetMapping("/dni/{dni}")
    public ResponseEntity<ApiResponse<Customer>> findByDni(@PathVariable String dni) {
        return ResponseEntity.ok(ApiResponse.ok("Cliente encontrado", service.getByDni(dni)));
    }
}
