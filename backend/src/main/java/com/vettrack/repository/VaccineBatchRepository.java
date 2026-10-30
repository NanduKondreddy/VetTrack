package com.vettrack.repository;

import com.vettrack.domain.VaccineBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VaccineBatchRepository extends JpaRepository<VaccineBatch, Long> {

    @Query("SELECT b FROM VaccineBatch b JOIN FETCH b.vaccine v " +
           "WHERE b.expiryDate <= :cutoffDate AND b.quantityRemaining > 0 " +
           "ORDER BY b.expiryDate ASC")
    List<VaccineBatch> findExpiringBatches(@Param("cutoffDate") LocalDate cutoffDate);

    @Query("SELECT b FROM VaccineBatch b JOIN FETCH b.vaccine v WHERE b.vaccine.id = :vaccineId ORDER BY b.expiryDate ASC")
    List<VaccineBatch> findByVaccineId(@Param("vaccineId") Long vaccineId);

    Optional<VaccineBatch> findByVaccineIdAndBatchNo(Long vaccineId, String batchNo);

    @Query("SELECT COUNT(b) FROM VaccineBatch b WHERE b.expiryDate <= :cutoffDate AND b.quantityRemaining > 0")
    long countExpiringBatches(@Param("cutoffDate") LocalDate cutoffDate);
}
