package com.rapidocourier.ms_shipping.repository;

import com.rapidocourier.ms_shipping.entity.ShippingStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ShippingStatusHistoryRepository extends JpaRepository<ShippingStatusHistory, UUID> {
    List<ShippingStatusHistory> findByShippingIdOrderByChangedAtAsc(UUID shippingId);
}
