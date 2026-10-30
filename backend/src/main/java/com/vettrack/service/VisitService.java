package com.vettrack.service;

import com.vettrack.domain.Animal;
import com.vettrack.domain.Vet;
import com.vettrack.domain.Visit;
import com.vettrack.dto.request.VisitCreateRequest;
import com.vettrack.dto.response.VisitResponse;
import com.vettrack.exception.ResourceNotFoundException;
import com.vettrack.repository.AnimalRepository;
import com.vettrack.repository.VetRepository;
import com.vettrack.repository.VisitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class VisitService {

    private static final Logger log = LoggerFactory.getLogger(VisitService.class);

    private final VisitRepository visitRepository;
    private final AnimalRepository animalRepository;
    private final VetRepository vetRepository;

    public VisitService(VisitRepository visitRepository, AnimalRepository animalRepository, VetRepository vetRepository) {
        this.visitRepository = visitRepository;
        this.animalRepository = animalRepository;
        this.vetRepository = vetRepository;
    }

    @Transactional(readOnly = true)
    public List<VisitResponse> getAnimalVisits(Long animalId) {
        if (!animalRepository.existsById(animalId)) {
            throw new ResourceNotFoundException("Animal not found with id: " + animalId);
        }
        return visitRepository.findByAnimalIdOrderByVisitDateDesc(animalId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public VisitResponse createVisit(Long animalId, VisitCreateRequest request) {
        log.info("Recording visit for animalId: {}", animalId);
        Animal animal = animalRepository.findById(animalId)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + animalId));

        Vet vet = null;
        if (request.getVetId() != null) {
            vet = vetRepository.findById(request.getVetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vet not found with id: " + request.getVetId()));
        }

        Visit visit = new Visit(animal, vet, request.getVisitDate(), request.getReason(), request.getNotes());
        Visit saved = visitRepository.save(visit);
        return mapToResponse(saved);
    }

    private VisitResponse mapToResponse(Visit visit) {
        Long vetId = visit.getVet() != null ? visit.getVet().getId() : null;
        String vetName = visit.getVet() != null ? visit.getVet().getName() : null;
        return new VisitResponse(
                visit.getId(),
                visit.getAnimal().getId(),
                vetId,
                vetName,
                visit.getVisitDate(),
                visit.getReason(),
                visit.getNotes(),
                visit.getCreatedAt()
        );
    }
}
