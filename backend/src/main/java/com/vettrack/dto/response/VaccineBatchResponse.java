package com.vettrack.dto.response;

import java.time.LocalDate;

public class VaccineBatchResponse {
    private Long id;
    private Long vaccineId;
    private String vaccineName;
    private String batchNo;
    private LocalDate expiryDate;
    private Integer quantityRemaining;
    private boolean expired;
    private boolean lowStock;

    public VaccineBatchResponse() {}

    public VaccineBatchResponse(Long id, Long vaccineId, String vaccineName, String batchNo, LocalDate expiryDate, Integer quantityRemaining) {
        this.id = id;
        this.vaccineId = vaccineId;
        this.vaccineName = vaccineName;
        this.batchNo = batchNo;
        this.expiryDate = expiryDate;
        this.quantityRemaining = quantityRemaining;
        this.expired = expiryDate != null && expiryDate.isBefore(LocalDate.now());
        this.lowStock = quantityRemaining != null && quantityRemaining <= 10;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVaccineId() { return vaccineId; }
    public void setVaccineId(Long vaccineId) { this.vaccineId = vaccineId; }
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public Integer getQuantityRemaining() { return quantityRemaining; }
    public void setQuantityRemaining(Integer quantityRemaining) { this.quantityRemaining = quantityRemaining; }
    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }
    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }
}
