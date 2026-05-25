package com.rapidocourier.ms_shipping.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "shipping_status_history")
public class ShippingStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_id", nullable = false)
    private Shipping shipping;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShippingStatus status;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @Column(nullable = false)
    private String changedBy;

    public static ShippingStatusHistoryBuilder builder() { return new ShippingStatusHistoryBuilder(); }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Shipping getShipping() { return shipping; }
    public void setShipping(Shipping shipping) { this.shipping = shipping; }
    public ShippingStatus getStatus() { return status; }
    public void setStatus(ShippingStatus status) { this.status = status; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public static class ShippingStatusHistoryBuilder {
        private UUID id;
        private Shipping shipping;
        private ShippingStatus status;
        private LocalDateTime changedAt;
        private String changedBy;

        public ShippingStatusHistoryBuilder id(UUID id) { this.id = id; return this; }
        public ShippingStatusHistoryBuilder shipping(Shipping shipping) { this.shipping = shipping; return this; }
        public ShippingStatusHistoryBuilder status(ShippingStatus status) { this.status = status; return this; }
        public ShippingStatusHistoryBuilder changedAt(LocalDateTime changedAt) { this.changedAt = changedAt; return this; }
        public ShippingStatusHistoryBuilder changedBy(String changedBy) { this.changedBy = changedBy; return this; }

        public ShippingStatusHistory build() {
            ShippingStatusHistory history = new ShippingStatusHistory();
            history.id = this.id;
            history.shipping = this.shipping;
            history.status = this.status;
            history.changedAt = this.changedAt;
            history.changedBy = this.changedBy;
            return history;
        }
    }
}
