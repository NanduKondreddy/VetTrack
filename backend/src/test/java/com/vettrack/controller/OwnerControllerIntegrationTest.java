package com.vettrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vettrack.dto.request.OwnerCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OwnerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/v1/owners without authentication returns 403 or 401")
    void getOwners_Unauthenticated_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/owners"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("GET /api/v1/owners with STAFF role returns 200 and paged list")
    void getOwners_Authenticated_ReturnsPagedList() throws Exception {
        mockMvc.perform(get("/api/v1/owners?page=0&size=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.pageSize").value(5));
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("POST /api/v1/owners creates a new owner")
    void createOwner_Valid_Returns201() throws Exception {
        OwnerCreateRequest request = new OwnerCreateRequest("Integration Test Owner", "+1-555-900-1122", "integration@vet.org", "Waco");

        mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Integration Test Owner"))
                .andExpect(jsonPath("$.city").value("Waco"));
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("DELETE /api/v1/owners/{id} by STAFF is rejected with 403 Forbidden")
    void deleteOwner_StaffRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/owners/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /api/v1/owners/{id} by ADMIN succeeds with 204 No Content")
    void deleteOwner_AdminRole_ReturnsNoContent() throws Exception {
        // Create an owner to delete safely
        OwnerCreateRequest request = new OwnerCreateRequest("Disposable Owner", "+1-555-888-0000", "disposable@vet.org", "Houston");
        String response = mockMvc.perform(post("/api/v1/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long createdId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/v1/owners/" + createdId))
                .andExpect(status().isNoContent());
    }
}
