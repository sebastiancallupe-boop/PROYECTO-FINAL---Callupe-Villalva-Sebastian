package com.rapidocourier.ms_shipping.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "shippings")
public class Shipping {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, length = 24)
    private String trackingCode;

    @Column(nullable = false)
    private String description;

    @Column(precision = 10, scale = 2)
    private BigDecimal weightKg;

    @Column(precision = 10, scale = 2)
    private BigDecimal declaredValue;

    @Column(precision = 10, scale = 2)
    private BigDecimal tariff;

    @Column
    private String originBranch;

    // Le agregamos nullable = false para que sea obligatorio en BD
    @Column(nullable = false)
    private String destinationBranch;

    @Column(length = 8)
    private String senderDni;

    @Column
    private String senderName;

    @Column(length = 8)
    private String recipientDni;

    @Column
    private String recipientName;

    @Column(nullable = false, length = 8)
    private String customerDni;

    @Column
    private String status;

    @ManyToMany
    @JoinTable(
            name = "shipping_categories",
            joinColumns = @JoinColumn(name = "shipping_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<PackageCategory> categories = new LinkedHashSet<>();

    @OneToMany(mappedBy = "shipping", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ShippingStatusHistory> statusHistory = new LinkedHashSet<>();

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public static ShippingBuilder builder() { return new ShippingBuilder(); }

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
    public String getCustomerDni() { return customerDni; }
    public void setCustomerDni(String customerDni) { this.customerDni = customerDni; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Set<PackageCategory> getCategories() { return categories; }
    public void setCategories(Set<PackageCategory> categories) { this.categories = categories; }
    public Set<ShippingStatusHistory> getStatusHistory() { return statusHistory; }
    public void setStatusHistory(Set<ShippingStatusHistory> statusHistory) { this.statusHistory = statusHistory; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addHistory(ShippingStatus newStatus, String username) {
        ShippingStatusHistory history = ShippingStatusHistory.builder()
                .shipping(this)
                .status(newStatus)
                .changedBy(username)
                .changedAt(LocalDateTime.now())
                .build();
        statusHistory.add(history);
    }

    public static class ShippingBuilder {
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
        private String customerDni;
        private String status;
        private Set<PackageCategory> categories;
        private Set<ShippingStatusHistory> statusHistory;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public ShippingBuilder id(UUID id) { this.id = id; return this; }
        public ShippingBuilder trackingCode(String trackingCode) { this.trackingCode = trackingCode; return this; }
        public ShippingBuilder description(String description) { this.description = description; return this; }
        public ShippingBuilder weightKg(BigDecimal weightKg) { this.weightKg = weightKg; return this; }
        public ShippingBuilder declaredValue(BigDecimal declaredValue) { this.declaredValue = declaredValue; return this; }
        public ShippingBuilder tariff(BigDecimal tariff) { this.tariff = tariff; return this; }
        public ShippingBuilder originBranch(String originBranch) { this.originBranch = originBranch; return this; }
        public ShippingBuilder destinationBranch(String destinationBranch) { this.destinationBranch = destinationBranch; return this; }
        public ShippingBuilder senderDni(String senderDni) { this.senderDni = senderDni; return this; }
        public ShippingBuilder senderName(String senderName) { this.senderName = senderName; return this; }
        public ShippingBuilder recipientDni(String recipientDni) { this.recipientDni = recipientDni; return this; }
        public ShippingBuilder recipientName(String recipientName) { this.recipientName = recipientName; return this; }
        public ShippingBuilder customerDni(String customerDni) { this.customerDni = customerDni; return this; }
        public ShippingBuilder status(String status) { this.status = status; return this; }
        public ShippingBuilder categories(Set<PackageCategory> categories) { this.categories = categories; return this; }
        public ShippingBuilder statusHistory(Set<ShippingStatusHistory> statusHistory) { this.statusHistory = statusHistory; return this; }
        public ShippingBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ShippingBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Shipping build() {
            Shipping shipping = new Shipping();
            shipping.id = this.id;
            shipping.trackingCode = this.trackingCode;
            shipping.description = this.description;
            shipping.weightKg = this.weightKg;
            shipping.declaredValue = this.declaredValue;
            shipping.tariff = this.tariff;
            shipping.originBranch = this.originBranch;
            shipping.destinationBranch = this.destinationBranch;
            shipping.senderDni = this.senderDni;
            shipping.senderName = this.senderName;
            shipping.recipientDni = this.recipientDni;
            shipping.recipientName = this.recipientName;
            shipping.customerDni = this.customerDni;
            shipping.status = this.status;
            shipping.categories = this.categories != null ? this.categories : new LinkedHashSet<>();
            shipping.statusHistory = this.statusHistory != null ? this.statusHistory : new LinkedHashSet<>();
            return shipping;
        }
    }
}