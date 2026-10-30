package com.vettrack.repository;

import com.vettrack.domain.ImportError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportErrorRepository extends JpaRepository<ImportError, Long> {
    List<ImportError> findBySourceFileOrderByRowNumberAsc(String sourceFile);
    long countBySourceFile(String sourceFile);
}
