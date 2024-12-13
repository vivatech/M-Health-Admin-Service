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
            "AND (:status IS NULL OR c.status = :status)")
    Page<HealthTipCategoryMaster> findByNameContainingAndStatus(@Param("name") String name,
                                                                @Param("status") StatusAI status,
                                                                Pageable pageable);
}