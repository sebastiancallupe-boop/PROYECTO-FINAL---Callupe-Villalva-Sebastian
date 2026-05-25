package com.rapidocourier.ms_customer.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "customers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 8, nullable = false)
    private String dni;

    private String fullName; // Se llenará con la API de RENIEC

    @Column(unique = true, nullable = false)
    private String email;
}