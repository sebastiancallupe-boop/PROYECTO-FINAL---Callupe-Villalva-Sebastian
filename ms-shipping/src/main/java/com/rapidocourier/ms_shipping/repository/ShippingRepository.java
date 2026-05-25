package com.rapidocourier.ms_shipping.repository;

import com.rapidocourier.ms_shipping.entity.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ShippingRepository extends JpaRepository<Shipping, UUID> {
    boolean existsByTrackingCode(String trackingCode);

    @Query(value = """
            SELECT * FROM shippings s
            WHERE LOWER(s.tracking_code) LIKE LOWER(CONCAT('%', :text, '%'))
               OR LOWER(s.sender_name) LIKE LOWER(CONCAT('%', :text, '%'))
               OR LOWER(s.recipient_name) LIKE LOWER(CONCAT('%', :text, '%'))
            """, nativeQuery = true)
    List<Shipping> searchByText(@Param("text") String text);

    @Query("""
            SELECT s FROM Shipping s
            WHERE (:status IS NULL OR s.status = :status)
              AND (LOWER(s.originBranch) = LOWER(:branch) OR LOWER(s.destinationBranch) = LOWER(:branch))
            """)
    List<Shipping> findByBranchAndStatus(@Param("branch") String branch, @Param("status") String status);
}
