package com.vettrack.service;

import com.vettrack.domain.Animal;
import com.vettrack.dto.response.DashboardSummaryResponse;
import com.vettrack.repository.AnimalRepository;
import com.vettrack.repository.OwnerRepository;
import com.vettrack.repository.VaccinationRepository;
import com.vettrack.repository.VaccineBatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final OwnerRepository ownerRepository;
    private final AnimalRepository animalRepository;
    private final VaccinationRepository vaccinationRepository;
    private final VaccineBatchRepository batchRepository;

    public ReportService(OwnerRepository ownerRepository,
                         AnimalRepository animalRepository,
                         VaccinationRepository vaccinationRepository,
                         VaccineBatchRepository batchRepository) {
        this.ownerRepository = ownerRepository;
        this.animalRepository = animalRepository;
        this.vaccinationRepository = vaccinationRepository;
        this.batchRepository = batchRepository;
    }

    public DashboardSummaryResponse getDashboardSummary() {
        long totalOwners = ownerRepository.count();
        long totalAnimals = animalRepository.countTotalAnimals();

        LocalDate today = LocalDate.now();
        YearMonth currentMonth = YearMonth.now();
        LocalDate startOfMonth = currentMonth.atDay(1);
        LocalDate endOfMonth = currentMonth.atEndOfMonth();

        long vaccinationsThisMonth = vaccinationRepository.countGivenBetween(startOfMonth, endOfMonth);
        long overdueVaccinations = vaccinationRepository.countOverdue(today);
        long expiringBatches = batchRepository.countExpiringBatches(today.plusDays(30));

        Map<String, Long> speciesBreakdown = animalRepository.findAll().stream()
                .collect(Collectors.groupingBy(Animal::getSpecies, Collectors.counting()));

        return new DashboardSummaryResponse(
                totalOwners,
                totalAnimals,
                vaccinationsThisMonth,
                overdueVaccinations,
                expiringBatches,
                speciesBreakdown
        );
    }
}
