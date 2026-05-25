package com.rapidocourier.ms_tracking.controller;

import com.rapidocourier.ms_tracking.dto.ApiResponse;
import com.rapidocourier.ms_tracking.dto.TrackingRequest;
import com.rapidocourier.ms_tracking.entity.Tracking;
import com.rapidocourier.ms_tracking.service.TrackingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trackings")
@RequiredArgsConstructor
public class TrackingController {

    private final TrackingService service;

    @PostMapping
    public ResponseEntity<ApiResponse<Tracking>> create(@Valid @RequestBody TrackingRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.ok("Tracking registrado", service.saveTracking(request)));
    }

    @GetMapping("/shipping/{shippingId}")
    public ResponseEntity<ApiResponse<List<Tracking>>> findByShippingId(@PathVariable UUID shippingId) {
        return ResponseEntity.ok(ApiResponse.ok("Trackings encontrados", service.findByShippingId(shippingId)));
    }
}
