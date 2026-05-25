package com.rapidocourier.ms_shipping.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShippingRequest {
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

    @NotBlank(message = "El DNI del remitente es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI del remitente debe tener 8 digitos")
    private String senderDni;

    @NotBlank(message = "El DNI del destinatario es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI del destinatario debe tener 8 digitos")
    private String recipientDni;
}
