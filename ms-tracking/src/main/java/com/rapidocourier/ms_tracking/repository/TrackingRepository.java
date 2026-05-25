package com.rapidocourier.ms_tracking.repository;

import com.rapidocourier.ms_tracking.entity.Tracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface TrackingRepository extends JpaRepository<Tracking, UUID> {

    // Este método te servirá para el RF-04 (Consultar historial de un paquete)
    List<Tracking> findByShippingIdOrderByTimestampDesc(UUID shippingId);
}