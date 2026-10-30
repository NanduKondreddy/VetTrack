package com.vettrack.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "animals")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 50)
    private String species;

    @Column(length = 100)
    private String breed;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, length = 20)
    private String sex;

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visits = new ArrayList<>();

    @OneToMany(mappedBy = "animal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vaccination> vaccinations = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Animal() {}

    public Animal(Owner owner, String name, String species, String breed, LocalDate birthDate, String sex) {
        this.owner = owner;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.sex = sex;
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Owner getOwner() { return owner; }
    public void setOwner(Owner owner) { this.owner = owner; }
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
    public List<Visit> getVisits() { return visits; }
    public void setVisits(List<Visit> visits) { this.visits = visits; }
    public List<Vaccination> getVaccinations() { return vaccinations; }
    public void setVaccinations(List<Vaccination> vaccinations) { this.vaccinations = vaccinations; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
