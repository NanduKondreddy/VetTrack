package com.vettrack.service;

import com.vettrack.domain.Owner;
import com.vettrack.dto.request.OwnerCreateRequest;
import com.vettrack.dto.response.OwnerResponse;
import com.vettrack.dto.response.PagedResponse;
import com.vettrack.exception.ResourceNotFoundException;
import com.vettrack.repository.OwnerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OwnerService {

    private static final Logger log = LoggerFactory.getLogger(OwnerService.class);

    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    @Transactional(readOnly = true)
    public PagedResponse<OwnerResponse> getOwners(String query, Pageable pageable) {
        Page<Owner> page = ownerRepository.searchOwners(query, pageable);
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
    public OwnerResponse getOwnerById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));
        return mapToResponse(owner);
    }

    public OwnerResponse createOwner(OwnerCreateRequest request) {
        log.info("Creating owner: {}", request.getName());
        Owner owner = new Owner(request.getName(), request.getPhone(), request.getEmail(), request.getCity());
        Owner saved = ownerRepository.save(owner);
        return mapToResponse(saved);
    }

    public OwnerResponse updateOwner(Long id, OwnerCreateRequest request) {
        log.info("Updating owner id: {}", id);
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + id));

        owner.setName(request.getName());
        owner.setPhone(request.getPhone());
        owner.setEmail(request.getEmail());
        owner.setCity(request.getCity());

        return mapToResponse(owner);
    }

    public void deleteOwner(Long id) {
        log.info("Deleting owner id: {}", id);
        if (!ownerRepository.existsById(id)) {
            throw new ResourceNotFoundException("Owner not found with id: " + id);
        }
        ownerRepository.deleteById(id);
    }

    private OwnerResponse mapToResponse(Owner owner) {
        int count = owner.getAnimals() != null ? owner.getAnimals().size() : 0;
        return new OwnerResponse(
                owner.getId(),
                owner.getName(),
                owner.getPhone(),
                owner.getEmail(),
                owner.getCity(),
                count,
                owner.getCreatedAt()
        );
    }
}
