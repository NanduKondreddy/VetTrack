package com.vettrack.controller;

import com.vettrack.dto.request.AnimalCreateRequest;
import com.vettrack.dto.request.VaccinationCreateRequest;
import com.vettrack.dto.request.VisitCreateRequest;
import com.vettrack.dto.response.AnimalResponse;
import com.vettrack.dto.response.PagedResponse;
import com.vettrack.dto.response.VaccinationResponse;
import com.vettrack.dto.response.VisitResponse;
import com.vettrack.service.AnimalService;
import com.vettrack.service.VaccinationService;
import com.vettrack.service.VisitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/animals")
@Tag(name = "Animals", description = "Endpoints for managing animal health records, visits, and vaccinations")
@SecurityRequirement(name = "bearerAuth")
public class AnimalController {

    private final AnimalService animalService;
    private final VisitService visitService;
    private final VaccinationService vaccinationService;

    public AnimalController(AnimalService animalService,
                            VisitService visitService,
                            VaccinationService vaccinationService) {
        this.animalService = animalService;
        this.visitService = visitService;
        this.vaccinationService = vaccinationService;
    }

    @GetMapping
    @Operation(summary = "List animals with species and owner filtering and pagination")
    public ResponseEntity<PagedResponse<AnimalResponse>> getAnimals(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) Long ownerId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(animalService.getAnimals(species, ownerId, search, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get animal profile by ID")
    public ResponseEntity<AnimalResponse> getAnimalById(@PathVariable Long id) {
        return ResponseEntity.ok(animalService.getAnimalById(id));
    }

    @PostMapping
    @Operation(summary = "Register new animal")
    public ResponseEntity<AnimalResponse> createAnimal(@Valid @RequestBody AnimalCreateRequest request) {
        AnimalResponse response = animalService.createAnimal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update animal profile")
    public ResponseEntity<AnimalResponse> updateAnimal(@PathVariable Long id, @Valid @RequestBody AnimalCreateRequest request) {
        return ResponseEntity.ok(animalService.updateAnimal(id, request));
    }

    @GetMapping("/{id}/visits")
    @Operation(summary = "Get visit history for an animal")
    public ResponseEntity<List<VisitResponse>> getAnimalVisits(@PathVariable Long id) {
        return ResponseEntity.ok(visitService.getAnimalVisits(id));
    }

    @PostMapping("/{id}/visits")
    @Operation(summary = "Record new clinic visit for an animal")
    public ResponseEntity<VisitResponse> createVisit(@PathVariable Long id, @Valid @RequestBody VisitCreateRequest request) {
        VisitResponse response = visitService.createVisit(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}/vaccinations")
    @Operation(summary = "Get vaccination history for an animal")
    public ResponseEntity<List<VaccinationResponse>> getAnimalVaccinations(@PathVariable Long id) {
        return ResponseEntity.ok(vaccinationService.getAnimalVaccinations(id));
    }

    @PostMapping("/{id}/vaccinations")
    @Operation(summary = "Administer and record vaccination, validating and decrementing batch stock")
    public ResponseEntity<VaccinationResponse> recordVaccination(
            @PathVariable Long id,
            @Valid @RequestBody VaccinationCreateRequest request) {
        VaccinationResponse response = vaccinationService.recordVaccination(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
