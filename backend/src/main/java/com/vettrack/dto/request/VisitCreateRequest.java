package com.vettrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class VisitCreateRequest {

    private Long vetId;

    @NotNull(message = "Visit date is required")
    private LocalDate visitDate;

    @NotBlank(message = "Reason for visit is required")
    @Size(max = 255, message = "Reason must not exceed 255 characters")
    private String reason;

    private String notes;

    public VisitCreateRequest() {}

    public Long getVetId() { return vetId; }
    public void setVetId(Long vetId) { this.vetId = vetId; }
    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
