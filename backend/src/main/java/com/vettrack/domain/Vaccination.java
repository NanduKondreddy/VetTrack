package com.vettrack.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "vaccinations")
public class Vaccination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false)
    private VaccineBatch batch;

    @Column(name = "given_on", nullable = false)
    private LocalDate givenOn;

    @Column(name = "next_due_on", nullable = false)
    private LocalDate nextDueOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Vaccination() {}

    public Vaccination(Animal animal, Vaccine vaccine, VaccineBatch batch, LocalDate givenOn, LocalDate nextDueOn, Visit visit) {
        this.animal = animal;
        this.vaccine = vaccine;
        this.batch = batch;
        this.givenOn = givenOn;
        this.nextDueOn = nextDueOn;
        this.visit = visit;
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
    public Animal getAnimal() { return animal; }
    public void setAnimal(Animal animal) { this.animal = animal; }
    public Vaccine getVaccine() { return vaccine; }
    public void setVaccine(Vaccine vaccine) { this.vaccine = vaccine; }
    public VaccineBatch getBatch() { return batch; }
    public void setBatch(VaccineBatch batch) { this.batch = batch; }
    public LocalDate getGivenOn() { return givenOn; }
    public void setGivenOn(LocalDate givenOn) { this.givenOn = givenOn; }
    public LocalDate getNextDueOn() { return nextDueOn; }
    public void setNextDueOn(LocalDate nextDueOn) { this.nextDueOn = nextDueOn; }
    public Visit getVisit() { return visit; }
    public void setVisit(Visit visit) { this.visit = visit; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
