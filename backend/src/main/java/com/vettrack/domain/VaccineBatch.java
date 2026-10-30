package com.vettrack.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "vaccine_batches", uniqueConstraints = {
    @UniqueConstraint(name = "uq_vaccine_batch", columnNames = {"vaccine_id", "batch_no"})
})
public class VaccineBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    @Column(name = "batch_no", nullable = false, length = 100)
    private String batchNo;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "quantity_remaining", nullable = false)
    private Integer quantityRemaining;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public VaccineBatch() {}

    public VaccineBatch(Vaccine vaccine, String batchNo, LocalDate expiryDate, Integer quantityRemaining) {
        this.vaccine = vaccine;
        this.batchNo = batchNo;
        this.expiryDate = expiryDate;
        this.quantityRemaining = quantityRemaining;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public boolean isExpired(LocalDate asOfDate) {
        return expiryDate != null && expiryDate.isBefore(asOfDate);
    }

    public boolean isOutOfStock() {
        return quantityRemaining == null || quantityRemaining <= 0;
    }

    public void decrementStock() {
        if (this.quantityRemaining == null || this.quantityRemaining <= 0) {
            throw new IllegalStateException("Cannot decrement empty batch stock");
        }
        this.quantityRemaining -= 1;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Vaccine getVaccine() { return vaccine; }
    public void setVaccine(Vaccine vaccine) { this.vaccine = vaccine; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Integer getQuantityRemaining() { return quantityRemaining; }
    public void setQuantityRemaining(Integer quantityRemaining) { this.quantityRemaining = quantityRemaining; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
