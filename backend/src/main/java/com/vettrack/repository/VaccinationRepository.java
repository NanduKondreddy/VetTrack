package com.vettrack.repository;

import com.vettrack.domain.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {

    @Query("SELECT v FROM Vaccination v " +
           "JOIN FETCH v.animal a " +
           "JOIN FETCH a.owner o " +
           "JOIN FETCH v.vaccine vac " +
           "JOIN FETCH v.batch b " +
           "WHERE v.nextDueOn <= :cutoffDate " +
           "ORDER BY v.nextDueOn ASC")
    List<Vaccination> findDueVaccinations(@Param("cutoffDate") LocalDate cutoffDate);

    @Query("SELECT v FROM Vaccination v " +
           "JOIN FETCH v.vaccine vac " +
           "JOIN FETCH v.batch b " +
           "LEFT JOIN FETCH v.visit vis " +
           "WHERE v.animal.id = :animalId " +
           "ORDER BY v.givenOn DESC")
    List<Vaccination> findByAnimalIdOrderByGivenOnDesc(@Param("animalId") Long animalId);

    @Query("SELECT COUNT(v) FROM Vaccination v WHERE v.nextDueOn < :today")
    long countOverdue(@Param("today") LocalDate today);

    @Query("SELECT COUNT(v) FROM Vaccination v WHERE v.givenOn BETWEEN :startDate AND :endDate")
    long countGivenBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
