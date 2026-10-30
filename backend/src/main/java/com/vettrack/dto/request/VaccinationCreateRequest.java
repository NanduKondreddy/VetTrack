package com.vettrack.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class VaccinationCreateRequest {

    @NotNull(message = "Vaccine ID is required")
    private Long vaccineId;

    @NotNull(message = "Batch ID is required")
    private Long batchId;

    @NotNull(message = "Given on date is required")
    private LocalDate givenOn;

    private LocalDate nextDueOn;

    private Long visitId;

    public VaccinationCreateRequest() {}

    public VaccinationCreateRequest(Long vaccineId, Long batchId, LocalDate givenOn, LocalDate nextDueOn, Long visitId) {
        this.vaccineId = vaccineId;
        this.batchId = batchId;
        this.givenOn = givenOn;
        this.nextDueOn = nextDueOn;
        this.visitId = visitId;
    }

    public Long getVaccineId() { return vaccineId; }
    public void setVaccineId(Long vaccineId) { this.vaccineId = vaccineId; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public LocalDate getGivenOn() { return givenOn; }
    public void setGivenOn(LocalDate givenOn) { this.givenOn = givenOn; }
    public LocalDate getNextDueOn() { return nextDueOn; }
    public void setNextDueOn(LocalDate nextDueOn) { this.nextDueOn = nextDueOn; }
    public Long getVisitId() { return visitId; }
    public void setVisitId(Long visitId) { this.visitId = visitId; }
}
