package com.rapidocourier.ms_auth.controller;

import com.rapidocourier.ms_auth.dto.ApiResponse;
import com.rapidocourier.ms_auth.dto.AuthRequest;
import com.rapidocourier.ms_auth.dto.AuthResponse;
import com.rapidocourier.ms_auth.dto.RegisterRequest;
import com.rapidocourier.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.ok("Usuario registrado", authService.register(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login exitoso", authService.login(request)));
    }
}
