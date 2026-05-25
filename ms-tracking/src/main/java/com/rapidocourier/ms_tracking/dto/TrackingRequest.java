package com.rapidocourier.ms_tracking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TrackingRequest {
    @NotNull(message = "El shippingId es obligatorio")
    private UUID shippingId;

    @NotBlank(message = "El estado es obligatorio")
    private String status;

    @NotBlank(message = "La ubicacion es obligatoria")
    private String location;
}
