package com.rapidocourier.ms_auth.service;

import com.rapidocourier.ms_auth.dto.AuthRequest;
import com.rapidocourier.ms_auth.dto.AuthResponse;
import com.rapidocourier.ms_auth.dto.RegisterRequest;
import com.rapidocourier.ms_auth.entity.User;
import com.rapidocourier.ms_auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Set<String> ALLOWED_ROLES = Set.of("ROLE_ADMIN", "ROLE_OPERADOR", "ROLE_CLIENTE");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya existe");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(normalizeRole(request.getRole()))
                .build();

        User savedUser = userRepository.save(user);
        return new AuthResponse(jwtService.generateToken(savedUser));
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales invalidas");
        }

        return new AuthResponse(jwtService.generateToken(user));
    }

    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return "ROLE_CLIENTE";
        }

        String cleanRole = role.trim().toUpperCase();
        String normalizedRole = cleanRole.startsWith("ROLE_") ? cleanRole : "ROLE_" + cleanRole;

        if (!ALLOWED_ROLES.contains(normalizedRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no permitido: " + role);
        }

        return normalizedRole;
    }
}
