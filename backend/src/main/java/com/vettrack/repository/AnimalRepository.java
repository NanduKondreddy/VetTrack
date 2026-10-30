package com.vettrack.repository;

import com.vettrack.domain.Animal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, Long> {

    @Query("SELECT a FROM Animal a JOIN FETCH a.owner o WHERE " +
           "(:species IS NULL OR LOWER(a.species) = LOWER(:species)) AND " +
           "(:ownerId IS NULL OR a.owner.id = :ownerId) AND " +
           "(:search IS NULL OR :search = '' OR LOWER(a.name) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Animal> findWithFilters(@Param("species") String species,
                                @Param("ownerId") Long ownerId,
                                @Param("search") String search,
                                Pageable pageable);

    List<Animal> findByOwnerId(Long ownerId);

    @Query("SELECT COUNT(a) FROM Animal a")
    long countTotalAnimals();
}
