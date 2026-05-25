package com.rapidocourier.ms_shipping.service;

import com.rapidocourier.ms_shipping.client.CustomerClient;
import com.rapidocourier.ms_shipping.dto.ApiResponse;
import com.rapidocourier.ms_shipping.dto.ShippingRequest;
import com.rapidocourier.ms_shipping.dto.StatusUpdateRequest;
import com.rapidocourier.ms_shipping.entity.Shipping;
import com.rapidocourier.ms_shipping.entity.ShippingStatus;
import com.rapidocourier.ms_shipping.repository.PackageCategoryRepository;
import com.rapidocourier.ms_shipping.repository.ShippingRepository;
import com.rapidocourier.ms_shipping.repository.ShippingStatusHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShippingServiceTest {
    @Mock
    private ShippingRepository repository;

    @Mock
    private PackageCategoryRepository categoryRepository;

    @Mock
    private ShippingStatusHistoryRepository historyRepository;

    @Mock
    private CustomerClient customerClient;

    @InjectMocks
    private ShippingService service;

    @Test
    void createShippingCalculatesTariffAndTrackingCode() {
        ShippingRequest request = request();

        when(customerClient.checkCustomerExists("60805658")).thenReturn(ApiResponse.ok("ok", true));
        when(repository.save(any(Shipping.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Shipping response = service.createShipping(request, "operador");

        assertNotNull(response.getTrackingCode());
        assertEquals(new BigDecimal("28.75"), response.getTariff());
        assertEquals("REGISTRADO", response.getStatus());
    }

    @Test
    void updateStatusRejectsInvalidTransition() {
        UUID id = UUID.randomUUID();
        Shipping shipping = Shipping.builder()
                .id(id)
                .trackingCode("RC20260501ABC123")
                .description("Documentos")
                .status(ShippingStatus.ENTREGADO.name())
                .build();
        StatusUpdateRequest request = new StatusUpdateRequest();
        request.setNewStatus("EN_TRANSITO");

        when(repository.findById(id)).thenReturn(Optional.of(shipping));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.updateStatus(id, request, "operador"));

        assertTrue(ex.getReason().contains("Transicion invalida"));
    }

    @Test
    void getHistoryRejectsMissingShipping() {
        UUID id = UUID.randomUUID();
        when(repository.existsById(id)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.getHistory(id));

        assertEquals("Envio no encontrado", ex.getReason());
    }

    @Test
    void searchReturnsEmptyList() {
        when(repository.searchByText("ABC")).thenReturn(List.of());

        assertTrue(service.find("ABC", null, null).isEmpty());
    }

    private ShippingRequest request() {
        ShippingRequest request = new ShippingRequest();
        request.setDescription("Documentos legales");
        request.setWeightKg(new BigDecimal("1.50"));
        request.setDeclaredValue(new BigDecimal("200.00"));
        request.setOriginBranch("Lima");
        request.setDestinationBranch("Arequipa");
        request.setSenderDni("60805658");
        request.setRecipientDni("60805658");
        return request;
    }

}
