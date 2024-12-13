package com.mhealth.admin.repository;

import com.mhealth.admin.model.NodLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NodLogRepository extends JpaRepository<NodLog, Integer> {
    @Query("SELECT n FROM NodLog n WHERE " +
            "(:searchId IS NULL OR n.searchId = :searchId) AND " +
            "(:patientName IS NULL OR n.reason LIKE %:patientName%)")
    Page<NodLog> findAllByFilters(
            @Param("searchId") String searchId,
            @Param("patientName") String patientName,
            Pageable pageable
    );
}