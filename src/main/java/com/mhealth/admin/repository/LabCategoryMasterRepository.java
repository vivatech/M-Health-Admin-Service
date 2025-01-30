package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.LabCategoryMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LabCategoryMasterRepository extends JpaRepository<LabCategoryMaster, Integer> {

    @Query("Select DISTINCT u.catId from LabPrice u where u.catId.catStatus = ?1")
    List<LabCategoryMaster> findActiveLabCategoryByLabPrice(CategoryStatus status);

    @Query("Select u from LabCategoryMaster u where u.catName = :catName")
    Optional<LabCategoryMaster> findByCatName(@Param("catName") String catName);

    @Query("Select u from LabCategoryMaster u where u.catName LIKE %:catName% " +
            "AND (:catStatus IS NULL OR u.catStatus = :catStatus)")
    Page<LabCategoryMaster> findByCatNameContainingAndCatStatus(
            @Param("catName") String catName,
            @Param("catStatus") StatusAI catStatus, Pageable pageable);

    @Query(value = "SELECT * FROM mh_lab_cat_master lcm WHERE lcm.cat_status = 'Active' ", nativeQuery = true)
    List<LabCategoryMaster> findAllByCatStatus();
}