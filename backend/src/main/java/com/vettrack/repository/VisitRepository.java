package com.vettrack.repository;

import com.vettrack.domain.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("SELECT v FROM Visit v LEFT JOIN FETCH v.vet WHERE v.animal.id = :animalId ORDER BY v.visitDate DESC")
    List<Visit> findByAnimalIdOrderByVisitDateDesc(@Param("animalId") Long animalId);
}
