package com.vettrack.controller;

import com.vettrack.dto.response.DashboardSummaryResponse;
import com.vettrack.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Endpoints for platform reporting and dashboard analytics")
@SecurityRequirement(name = "bearerAuth")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Get aggregated summary statistics for clinic dashboard")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary() {
        return ResponseEntity.ok(reportService.getDashboardSummary());
    }
}
