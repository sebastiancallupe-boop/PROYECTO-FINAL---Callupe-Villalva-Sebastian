package com.rapidocourier.ms_shipping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class StatusUpdateRequest {
    @NotBlank(message = "El nuevo estado es obligatorio")
    @Pattern(regexp = "REGISTRADO|EN_TRANSITO|EN_REPARTO|ENTREGADO|CANCELADO",
            message = "Estado invalido")
    private String newStatus;

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
}
