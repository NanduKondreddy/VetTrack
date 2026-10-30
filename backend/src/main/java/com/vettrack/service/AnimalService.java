package com.vettrack.service;

import com.vettrack.domain.Animal;
import com.vettrack.domain.Owner;
import com.vettrack.dto.request.AnimalCreateRequest;
import com.vettrack.dto.response.AnimalResponse;
import com.vettrack.dto.response.PagedResponse;
import com.vettrack.exception.ResourceNotFoundException;
import com.vettrack.repository.AnimalRepository;
import com.vettrack.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnimalService {

    private static final Logger log = LoggerFactory.getLogger(AnimalService.class);

    private final AnimalRepository animalRepository;
    private final OwnerRepository ownerRepository;

    public AnimalService(AnimalRepository animalRepository, OwnerRepository ownerRepository) {
        this.animalRepository = animalRepository;
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<AnimalResponse> getAnimals(String species, Long ownerId, String search, Pageable pageable) {
        Page<Animal> page = animalRepository.findWithFilters(species, ownerId, search, pageable);
        return new PagedResponse<>(
                page.getContent().stream().map(this::mapToResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Transactional(readOnly = true)
    public AnimalResponse getAnimalById(Long id) {
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));
        return mapToResponse(animal);
    }

    public AnimalResponse createAnimal(AnimalCreateRequest request) {
        log.info("Creating animal: {} for ownerId: {}", request.getName(), request.getOwnerId());
        Owner owner = ownerRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));

        Animal animal = new Animal(
                owner,
                request.getName(),
                request.getSpecies(),
                request.getBreed(),
                request.getBirthDate(),
                request.getSex()
        );

        Animal saved = animalRepository.save(animal);
        return mapToResponse(saved);
    }

    public AnimalResponse updateAnimal(Long id, AnimalCreateRequest request) {
        log.info("Updating animal id: {}", id);
        Animal animal = animalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Animal not found with id: " + id));

        if (!animal.getOwner().getId().equals(request.getOwnerId())) {
            Owner newOwner = ownerRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + request.getOwnerId()));
            animal.setOwner(newOwner);
        }

        animal.setName(request.getName());
        animal.setSpecies(request.getSpecies());
        animal.setBreed(request.getBreed());
        animal.setBirthDate(request.getBirthDate());
        animal.setSex(request.getSex());

        return mapToResponse(animal);
    }

    private AnimalResponse mapToResponse(Animal animal) {
        return new AnimalResponse(
                animal.getId(),
                animal.getOwner().getId(),
                animal.getOwner().getName(),
                animal.getOwner().getPhone(),
                animal.getName(),
                animal.getSpecies(),
                animal.getBreed(),
                animal.getBirthDate(),
                animal.getSex(),
                animal.getCreatedAt()
        );
    }
}
