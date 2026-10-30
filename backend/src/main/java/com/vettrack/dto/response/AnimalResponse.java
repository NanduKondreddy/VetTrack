package com.vettrack.dto.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class AnimalResponse {
    private Long id;
    private Long ownerId;
    private String ownerName;
    private String ownerPhone;
    private String name;
    private String species;
    private String breed;
    private LocalDate birthDate;
    private String sex;
    private OffsetDateTime createdAt;

    public AnimalResponse() {}

    public AnimalResponse(Long id, Long ownerId, String ownerName, String ownerPhone, String name,
                          String species, String breed, LocalDate birthDate, String sex, OffsetDateTime createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.sex = sex;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
