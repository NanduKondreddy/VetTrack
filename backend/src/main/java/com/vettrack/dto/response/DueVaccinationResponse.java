package com.vettrack.dto.response;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DueVaccinationResponse {
    private Long vaccinationId;
    private Long animalId;
    private String animalName;
    private String species;
    private String breed;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private String vaccineName;
    private String batchNo;
    private LocalDate givenOn;
    private LocalDate nextDueOn;
    private long daysRemaining;
    private boolean overdue;

    public DueVaccinationResponse() {}

    public DueVaccinationResponse(Long vaccinationId, Long animalId, String animalName, String species, String breed,
                                  Long ownerId, String ownerName, String ownerPhone, String ownerEmail,
                                  String vaccineName, String batchNo, LocalDate givenOn, LocalDate nextDueOn) {
        this.vaccinationId = vaccinationId;
        this.animalId = animalId;
        this.animalName = animalName;
        this.species = species;
        this.breed = breed;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.ownerEmail = ownerEmail;
        this.vaccineName = vaccineName;
        this.batchNo = batchNo;
        this.givenOn = givenOn;
        this.nextDueOn = nextDueOn;
        this.daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), nextDueOn);
        this.overdue = this.daysRemaining < 0;
    }

    public Long getVaccinationId() { return vaccinationId; }
    public void setVaccinationId(Long vaccinationId) { this.vaccinationId = vaccinationId; }
    public Long getAnimalId() { return animalId; }
    public void setAnimalId(Long animalId) { this.animalId = animalId; }
    public String getAnimalName() { return animalName; }
    public void setAnimalName(String animalName) { this.animalName = animalName; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail; }
    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public LocalDate getGivenOn() { return givenOn; }
    public void setGivenOn(LocalDate givenOn) { this.givenOn = givenOn; }
    public LocalDate getNextDueOn() { return nextDueOn; }
    public void setNextDueOn(LocalDate nextDueOn) { this.nextDueOn = nextDueOn; }
    public long getDaysRemaining() { return daysRemaining; }
    public void setDaysRemaining(long daysRemaining) { this.daysRemaining = daysRemaining; }
    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }
}
