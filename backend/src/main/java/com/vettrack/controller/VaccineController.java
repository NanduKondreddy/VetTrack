package com.vettrack.controller;

import com.vettrack.dto.response.VaccineResponse;
import com.vettrack.service.VaccineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vaccines")
@Tag(name = "Vaccines", description = "Endpoints for vaccine catalog and batch inventory")
@SecurityRequirement(name = "bearerAuth")
public class VaccineController {

    private final VaccineService vaccineService;

    public VaccineController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping
    @Operation(summary = "Get list of all vaccines with their respective batches")
    public ResponseEntity<List<VaccineResponse>> getAllVaccines() {
        return ResponseEntity.ok(vaccineService.getAllVaccines());
    }
}
