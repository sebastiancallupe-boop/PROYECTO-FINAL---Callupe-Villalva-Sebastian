package com.rapidocourier.ms_customer.service;

import com.rapidocourier.ms_customer.dto.CustomerRequest;
import com.rapidocourier.ms_customer.dto.ReniecResponse;
import com.rapidocourier.ms_customer.model.Customer;
import com.rapidocourier.ms_customer.repository.CustomerRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final ReniecClient reniecClient;

    @Value("${reniec.token}") // Trae el token desde el Config Server
    private String token;

    public Customer registerCustomer(CustomerRequest request) {
        if (repository.existsByDni(request.getDni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese DNI");
        }

        if (repository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese email");
        }

        ReniecResponse reniec = consultarReniec(request.getDni());

        if (reniec == null || isBlank(reniec.getNombres())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No se encontraron datos para el DNI indicado");
        }

        // Armamos el nombre real obtenido de la RENIEC
        String completeName = reniec.getNombres() + " " +
                reniec.getApellidoPaterno() + " " +
                reniec.getApellidoMaterno();

        Customer customer = Customer.builder()
                .dni(request.getDni())
                .email(request.getEmail())
                .fullName(completeName)
                .build();

        return repository.save(customer);
    }

    private ReniecResponse consultarReniec(String dni) {
        try {
            return reniecClient.obtenerDatosPorDni(dni, authorizationHeader());
        } catch (FeignException e) {
            if (e.status() == HttpStatus.UNAUTHORIZED.value() || e.status() == HttpStatus.FORBIDDEN.value()) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Token de RENIEC invalido o sin permisos", e);
            }

            if (e.status() == HttpStatus.NOT_FOUND.value()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "DNI no encontrado en RENIEC", e);
            }

            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "No se pudo consultar RENIEC", e);
        }
    }
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public boolean existsByDni(String dni) {
        return repository.existsByDni(dni);
    }

    public Customer getByDni(String dni) {
        return repository.findByDni(dni)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con DNI " + dni));
    }

    public Customer getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    public Customer updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = getById(id);

        if (!customer.getDni().equals(request.getDni()) && repository.existsByDni(request.getDni())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese DNI");
        }

        if (!customer.getEmail().equals(request.getEmail()) && repository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cliente con ese email");
        }

        customer.setDni(request.getDni());
        customer.setEmail(request.getEmail());
        return repository.save(customer);
    }

    public void deleteCustomer(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        repository.deleteById(id);
    }

    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    private String authorizationHeader() {
        if (isBlank(token)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se configuro reniec.token");
        }

        String cleanToken = token.trim();
        if (cleanToken.regionMatches(true, 0, "Bearer ", 0, "Bearer ".length())) {
            return cleanToken;
        }

        return "Bearer " + cleanToken;
    }
}
