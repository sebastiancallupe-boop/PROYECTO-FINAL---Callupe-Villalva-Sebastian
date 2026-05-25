package com.rapidocourier.ms_auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "El password es obligatorio")
    private String password;

    @Pattern(regexp = "ADMIN|OPERADOR|CLIENTE|ROLE_ADMIN|ROLE_OPERADOR|ROLE_CLIENTE",
            message = "El rol debe ser ADMIN, OPERADOR o CLIENTE")
    private String role = "CLIENTE";
}
