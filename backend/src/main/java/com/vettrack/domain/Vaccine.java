package com.vettrack.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vaccines")
public class Vaccine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(name = "species_target", nullable = false, length = 100)
    private String speciesTarget;

    @Column(name = "interval_days", nullable = false)
    private Integer intervalDays = 365;

    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccineBatch> batches = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public Vaccine() {}

    public Vaccine(String name, String speciesTarget, Integer intervalDays) {
        this.name = name;
        this.speciesTarget = speciesTarget;
        this.intervalDays = intervalDays;
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpeciesTarget() { return speciesTarget; }
    public void setSpeciesTarget(String speciesTarget) { this.speciesTarget = speciesTarget; }
    public Integer getIntervalDays() { return intervalDays; }
    public void setIntervalDays(Integer intervalDays) { this.intervalDays = intervalDays; }
    public List<VaccineBatch> getBatches() { return batches; }
    public void setBatches(List<VaccineBatch> batches) { this.batches = batches; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
