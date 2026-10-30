package com.vettrack.service;

import com.vettrack.domain.Owner;
import com.vettrack.dto.request.OwnerCreateRequest;
import com.vettrack.dto.response.OwnerResponse;
import com.vettrack.dto.response.PagedResponse;
import com.vettrack.exception.ResourceNotFoundException;
import com.vettrack.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OwnerServiceTest {

    @Mock
    private OwnerRepository ownerRepository;

    @InjectMocks
    private OwnerService ownerService;

    private Owner testOwner;

    @BeforeEach
    void setUp() {
        testOwner = new Owner("Jane Doe", "+1-555-4321", "jane@example.com", "Dallas");
        testOwner.setId(1L);
    }

    @Test
    @DisplayName("Should create owner successfully")
    void createOwner_Success() {
        OwnerCreateRequest request = new OwnerCreateRequest("Jane Doe", "+1-555-4321", "jane@example.com", "Dallas");
        when(ownerRepository.save(any(Owner.class))).thenReturn(testOwner);

        OwnerResponse response = ownerService.createOwner(request);

        assertNotNull(response);
        assertEquals("Jane Doe", response.getName());
        assertEquals("Dallas", response.getCity());
        verify(ownerRepository).save(any(Owner.class));
    }

    @Test
    @DisplayName("Should get owner by ID")
    void getOwnerById_Success() {
        when(ownerRepository.findById(1L)).thenReturn(Optional.of(testOwner));

        OwnerResponse response = ownerService.getOwnerById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Jane Doe", response.getName());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when owner ID not found")
    void getOwnerById_NotFound() {
        when(ownerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ownerService.getOwnerById(999L));
    }

    @Test
    @DisplayName("Should list owners with pagination and search")
    void getOwners_Paged() {
        Pageable pageable = PageRequest.of(0, 10);
        when(ownerRepository.searchOwners("Jane", pageable))
                .thenReturn(new PageImpl<>(List.of(testOwner), pageable, 1));

        PagedResponse<OwnerResponse> pagedResponse = ownerService.getOwners("Jane", pageable);

        assertNotNull(pagedResponse);
        assertEquals(1, pagedResponse.getTotalElements());
        assertEquals("Jane Doe", pagedResponse.getContent().get(0).getName());
    }
}
