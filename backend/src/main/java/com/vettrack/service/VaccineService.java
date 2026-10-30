package com.vettrack.service;

import com.vettrack.domain.Vaccine;
import com.vettrack.dto.response.VaccineBatchResponse;
import com.vettrack.dto.response.VaccineResponse;
import com.vettrack.repository.VaccineBatchRepository;
import com.vettrack.repository.VaccineRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class VaccineService {

    private final VaccineRepository vaccineRepository;
    private final VaccineBatchRepository batchRepository;

    public VaccineService(VaccineRepository vaccineRepository, VaccineBatchRepository batchRepository) {
        this.vaccineRepository = vaccineRepository;
        this.batchRepository = batchRepository;
    }

    public List<VaccineResponse> getAllVaccines() {
        return vaccineRepository.findAll().stream().map(vaccine -> {
            List<VaccineBatchResponse> batchDtos = vaccine.getBatches().stream().map(b ->
                    new VaccineBatchResponse(
                            b.getId(),
                            vaccine.getId(),
                            vaccine.getName(),
                            b.getBatchNo(),
                            b.getExpiryDate(),
                            b.getQuantityRemaining()
                    )
            ).toList();

            return new VaccineResponse(
                    vaccine.getId(),
                    vaccine.getName(),
                    vaccine.getSpeciesTarget(),
                    vaccine.getIntervalDays(),
                    batchDtos
            );
        }).toList();
    }

    public List<VaccineBatchResponse> getExpiringBatches(int withinDays) {
        LocalDate cutoff = LocalDate.now().plusDays(withinDays);
        return batchRepository.findExpiringBatches(cutoff).stream().map(b ->
                new VaccineBatchResponse(
                        b.getId(),
                        b.getVaccine().getId(),
                        b.getVaccine().getName(),
                        b.getBatchNo(),
                        b.getExpiryDate(),
                        b.getQuantityRemaining()
                )
        ).toList();
    }
}
