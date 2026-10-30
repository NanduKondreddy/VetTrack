package com.vettrack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class AnimalCreateRequest {

    @NotNull(message = "Owner ID is required")
    private Long ownerId;

    @NotBlank(message = "Animal name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "Species is required")
    @Size(max = 50, message = "Species must not exceed 50 characters")
    private String species;

    @Size(max = 100, message = "Breed must not exceed 100 characters")
    private String breed;

    @NotNull(message = "Birth date is required")
    @PastOrPresent(message = "Birth date cannot be in the future")
    private LocalDate birthDate;

    @NotBlank(message = "Sex is required")
    @Size(max = 20, message = "Sex must not exceed 20 characters")
    private String sex;

    public AnimalCreateRequest() {}

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
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
}
