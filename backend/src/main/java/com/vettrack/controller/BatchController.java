package com.vettrack.controller;

import com.vettrack.dto.response.VaccineBatchResponse;
import com.vettrack.service.VaccineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/batches")
@Tag(name = "Batches", description = "Endpoints for vaccine inventory and batch expiry management")
@SecurityRequirement(name = "bearerAuth")
public class BatchController {

    private final VaccineService vaccineService;

    public BatchController(VaccineService vaccineService) {
        this.vaccineService = vaccineService;
    }

    @GetMapping("/expiring")
    @Operation(summary = "Get vaccine batches nearing expiration (stock risk)")
    public ResponseEntity<List<VaccineBatchResponse>> getExpiringBatches(
            @RequestParam(defaultValue = "30") int withinDays) {
        return ResponseEntity.ok(vaccineService.getExpiringBatches(withinDays));
    }
}
