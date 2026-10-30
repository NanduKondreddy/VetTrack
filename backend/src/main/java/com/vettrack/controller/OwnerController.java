package com.vettrack.controller;

import com.vettrack.dto.request.OwnerCreateRequest;
import com.vettrack.dto.response.OwnerResponse;
import com.vettrack.dto.response.PagedResponse;
import com.vettrack.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/owners")
@Tag(name = "Owners", description = "Endpoints for client and animal owner management")
@SecurityRequirement(name = "bearerAuth")
public class OwnerController {

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @GetMapping
    @Operation(summary = "Search and list animal owners with pagination")
    public ResponseEntity<PagedResponse<OwnerResponse>> getOwners(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ownerService.getOwners(query, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get owner by ID")
    public ResponseEntity<OwnerResponse> getOwnerById(@PathVariable Long id) {
        return ResponseEntity.ok(ownerService.getOwnerById(id));
    }

    @PostMapping
    @Operation(summary = "Register new animal owner")
    public ResponseEntity<OwnerResponse> createOwner(@Valid @RequestBody OwnerCreateRequest request) {
        OwnerResponse response = ownerService.createOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing animal owner")
    public ResponseEntity<OwnerResponse> updateOwner(@PathVariable Long id, @Valid @RequestBody OwnerCreateRequest request) {
        return ResponseEntity.ok(ownerService.updateOwner(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete owner (ADMIN only)")
    public ResponseEntity<Void> deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return ResponseEntity.noContent().build();
    }
}
