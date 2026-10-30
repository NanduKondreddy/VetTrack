package com.vettrack.controller;

import com.vettrack.dto.response.DueVaccinationResponse;
import com.vettrack.service.VaccinationService;
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
@RequestMapping("/api/v1/vaccinations")
@Tag(name = "Vaccinations", description = "Endpoints for vaccination tracking and reminder schedules")
@SecurityRequirement(name = "bearerAuth")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    @GetMapping("/due")
    @Operation(summary = "Get list of due and overdue vaccinations with owner contact info")
    public ResponseEntity<List<DueVaccinationResponse>> getDueVaccinations(
            @RequestParam(defaultValue = "14") int withinDays) {
        return ResponseEntity.ok(vaccinationService.getDueVaccinations(withinDays));
    }
}
