package com.vettrack.dto.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class VisitResponse {
    private Long id;
    private Long animalId;
    private Long vetId;
    private String vetName;
    private LocalDate visitDate;
    private String reason;
    private String notes;
    private OffsetDateTime createdAt;

    public VisitResponse() {}

    public VisitResponse(Long id, Long animalId, Long vetId, String vetName, LocalDate visitDate, String reason, String notes, OffsetDateTime createdAt) {
        this.id = id;
        this.animalId = animalId;
        this.vetId = vetId;
        this.vetName = vetName;
        this.visitDate = visitDate;
        this.reason = reason;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    public Long getVetId() { return vetId; }
    public void setVetId(Long vetId) { this.vetId = vetId; }
    public String getVetName() { return vetName; }
    public void setVetName(String vetName) { this.vetName = vetName; }
    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
