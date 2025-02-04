package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.HealthTipDuration;
import com.mhealth.admin.model.HealthTipPackage;
import com.mhealth.admin.model.HealthTipPackageCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthTipPackageRepository extends JpaRepository<HealthTipPackage,Integer> {
    @Query("SELECT MAX(p.packagePrice) FROM HealthTipPackage p")
    Double findMaxPackagePrice();

    @Query("SELECT p FROM HealthTipPackage p where p.packageId = ?1")
    List<HealthTipPackage> findByPackageId(Integer packageId);

    @Query("SELECT c FROM HealthTipPackageCategories c " +
            "JOIN c.healthTipPackage p " +
            "JOIN c.healthTipCategoryMaster h " +
            "WHERE " +
            "(:name IS NULL OR p.packageName LIKE %:name%) AND " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:durationId IS NULL OR p.healthTipDuration.durationId = :durationId) AND " +
            "(:categoryId IS NULL OR h.categoryId = :categoryId)")
    Page<HealthTipPackageCategories> findByNameAndStatusAndDurationAndCategory(
            @Param("name") String name,
            @Param("status") StatusAI status,
            @Param("durationId") Integer durationId,
            @Param("categoryId") Integer categoryId,
            Pageable pageable);


    @Query("SELECT h FROM HealthTipPackage h " +
            "WHERE (:packageName IS NULL OR :packageName = '' OR LOWER(h.packageName) LIKE LOWER(CONCAT('%', :packageName, '%'))) " +
            "AND (:startDate IS NULL OR h.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR h.createdAt <= :endDate)")
    Page<HealthTipPackage> searchByPackageNameAndCreatedAt(
            @Param("packageName") String packageName,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    List<HealthTipPackage> findByHealthTipDuration(HealthTipDuration healthTipDuration);
}
