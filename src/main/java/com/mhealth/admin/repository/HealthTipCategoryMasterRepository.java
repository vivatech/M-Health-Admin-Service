package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.HealthTipCategoryMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HealthTipCategoryMasterRepository extends JpaRepository<HealthTipCategoryMaster, Integer> {

    @Query("SELECT c FROM HealthTipCategoryMaster c " +
            "WHERE (:name IS NULL OR c.name LIKE %:name%) " +
            "AND (:status IS NULL OR c.status = :status) " +
            "ORDER BY " +
            "CASE WHEN :sortBy = 'priority' AND :sortDirection = 'asc' THEN c.priority END ASC, " +
            "CASE WHEN :sortBy = 'priority' AND :sortDirection = 'desc' THEN c.priority END DESC, " +
            "CASE WHEN :sortBy = 'description' AND :sortDirection = 'asc' THEN c.description END ASC, " +
            "CASE WHEN :sortBy = 'description' AND :sortDirection = 'desc' THEN c.description END DESC, " +
            "CASE WHEN :sortBy = 'name' AND :sortDirection = 'asc' THEN c.name END ASC, " +
            "CASE WHEN :sortBy = 'name' AND :sortDirection = 'desc' THEN c.name END DESC," +
            "c.categoryId DESC")
    Page<HealthTipCategoryMaster> findByNameContainingAndStatus(
            @Param("name") String name,
            @Param("status") StatusAI status,
            @Param("sortBy") String sortBy,
            @Param("sortDirection") String sortDirection,
            Pageable pageable);

}