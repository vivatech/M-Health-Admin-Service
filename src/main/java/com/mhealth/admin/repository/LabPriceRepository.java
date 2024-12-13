package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.LabPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LabPriceRepository extends JpaRepository<LabPrice, Integer> {

    @Query("Select u from LabPrice u where u.subCatId.subCatId in ?1 and u.labUser.type = ?2 and u.labUser.status = ?3")
    List<LabPrice> findBySubCatIdAndUserTypeAndStatus(List<Integer> labcatIds, UserType userType, String status);

    @Query("Select u from LabPrice u where u.labUser.userId = ?1 and u.subCatId.subCatId = ?2")
    List<LabPrice> findByLabIdAndSubCatId(Integer labId, Integer subCatId);

    @Query("Select u from LabPrice u where u.labUser.userId = ?1 and u.catId.catId = ?2 and (?3 is NULL OR u.subCatId.subCatId = ?3)")
    List<LabPrice> findByLabIdAndCatIdAndSubCatId(Integer userId, Integer catId, Integer subCatId);
}