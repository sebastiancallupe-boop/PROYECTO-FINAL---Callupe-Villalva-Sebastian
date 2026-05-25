package com.rapidocourier.ms_shipping.Controller;

import com.rapidocourier.ms_shipping.dto.ApiResponse;
import com.rapidocourier.ms_shipping.dto.AssignCategoriesRequest;
import com.rapidocourier.ms_shipping.dto.ShippingRequest;
import com.rapidocourier.ms_shipping.dto.ShippingResponse;
import com.rapidocourier.ms_shipping.dto.ShippingUpdateRequest;
import com.rapidocourier.ms_shipping.dto.StatusHistoryResponse;
import com.rapidocourier.ms_shipping.dto.StatusUpdateRequest;
import com.rapidocourier.ms_shipping.entity.Shipping;
import com.rapidocourier.ms_shipping.service.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/shippings")
@RequiredArgsConstructor
public class ShippingController {

    private final ShippingService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ShippingResponse>> create(
            @Valid @RequestBody ShippingRequest request,
            @RequestHeader(value = "X-User", defaultValue = "operador") String username) {

        Shipping createdShipping = service.createShipping(request, username);

        ShippingResponse responseDto = ShippingResponse.builder()
                .id(createdShipping.getId())
                .trackingCode(createdShipping.getTrackingCode())
                .description(createdShipping.getDescription())
                .weightKg(createdShipping.getWeightKg())
                .declaredValue(createdShipping.getDeclaredValue())
                .tariff(createdShipping.getTariff())
                .originBranch(createdShipping.getOriginBranch())
                .destinationBranch(createdShipping.getDestinationBranch())
                .senderDni(createdShipping.getSenderDni())
                .senderName(createdShipping.getSenderName())
                .recipientDni(createdShipping.getRecipientDni())
                .recipientName(createdShipping.getRecipientName())
                .status(createdShipping.getStatus())
                .createdAt(createdShipping.getCreatedAt())
                .updatedAt(createdShipping.getUpdatedAt())
                .build();

        return ResponseEntity.status(201)
                .body(ApiResponse.ok("Envio registrado con tarifa calculada", responseDto));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ShippingResponse>>> find(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(ApiResponse.ok("Envios encontrados", service.find(search, branch, status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShippingResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Envio encontrado", service.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ShippingResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody ShippingUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Envio actualizado", service.updateShipping(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteShipping(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ShippingResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest request,
            @RequestHeader(value = "X-User", defaultValue = "operador") String username) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado", service.updateStatus(id, request, username)));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<StatusHistoryResponse>>> history(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Historial encontrado", service.getHistory(id)));
    }

    @PostMapping("/{id}/categories")
    public ResponseEntity<ApiResponse<ShippingResponse>> assignCategories(
            @PathVariable UUID id,
            @Valid @RequestBody AssignCategoriesRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Categorias asignadas", service.assignCategories(id, request)));
    }
}
