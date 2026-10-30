package com.vettrack.repository;

import com.vettrack.domain.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    @Query("SELECT o FROM Owner o WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(o.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.phone) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.city) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Owner> searchOwners(@Param("query") String query, Pageable pageable);

    Optional<Owner> findByPhone(String phone);
    Optional<Owner> findByEmailIgnoreCase(String email);
}
