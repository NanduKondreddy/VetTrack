package com.vettrack.dto.response;

import java.util.Map;

public class DashboardSummaryResponse {
    private long totalOwners;
    private long totalAnimals;
    private long vaccinationsThisMonth;
    private long overdueVaccinations;
    private long expiringBatchesCount;
    private Map<String, Long> speciesBreakdown;

    public DashboardSummaryResponse() {}

    public DashboardSummaryResponse(long totalOwners, long totalAnimals, long vaccinationsThisMonth,
                                    long overdueVaccinations, long expiringBatchesCount, Map<String, Long> speciesBreakdown) {
        this.totalOwners = totalOwners;
        this.totalAnimals = totalAnimals;
        this.vaccinationsThisMonth = vaccinationsThisMonth;
        this.overdueVaccinations = overdueVaccinations;
        this.expiringBatchesCount = expiringBatchesCount;
        this.speciesBreakdown = speciesBreakdown;
    }

    public long getTotalOwners() { return totalOwners; }
    public void setTotalOwners(long totalOwners) { this.totalOwners = totalOwners; }
    public long getTotalAnimals() { return totalAnimals; }
    public void setTotalAnimals(long totalAnimals) { this.totalAnimals = totalAnimals; }
    public long getVaccinationsThisMonth() { return vaccinationsThisMonth; }
    public void setVaccinationsThisMonth(long vaccinationsThisMonth) { this.vaccinationsThisMonth = vaccinationsThisMonth; }
    public long getOverdueVaccinations() { return overdueVaccinations; }
    public void setOverdueVaccinations(long overdueVaccinations) { this.overdueVaccinations = overdueVaccinations; }
    public long getExpiringBatchesCount() { return expiringBatchesCount; }
    public void setExpiringBatchesCount(long expiringBatchesCount) { this.expiringBatchesCount = expiringBatchesCount; }
    public Map<String, Long> getSpeciesBreakdown() { return speciesBreakdown; }
    public void setSpeciesBreakdown(Map<String, Long> speciesBreakdown) { this.speciesBreakdown = speciesBreakdown; }
}
