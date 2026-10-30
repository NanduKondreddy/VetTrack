package com.vettrack.service;

import com.vettrack.domain.Animal;
import com.vettrack.domain.Owner;
import com.vettrack.domain.Vaccination;
import com.vettrack.domain.Vaccine;
import com.vettrack.domain.VaccineBatch;
import com.vettrack.dto.request.VaccinationCreateRequest;
import com.vettrack.dto.response.VaccinationResponse;
import com.vettrack.exception.BatchExpiredException;
import com.vettrack.exception.BatchOutOfStockException;
import com.vettrack.exception.BusinessRuleException;
import com.vettrack.repository.AnimalRepository;
import com.vettrack.repository.VaccinationRepository;
import com.vettrack.repository.VaccineBatchRepository;
import com.vettrack.repository.VaccineRepository;
import com.vettrack.repository.VisitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VaccinationServiceTest {

    @Mock
    private VaccinationRepository vaccinationRepository;

    @Mock
    private AnimalRepository animalRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private VaccineBatchRepository batchRepository;

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VaccinationService vaccinationService;

    private Animal testAnimal;
    private Vaccine testVaccine;
    private VaccineBatch testBatch;

    @BeforeEach
    void setUp() {
        Owner testOwner = new Owner("Test Owner", "+1-555-0101", "owner@test.com", "Austin");
        testOwner.setId(1L);

        testAnimal = new Animal(testOwner, "Buddy", "Dog", "Golden Retriever", LocalDate.now().minusYears(2), "Male");
        testAnimal.setId(10L);

        testVaccine = new Vaccine("Rabies Core", "Dog", 365);
        testVaccine.setId(100L);

        testBatch = new VaccineBatch(testVaccine, "RAB-BATCH-01", LocalDate.now().plusMonths(6), 25);
        testBatch.setId(200L);
    }

    @Test
    @DisplayName("Should successfully record vaccination and decrement stock")
    void recordVaccination_Success_DecrementsStock() {
        VaccinationCreateRequest request = new VaccinationCreateRequest(
                100L, 200L, LocalDate.now(), null, null
        );

        when(animalRepository.findById(10L)).thenReturn(Optional.of(testAnimal));
        when(vaccineRepository.findById(100L)).thenReturn(Optional.of(testVaccine));
        when(batchRepository.findById(200L)).thenReturn(Optional.of(testBatch));
        when(vaccinationRepository.save(any(Vaccination.class))).thenAnswer(invocation -> {
            Vaccination v = invocation.getArgument(0);
            v.setId(999L);
            return v;
        });

        VaccinationResponse response = vaccinationService.recordVaccination(10L, request);

        assertNotNull(response);
        assertEquals(999L, response.getId());
        assertEquals("Buddy", response.getAnimalName());
        assertEquals("Rabies Core", response.getVaccineName());
        assertEquals("RAB-BATCH-01", response.getBatchNo());
        // Check stock was decremented from 25 to 24
        assertEquals(24, testBatch.getQuantityRemaining());
        // Check calculated next due date (today + 365 days)
        assertEquals(LocalDate.now().plusDays(365), response.getNextDueOn());

        verify(batchRepository).save(testBatch);
        verify(vaccinationRepository).save(any(Vaccination.class));
    }

    @Test
    @DisplayName("Should reject vaccination when batch is expired")
    void recordVaccination_FailsWhenBatchExpired() {
        testBatch.setExpiryDate(LocalDate.now().minusDays(5)); // Expired 5 days ago

        VaccinationCreateRequest request = new VaccinationCreateRequest(
                100L, 200L, LocalDate.now(), null, null
        );

        when(animalRepository.findById(10L)).thenReturn(Optional.of(testAnimal));
        when(vaccineRepository.findById(100L)).thenReturn(Optional.of(testVaccine));
        when(batchRepository.findById(200L)).thenReturn(Optional.of(testBatch));

        assertThrows(BatchExpiredException.class, () ->
                vaccinationService.recordVaccination(10L, request)
        );

        verify(batchRepository, never()).save(any());
        verify(vaccinationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject vaccination when batch stock is zero")
    void recordVaccination_FailsWhenBatchOutOfStock() {
        testBatch.setQuantityRemaining(0); // Zero stock

        VaccinationCreateRequest request = new VaccinationCreateRequest(
                100L, 200L, LocalDate.now(), null, null
        );

        when(animalRepository.findById(10L)).thenReturn(Optional.of(testAnimal));
        when(vaccineRepository.findById(100L)).thenReturn(Optional.of(testVaccine));
        when(batchRepository.findById(200L)).thenReturn(Optional.of(testBatch));

        assertThrows(BatchOutOfStockException.class, () ->
                vaccinationService.recordVaccination(10L, request)
        );

        verify(batchRepository, never()).save(any());
        verify(vaccinationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject vaccination when batch belongs to different vaccine")
    void recordVaccination_FailsWhenBatchVaccineMismatch() {
        Vaccine otherVaccine = new Vaccine("Feline Distemper", "Cat", 180);
        otherVaccine.setId(555L);
        testBatch.setVaccine(otherVaccine); // Mismatched vaccine

        VaccinationCreateRequest request = new VaccinationCreateRequest(
                100L, 200L, LocalDate.now(), null, null
        );

        when(animalRepository.findById(10L)).thenReturn(Optional.of(testAnimal));
        when(vaccineRepository.findById(100L)).thenReturn(Optional.of(testVaccine));
        when(batchRepository.findById(200L)).thenReturn(Optional.of(testBatch));

        assertThrows(BusinessRuleException.class, () ->
                vaccinationService.recordVaccination(10L, request)
        );

        verify(vaccinationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should reject vaccination when next due date precedes given date")
    void recordVaccination_FailsWhenNextDueDateBeforeGivenDate() {
        VaccinationCreateRequest request = new VaccinationCreateRequest(
                100L, 200L, LocalDate.now(), LocalDate.now().minusDays(10), null
        );

        when(animalRepository.findById(10L)).thenReturn(Optional.of(testAnimal));
        when(vaccineRepository.findById(100L)).thenReturn(Optional.of(testVaccine));
        when(batchRepository.findById(200L)).thenReturn(Optional.of(testBatch));

        assertThrows(BusinessRuleException.class, () ->
                vaccinationService.recordVaccination(10L, request)
        );

        verify(vaccinationRepository, never()).save(any());
    }
}
