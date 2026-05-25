package com.rapidocourier.ms_shipping.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public class ShippingResponse {
    private UUID id;
    private String trackingCode;
    private String description;
    private BigDecimal weightKg;
    private BigDecimal declaredValue;
    private BigDecimal tariff;
    private String originBranch;
    private String destinationBranch;
    private String senderDni;
    private String senderName;
    private String recipientDni;
    private String recipientName;
    private String status;
    private Set<String> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShippingResponseBuilder builder() { return new ShippingResponseBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTrackingCode() { return trackingCode; }
    public void setTrackingCode(String trackingCode) { this.trackingCode = trackingCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }
    public BigDecimal getDeclaredValue() { return declaredValue; }
    public void setDeclaredValue(BigDecimal declaredValue) { this.declaredValue = declaredValue; }
    public BigDecimal getTariff() { return tariff; }
    public void setTariff(BigDecimal tariff) { this.tariff = tariff; }
    public String getOriginBranch() { return originBranch; }
    public void setOriginBranch(String originBranch) { this.originBranch = originBranch; }
    public String getDestinationBranch() { return destinationBranch; }
    public void setDestinationBranch(String destinationBranch) { this.destinationBranch = destinationBranch; }
    public String getSenderDni() { return senderDni; }
    public void setSenderDni(String senderDni) { this.senderDni = senderDni; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getRecipientDni() { return recipientDni; }
    public void setRecipientDni(String recipientDni) { this.recipientDni = recipientDni; }
    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Set<String> getCategories() { return categories; }
    public void setCategories(Set<String> categories) { this.categories = categories; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static class ShippingResponseBuilder {
        private UUID id;
        private String trackingCode;
        private String description;
        private BigDecimal weightKg;
        private BigDecimal declaredValue;
        private BigDecimal tariff;
        private String originBranch;
        private String destinationBranch;
        private String senderDni;
        private String senderName;
        private String recipientDni;
        private String recipientName;
        private String status;
        private Set<String> categories;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ShippingResponseBuilder id(UUID id) { this.id = id; return this; }
        public ShippingResponseBuilder trackingCode(String trackingCode) { this.trackingCode = trackingCode; return this; }
        public ShippingResponseBuilder description(String description) { this.description = description; return this; }
        public ShippingResponseBuilder weightKg(BigDecimal weightKg) { this.weightKg = weightKg; return this; }
        public ShippingResponseBuilder declaredValue(BigDecimal declaredValue) { this.declaredValue = declaredValue; return this; }
        public ShippingResponseBuilder tariff(BigDecimal tariff) { this.tariff = tariff; return this; }
        public ShippingResponseBuilder originBranch(String originBranch) { this.originBranch = originBranch; return this; }
        public ShippingResponseBuilder destinationBranch(String destinationBranch) { this.destinationBranch = destinationBranch; return this; }
        public ShippingResponseBuilder senderDni(String senderDni) { this.senderDni = senderDni; return this; }
        public ShippingResponseBuilder senderName(String senderName) { this.senderName = senderName; return this; }
        public ShippingResponseBuilder recipientDni(String recipientDni) { this.recipientDni = recipientDni; return this; }
        public ShippingResponseBuilder recipientName(String recipientName) { this.recipientName = recipientName; return this; }
        public ShippingResponseBuilder status(String status) { this.status = status; return this; }
        public ShippingResponseBuilder categories(Set<String> categories) { this.categories = categories; return this; }
        public ShippingResponseBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ShippingResponseBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public ShippingResponse build() {
            ShippingResponse response = new ShippingResponse();
            response.id = this.id;
            response.trackingCode = this.trackingCode;
            response.description = this.description;
            response.weightKg = this.weightKg;
            response.declaredValue = this.declaredValue;
            response.tariff = this.tariff;
            response.originBranch = this.originBranch;
            response.destinationBranch = this.destinationBranch;
            response.senderDni = this.senderDni;
            response.senderName = this.senderName;
            response.recipientDni = this.recipientDni;
            response.recipientName = this.recipientName;
            response.status = this.status;
            response.categories = this.categories;
            response.createdAt = this.createdAt;
            response.updatedAt = this.updatedAt;
            return response;
        }
    }
}
