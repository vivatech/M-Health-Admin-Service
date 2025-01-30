package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.model.LabSubCategoryMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LabSubCategoryMasterRepository extends JpaRepository<LabSubCategoryMaster, Integer> {
    @Query("SELECT u FROM LabSubCategoryMaster u WHERE u.subCatName = :subCatName")
    Optional<LabSubCategoryMaster> findBySubCatName(@Param("subCatName") String subCatName);

    @Query("SELECT u FROM LabSubCategoryMaster u " +
            "WHERE (:subCatName IS NULL OR u.subCatName LIKE %:subCatName%) " +
            "AND (:catId IS NULL OR u.labCategory.catId = :catId) " +
            "AND (:subCatStatus IS NULL OR u.subCatStatus = :subCatStatus)")
    Page<LabSubCategoryMaster> findBySubCatNameContainingAndLabCategory_CatIdAndSubCatStatus(
            @Param("subCatName") String subCatName,
            @Param("catId") Integer catId,
            @Param("subCatStatus") CategoryStatus subCatStatus,
            Pageable pageable);

    @Query(value = "SELECT s FROM LabSubCategoryMaster s WHERE s.labCategory.catId = ?1 AND s.subCatStatus = ?2")
    List<LabSubCategoryMaster> findByCatIdAndStatus(Integer categoryId, CategoryStatus status);
}