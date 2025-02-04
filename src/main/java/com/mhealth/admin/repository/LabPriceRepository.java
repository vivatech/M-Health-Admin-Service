package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.LabPrice;
import com.mhealth.admin.model.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LabPriceRepository extends JpaRepository<LabPrice, Integer>, JpaSpecificationExecutor<LabPrice> {

    @Query("Select u from LabPrice u where u.subCatId.subCatId in ?1 and u.labUser.type = ?2 and u.labUser.status = ?3")
    List<LabPrice> findBySubCatIdAndUserTypeAndStatus(List<Integer> labcatIds, UserType userType, String status);

    @Query("Select u from LabPrice u where u.labUser.userId = ?1 and u.subCatId.subCatId = ?2")
    List<LabPrice> findByLabIdAndSubCatId(Integer labId, Integer subCatId);

    @Query("Select u from LabPrice u where u.labUser.userId = ?1 and u.catId.catId = ?2 and (?3 is NULL OR u.subCatId.subCatId = ?3)")
    List<LabPrice> findByLabIdAndCatIdAndSubCatId(Integer userId, Integer catId, Integer subCatId);

    @Query("SELECT lp FROM LabPrice lp WHERE (:categoryId IS NULL OR lp.catId.catId = :categoryId) " +
            "AND (:subCategoryId IS NULL OR lp.subCatId.subCatId = :subCategoryId)")
    Page<LabPrice> findByCategoryAndSubCategory(@Param("categoryId") Integer categoryId,
                                                @Param("subCategoryId") Integer subCategoryId,
                                                Pageable pageable);

    Page<LabPrice> findByLabUser(Users lab, Pageable pageable);

    LabPrice findByLabPriceIdAndLabUser(Integer labPriceId, Users lab);
}