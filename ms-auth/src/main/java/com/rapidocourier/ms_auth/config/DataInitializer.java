package com.rapidocourier.ms_auth.config;

import com.rapidocourier.ms_auth.entity.User;
import com.rapidocourier.ms_auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createUserIfMissing("admin", "Admin123", "ROLE_ADMIN");
        createUserIfMissing("operador", "Operador123", "ROLE_OPERADOR");
        createUserIfMissing("cliente", "Cliente123", "ROLE_CLIENTE");
    }

    private void createUserIfMissing(String username, String password, String role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }

        userRepository.save(User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build());
    }
}
