package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.model.HealthTipCategoryMaster;
import com.mhealth.admin.model.HealthTipPackage;
import com.mhealth.admin.model.HealthTipPackageCategories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthTipPackageCategoriesRepository extends JpaRepository<HealthTipPackageCategories, Integer> {
    @Query("Select u from HealthTipPackageCategories u where u.healthTipPackage.packageId in ?1")
    List<HealthTipPackageCategories> findByPackageIds(List<Integer> healthTipsId);

    @Query("Select u.healthTipCategoryMaster.categoryId from HealthTipPackageCategories u where u.healthTipPackage.packageId in ?1")
    List<Integer> findCategoriesIdsByPackageIds(List<Integer> healthTipPackageIds);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.categoryId = ?1")
    Optional<HealthTipPackageCategories> findByCategoriesId(Integer categoryId);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipPackage.packagePrice >= ?2 and u.healthTipPackage.packagePrice <= ?3 and " +
            "u.healthTipCategoryMaster.categoryId in ?4 order by u.healthTipPackage.packagePrice ASC")
    Page<HealthTipPackageCategories> findByStatusPriceFromToCategoryIdsAndPriceAndSort(StatusAI statusAI, Float fromPrice,
                                                                                       Float toPrice, String[] catIds,
                                                                                       Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipPackage.packagePrice >= ?2 and u.healthTipPackage.packagePrice <= ?3 and " +
            "u.healthTipCategoryMaster.categoryId in ?4 order by u.healthTipPackage.packagePrice DESC")
    Page<HealthTipPackageCategories> findByStatusPriceFromToCategoryIdsAndPriceAndSortDesc(StatusAI statusAI, Float fromPrice,
                                                                                           Float toPrice, String[] catIds,
                                                                                           Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipPackage.packagePrice >= ?2 and u.healthTipPackage.packagePrice <= ?3 and " +
            "u.healthTipCategoryMaster.categoryId in ?4 order by u.healthTipCategoryMaster.priority")
    Page<HealthTipPackageCategories> findByStatusPriceFromToCategoryIdsAndPriceAndSortPriority(StatusAI statusAI,
                                                                                               Float fromPrice, Float toPrice,
                                                                                               String[] catIds, Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipPackage.packagePrice >= ?2 and u.healthTipPackage.packagePrice <= ?3 " +
            " order by u.healthTipPackage.packagePrice ASC")
    Page<HealthTipPackageCategories> findByStatusPriceFromToAndPriceAndSort(StatusAI statusAI,
                                                                            Float fromPrice, Float toPrice,
                                                                            String sortByPrice, Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipPackage.packagePrice >= ?2 and u.healthTipPackage.packagePrice <= ?3 " +
            " order by u.healthTipPackage.packagePrice DESC")
    Page<HealthTipPackageCategories> findByStatusPriceFromToAndPriceAndSortDesc(StatusAI statusAI,
                                                                                Float fromPrice, Float toPrice,
                                                                                String sortByPrice, Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipPackage.packagePrice >= ?2 and u.healthTipPackage.packagePrice <= ?3 " +
            " order by u.healthTipCategoryMaster.priority")
    Page<HealthTipPackageCategories> findByStatusPriceFromToAndPriceAndSortPriority(StatusAI statusAI,
                                                                                    Float fromPrice, Float toPrice, Pageable pageable);


    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipCategoryMaster.categoryId in ?2 order by u.healthTipPackage.packagePrice ASC")
    Page<HealthTipPackageCategories> findByStatusCategoryIdsAndPriceAndSort(StatusAI statusAI, List<Integer> catIds,
                                                                            Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipCategoryMaster.categoryId in ?2 order by u.healthTipPackage.packagePrice DESC")
    Page<HealthTipPackageCategories> findByStatusCategoryIdsAndPriceAndSortDesc(StatusAI statusAI, List<Integer> catIds,
                                                                                Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 and " +
            "u.healthTipCategoryMaster.categoryId in ?2 order by u.healthTipCategoryMaster.priority")
    Page<HealthTipPackageCategories> findByStatusCategoryIdsAndPriceAndSortPriority(StatusAI statusAI, List<Integer> catIds, Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 " +
            " order by u.healthTipPackage.packagePrice ASC")
    Page<HealthTipPackageCategories> findByStatusAndPriceAndSort(StatusAI statusAI, Pageable pageable);


    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 " +
            " order by u.healthTipPackage.packagePrice DESC")
    Page<HealthTipPackageCategories> findByStatusAndPriceAndSortDesc(StatusAI statusAI, Pageable pageable);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipCategoryMaster.status = ?1 " +
            " order by u.healthTipCategoryMaster.priority")
    Page<HealthTipPackageCategories> findByStatusSortPriority(StatusAI statusAI, Pageable pageable);

    @Query("Select u.healthTipCategoryMaster.categoryId from HealthTipPackageCategories u where u.healthTipPackage.packageId in ?1")
    List<Integer> findCategoryIdsByPackageIds(List<Integer> packageIds);

    @Query("Select u from HealthTipPackageCategories u where u.healthTipPackage.packageId = ?1")
    List<HealthTipPackageCategories> findByPackageIds(Integer packageIds);

    HealthTipPackageCategories findByHealthTipPackage(HealthTipPackage healthTipPackage);
    List<HealthTipPackageCategories> findByHealthTipCategoryMaster(HealthTipCategoryMaster category);
}