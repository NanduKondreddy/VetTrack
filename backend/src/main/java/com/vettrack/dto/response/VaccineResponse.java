package com.vettrack.dto.response;

import java.util.List;

public class VaccineResponse {
    private Long id;
    private String name;
    private String speciesTarget;
    private Integer intervalDays;
    private List<VaccineBatchResponse> batches;

    public VaccineResponse() {}

    public VaccineResponse(Long id, String name, String speciesTarget, Integer intervalDays, List<VaccineBatchResponse> batches) {
        this.id = id;
        this.name = name;
        this.speciesTarget = speciesTarget;
        this.intervalDays = intervalDays;
        this.batches = batches;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpeciesTarget() { return speciesTarget; }
    public void setSpeciesTarget(String speciesTarget) { this.speciesTarget = speciesTarget; }
    public Integer getIntervalDays() { return intervalDays; }
    public void setIntervalDays(Integer intervalDays) { this.intervalDays = intervalDays; }
    public List<VaccineBatchResponse> getBatches() { return batches; }
    public void setBatches(List<VaccineBatchResponse> batches) { this.batches = batches; }
}
