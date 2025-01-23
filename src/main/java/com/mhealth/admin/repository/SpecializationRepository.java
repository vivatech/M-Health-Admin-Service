package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.Specialization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SpecializationRepository extends JpaRepository<Specialization, Integer> {
    @Query("Select u from Specialization u where u.status = ?1")
    List<Specialization> findAllByStatus(StatusAI a);

    @Query("SELECT s FROM Specialization s WHERE "
            + "(:name IS NULL OR s.name LIKE %:name%) AND "
            + "(:status IS NULL OR s.status = :status)")
    Page<Specialization> findAllByFilters(@Param("name") String name,
                                          @Param("status") StatusAI statusAI,
                                          Pageable pageable);


    @Query("Select u from Specialization u where u.name = :name")
    Optional<Specialization> findByName(@Param("name") String name);

    @Query("Select u from Specialization u where u.name LIKE %:name% AND (:status is null or u.status = :status)")
    Page<Specialization> findByNameContainingAndStatus(
            @Param(("name")) String name,
            @Param("status") StatusAI statusAI,
            Pageable pageable);

    List<Specialization> findByStatus(StatusAI status);
}