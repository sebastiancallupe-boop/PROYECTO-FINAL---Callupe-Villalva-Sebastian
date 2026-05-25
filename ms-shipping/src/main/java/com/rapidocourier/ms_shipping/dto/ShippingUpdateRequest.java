package com.rapidocourier.ms_shipping.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ShippingUpdateRequest {
    @NotBlank(message = "La descripcion es obligatoria")
    @Size(max = 150, message = "La descripcion no debe superar 150 caracteres")
    private String description;

    @NotNull(message = "El peso es obligatorio")
    @Positive(message = "El peso debe ser mayor que cero")
    private BigDecimal weightKg;

    @NotNull(message = "El valor declarado es obligatorio")
    @DecimalMin(value = "0.00", message = "El valor declarado no puede ser negativo")
    private BigDecimal declaredValue;

    @NotBlank(message = "La sucursal de origen es obligatoria")
    private String originBranch;

    @NotBlank(message = "La sucursal de destino es obligatoria")
    private String destinationBranch;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public BigDecimal getDeclaredValue() { return declaredValue; }
    public void setDeclaredValue(BigDecimal declaredValue) { this.declaredValue = declaredValue; }
    public String getOriginBranch() { return originBranch; }
    public void setOriginBranch(String originBranch) { this.originBranch = originBranch; }
    public String getDestinationBranch() { return destinationBranch; }
    public void setDestinationBranch(String destinationBranch) { this.destinationBranch = destinationBranch; }
}
