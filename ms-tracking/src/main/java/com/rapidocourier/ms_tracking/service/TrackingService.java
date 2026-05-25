package com.rapidocourier.ms_tracking.service;

import com.rapidocourier.ms_tracking.dto.TrackingRequest;
import com.rapidocourier.ms_tracking.entity.Tracking;
import com.rapidocourier.ms_tracking.repository.TrackingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TrackingService {
    private final TrackingRepository repository;

    public Tracking saveTracking(TrackingRequest request) {
        Tracking tracking = Tracking.builder()
                .shippingId(request.getShippingId())
                .status(request.getStatus())
                .location(request.getLocation())
                .timestamp(LocalDateTime.now())
                .build();

        return repository.save(tracking);
    }

    public List<Tracking> findByShippingId(UUID shippingId) {
        return repository.findByShippingIdOrderByTimestampDesc(shippingId);
    }
}
