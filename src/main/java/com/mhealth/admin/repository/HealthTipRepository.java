package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.HealthTip;
import com.mhealth.admin.model.HealthTipCategoryMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HealthTipRepository extends JpaRepository<HealthTip, Integer> {
    @Query("Select u from HealthTip u where u.healthTipCategory.categoryId in ?1")
    List<HealthTip> findByCategorisIds(List<Integer> categoriesIds);

    @Query("Select u from HealthTip u where u.topic like %?1% and u.healthTipCategory.categoryId in ?2 and u.healthTipCategory.categoryId = ?3")
    Page<HealthTip> findByTitlePackageCategories(String title, List<Integer> packageId, Integer categoryId, Pageable pageable);

    @Query("Select u from HealthTip u where u.topic like %?1% and u.healthTipCategory.categoryId in ?2")
    Page<HealthTip> findByTitleCategories(String title, List<Integer> categoriesIds, Pageable pageable);

    @Query("Select u from HealthTip u where u.topic like %?1% and u.healthTipCategory.categoryId = ?2")
    Page<HealthTip> findByTitleCategorieId(String title, Integer categoryId, Pageable pageable);

    @Query("Select u from HealthTip u where u.status = ?1 and u.healthTipCategory.categoryId in ?2")
    List<HealthTip> findByStatusAndCategory(StatusAI statusAI, List<Integer> categoryIds);

    List<HealthTip> findByCategory(HealthTipCategoryMaster healthTipCategoryMaster);

    @Query("Select u from HealthTip u where u.topic LIKE %?1% order by u.healthTipCategory.categoryId ASC, u.healthTipId DESC")
    List<HealthTip> findAllByTopic(String title);

    @Query("Select u from HealthTip u where (:topic is null or u.topic LIKE %:topic%) AND (:status is null or u.status = :status)")
    Page<HealthTip> findByTopicContainingAndStatus(
            @Param("topic") String topic,@Param("status")  StatusAI status, Pageable pageable);
}