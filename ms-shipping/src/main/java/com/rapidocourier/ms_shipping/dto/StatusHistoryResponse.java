package com.rapidocourier.ms_shipping.dto;

import com.rapidocourier.ms_shipping.entity.ShippingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public class StatusHistoryResponse {
    private UUID id;
    private ShippingStatus status;
    private LocalDateTime changedAt;
    private String changedBy;

    public static StatusHistoryResponseBuilder builder() { return new StatusHistoryResponseBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ShippingStatus getStatus() { return status; }
    public void setStatus(ShippingStatus status) { this.status = status; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public static class StatusHistoryResponseBuilder {
        private UUID id;
        private ShippingStatus status;
        private LocalDateTime changedAt;
        private String changedBy;

        public StatusHistoryResponseBuilder id(UUID id) { this.id = id; return this; }
        public StatusHistoryResponseBuilder status(ShippingStatus status) { this.status = status; return this; }
        public StatusHistoryResponseBuilder changedAt(LocalDateTime changedAt) { this.changedAt = changedAt; return this; }
        public StatusHistoryResponseBuilder changedBy(String changedBy) { this.changedBy = changedBy; return this; }

        public StatusHistoryResponse build() {
            StatusHistoryResponse response = new StatusHistoryResponse();
            response.id = this.id;
            response.status = this.status;
            response.changedAt = this.changedAt;
            response.changedBy = this.changedBy;
            return response;
        }
    }
}
