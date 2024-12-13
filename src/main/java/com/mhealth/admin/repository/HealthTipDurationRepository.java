package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.HealthTipDuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthTipDurationRepository extends JpaRepository<HealthTipDuration,Integer> {
    @Query("Select u from HealthTipDuration u where " +
            " (:durationName is null or u.durationName LIKE %:durationName%) AND " +
            " (:status is null OR u.status = :status)")
    Page<HealthTipDuration> findByDurationNameContainingAndStatus(
            @Param("durationName") String durationName,
            @Param("status") StatusAI status, Pageable pageable);
}
