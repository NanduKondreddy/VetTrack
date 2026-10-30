package com.vettrack.dto.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class VaccinationResponse {
    private Long id;
    private Long animalId;
    private String animalName;
    private Long vaccineId;
    private String vaccineName;
    private Long batchId;
    private String batchNo;
    private LocalDate givenOn;
    private LocalDate nextDueOn;
    private Long visitId;
    private boolean overdue;
    private OffsetDateTime createdAt;

    public VaccinationResponse() {}

    public VaccinationResponse(Long id, Long animalId, String animalName, Long vaccineId, String vaccineName,
                               Long batchId, String batchNo, LocalDate givenOn, LocalDate nextDueOn, Long visitId, OffsetDateTime createdAt) {
        this.id = id;
        this.animalId = animalId;
        this.animalName = animalName;
        this.vaccineId = vaccineId;
        this.vaccineName = vaccineName;
        this.batchId = batchId;
        this.batchNo = batchNo;
        this.givenOn = givenOn;
        this.nextDueOn = nextDueOn;
        this.visitId = visitId;
        this.overdue = nextDueOn != null && nextDueOn.isBefore(LocalDate.now());
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    public String getAnimalName() { return animalName; }
    public void setAnimalName(String animalName) { this.animalName = animalName; }
    public Long getVaccineId() { return vaccineId; }
    public void setVaccineId(Long vaccineId) { this.vaccineId = vaccineId; }
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public LocalDate getGivenOn() { return givenOn; }
    public void setGivenOn(LocalDate givenOn) { this.givenOn = givenOn; }
    public LocalDate getNextDueOn() { return nextDueOn; }
    public void setNextDueOn(LocalDate nextDueOn) { this.nextDueOn = nextDueOn; }
    public Long getVisitId() { return visitId; }
    public void setVisitId(Long visitId) { this.visitId = visitId; }
    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
