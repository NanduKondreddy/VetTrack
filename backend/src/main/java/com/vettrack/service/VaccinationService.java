package com.vettrack.service;

import com.vettrack.domain.Animal;
import com.vettrack.domain.Vaccination;
import com.vettrack.domain.Vaccine;
import com.vettrack.domain.VaccineBatch;
import com.vettrack.domain.Visit;
import com.vettrack.dto.request.VaccinationCreateRequest;
import com.vettrack.dto.response.DueVaccinationResponse;
import com.vettrack.dto.response.VaccinationResponse;
import com.vettrack.exception.BatchExpiredException;
import com.vettrack.exception.BatchOutOfStockException;
import com.vettrack.exception.BusinessRuleException;
import com.vettrack.exception.ResourceNotFoundException;
import com.vettrack.repository.AnimalRepository;
import com.vettrack.repository.VaccinationRepository;
import com.vettrack.repository.VaccineBatchRepository;
import com.vettrack.repository.VaccineRepository;
import com.vettrack.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class VaccinationService {

    private static final Logger log = LoggerFactory.getLogger(VaccinationService.class);

    private final VaccinationRepository vaccinationRepository;
    private final AnimalRepository animalRepository;
    private final VaccineRepository vaccineRepository;
    private final VaccineBatchRepository batchRepository;
    private final VisitRepository visitRepository;

    public VaccinationService(VaccinationRepository vaccinationRepository,
                              AnimalRepository animalRepository,
                              VaccineRepository vaccineRepository,
                              VaccineBatchRepository batchRepository,
                              VisitRepository visitRepository) {
        this.vaccinationRepository = vaccinationRepository;
        this.animalRepository = animalRepository;
        this.vaccineRepository = vaccineRepository;
        this.batchRepository = batchRepository;
        this.visitRepository = visitRepository;
    }

    public VaccinationResponse recordVaccination(Long animalId, VaccinationCreateRequest request) {
        log.info("Initiating vaccination record for animalId: {}, vaccineId: {}, batchId: {}",
                animalId, request.getVaccineId(), request.getBatchId());

        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + animalId));

        Vaccine vaccine = vaccineRepository.findById(request.getVaccineId())
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine not found with id: " + request.getVaccineId()));

        VaccineBatch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Vaccine batch not found with id: " + request.getBatchId()));

        // Rule 1: Batch must belong to the selected vaccine
        if (!batch.getVaccine().getId().equals(vaccine.getId())) {
            log.warn("Business rule violation: Batch {} does not belong to vaccine {}", batch.getBatchNo(), vaccine.getName());
            throw new BusinessRuleException(String.format("Batch '%s' does not belong to vaccine '%s'", batch.getBatchNo(), vaccine.getName()));
        }

        // Rule 2: Batch must not be expired at administration time
        if (batch.isExpired(request.getGivenOn())) {
            log.warn("Business rule rejection: Batch {} expired on {} (attempted admin on {})",
                    batch.getBatchNo(), batch.getExpiryDate(), request.getGivenOn());
            throw new BatchExpiredException(String.format(
                    "Cannot administer expired batch '%s'. Expiry date was %s, but given date is %s.",
                    batch.getBatchNo(), batch.getExpiryDate(), request.getGivenOn()));
        }

        // Rule 3: Batch must have available stock
        if (batch.isOutOfStock()) {
            log.warn("Business rule rejection: Batch {} has zero remaining stock", batch.getBatchNo());
            throw new BatchOutOfStockException(String.format("Batch '%s' is out of stock (remaining quantity: 0).", batch.getBatchNo()));
        }

        // Calculate next due date if not provided
        LocalDate nextDueOn = request.getNextDueOn();
        if (nextDueOn == null) {
            int interval = (vaccine.getIntervalDays() != null && vaccine.getIntervalDays() > 0) ? vaccine.getIntervalDays() : 365;
            nextDueOn = request.getGivenOn().plusDays(interval);
        }

        // Rule 4: next_due_on must be >= given_on
        if (nextDueOn.isBefore(request.getGivenOn())) {
            log.warn("Business rule rejection: nextDueOn ({}) cannot precede givenOn ({})", nextDueOn, request.getGivenOn());
            throw new BusinessRuleException("Next due date cannot be earlier than given date.");
        }

        // Optional visit reference
        Visit visit = null;
        if (request.getVisitId() != null) {
            visit = visitRepository.findById(request.getVisitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Visit not found with id: " + request.getVisitId()));
        }

        // Decrement stock within this transaction
        batch.decrementStock();
        batchRepository.save(batch);

        // Record vaccination
        Vaccination vaccination = new Vaccination(animal, vaccine, batch, request.getGivenOn(), nextDueOn, visit);
        Vaccination saved = vaccinationRepository.save(vaccination);

        log.info("Vaccination successfully recorded. ID: {}, Remaining batch stock: {}",
                saved.getId(), batch.getQuantityRemaining());

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<VaccinationResponse> getAnimalVaccinations(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal not found with id: " + animalId);
        }
        return vaccinationRepository.findByAnimalIdOrderByGivenOnDesc(animalId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DueVaccinationResponse> getDueVaccinations(int withinDays) {
        LocalDate cutoff = LocalDate.now().plusDays(withinDays);
        return vaccinationRepository.findDueVaccinations(cutoff).stream().map(v ->
                new DueVaccinationResponse(
                        v.getId(),
                        v.getAnimal().getId(),
                        v.getAnimal().getName(),
                        v.getAnimal().getSpecies(),
                        v.getAnimal().getBreed(),
                        v.getAnimal().getOwner().getId(),
                        v.getAnimal().getOwner().getName(),
                        v.getAnimal().getOwner().getPhone(),
                        v.getAnimal().getOwner().getEmail(),
                        v.getVaccine().getName(),
                        v.getBatch().getBatchNo(),
                        v.getGivenOn(),
                        v.getNextDueOn()
                )
        ).toList();
    }

    private VaccinationResponse mapToResponse(Vaccination v) {
        Long visitId = v.getVisit() != null ? v.getVisit().getId() : null;
        return new VaccinationResponse(
                v.getId(),
                v.getAnimal().getId(),
                v.getAnimal().getName(),
                v.getVaccine().getId(),
                v.getVaccine().getName(),
                v.getBatch().getId(),
                v.getBatch().getBatchNo(),
                v.getGivenOn(),
                v.getNextDueOn(),
                visitId,
                v.getCreatedAt()
        );
    }
}
