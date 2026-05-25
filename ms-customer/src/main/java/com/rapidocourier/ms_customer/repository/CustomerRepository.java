package com.rapidocourier.ms_customer.repository;

import com.rapidocourier.ms_customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByDni(String dni);

    boolean existsByEmail(String email);

    Optional<Customer> findByDni(String dni);
}
