package com.rapidocourier.ms_customer.service;

import com.rapidocourier.ms_customer.dto.CustomerRequest;
import com.rapidocourier.ms_customer.dto.ReniecResponse;
import com.rapidocourier.ms_customer.model.Customer;
import com.rapidocourier.ms_customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository repository;

    @Mock
    private ReniecClient reniecClient;

    @InjectMocks
    private CustomerService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "token", "test-token");
    }

    @Test
    void registerCustomerCreatesCustomerWithReniecName() {
        CustomerRequest request = request("60805658", "cliente@test.com");
        ReniecResponse reniec = new ReniecResponse();
        reniec.setNombres("JUAN");
        reniec.setApellidoPaterno("PEREZ");
        reniec.setApellidoMaterno("LOPEZ");

        when(repository.existsByDni("60805658")).thenReturn(false);
        when(repository.existsByEmail("cliente@test.com")).thenReturn(false);
        when(reniecClient.obtenerDatosPorDni(eq("60805658"), eq("Bearer test-token"))).thenReturn(reniec);
        when(repository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer result = service.registerCustomer(request);

        assertEquals("60805658", result.getDni());
        assertEquals("JUAN PEREZ LOPEZ", result.getFullName());
    }

    @Test
    void registerCustomerRejectsDuplicatedEmail() {
        CustomerRequest request = request("60805658", "cliente@test.com");

        when(repository.existsByDni("60805658")).thenReturn(false);
        when(repository.existsByEmail("cliente@test.com")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.registerCustomer(request));

        assertEquals("Ya existe un cliente con ese email", ex.getReason());
    }

    @Test
    void getByDniRejectsMissingCustomer() {
        when(repository.findByDni("60805658")).thenReturn(java.util.Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.getByDni("60805658"));

        assertEquals("Cliente no encontrado con DNI 60805658", ex.getReason());
    }

    @Test
    void getAllCustomersReturnsEmptyList() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.getAllCustomers().isEmpty());
    }

    private CustomerRequest request(String dni, String email) {
        CustomerRequest request = new CustomerRequest();
        request.setDni(dni);
        request.setEmail(email);
        return request;
    }
}
