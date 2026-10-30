package com.vettrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vettrack.dto.request.VaccinationCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VaccinationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("GET /api/v1/vaccinations/due returns list of upcoming and overdue vaccinations")
    void getDueVaccinations_ReturnsList() throws Exception {
        mockMvc.perform(get("/api/v1/vaccinations/due?withinDays=30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("POST /api/v1/animals/{id}/vaccinations with expired batch returns 400 Bad Request")
    void recordVaccination_ExpiredBatch_Returns400() throws Exception {
        // Batch 4 is DHPP-2025-C2, expired on 2026-10-10
        VaccinationCreateRequest request = new VaccinationCreateRequest(
                2L, 4L, LocalDate.of(2026, 10, 20), null, null
        );

        mockMvc.perform(post("/api/v1/animals/1/vaccinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Batch Expired"));
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("GET /api/v1/reports/summary returns clinic statistics")
    void getDashboardSummary_ReturnsData() throws Exception {
        mockMvc.perform(get("/api/v1/reports/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOwners").isNumber())
                .andExpect(jsonPath("$.totalAnimals").isNumber())
                .andExpect(jsonPath("$.speciesBreakdown").isMap());
    }
}
