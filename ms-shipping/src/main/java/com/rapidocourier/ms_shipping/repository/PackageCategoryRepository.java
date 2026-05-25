package com.rapidocourier.ms_shipping.repository;

import com.rapidocourier.ms_shipping.entity.PackageCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PackageCategoryRepository extends JpaRepository<PackageCategory, UUID> {
    Optional<PackageCategory> findByNameIgnoreCase(String name);
}
