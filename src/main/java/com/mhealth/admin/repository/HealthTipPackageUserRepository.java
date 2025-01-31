package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.PackageType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.model.HealthTipPackage;
import com.mhealth.admin.model.HealthTipPackageUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthTipPackageUserRepository extends JpaRepository<HealthTipPackageUser,Integer> {
    @Query("Select u from HealthTipPackageUser u where u.user.userId = ?1 and u.isExpire = ?2")
    List<HealthTipPackageUser> findByUserIdAndExpirey(Integer userId, YesNo yesNo);

    @Query("Select u from HealthTipPackageUser u where u.user.userId = ?1 order by u.id DESC")
    List<HealthTipPackageUser> findByUserId(Integer userId);

    @Query("Select u.id from HealthTipPackageUser u where u.user.userId = ?1 and u.isExpire = ?2")
    List<Integer> getIdByUserIdAndExpiery(Integer userId, YesNo yesNo);

    @Query("SELECT p FROM HealthTipPackageUser p WHERE p.user.userId = ?1 AND p.healthTipPackage.packageId = ?2 AND p.isExpire = 'No'")
    HealthTipPackageUser findActivePackageForUser(Integer userId, Integer categoryId);

    @Query("Select u from HealthTipPackageUser u where u.id = ?1 and u.isExpire = ?2")
    Optional<HealthTipPackageUser> getByIdAndExpiry(Integer userId, YesNo yesNo);

    @Query("Select u.healthTipPackage.packageId from HealthTipPackageUser u where u.user.userId = ?1 and u.isExpire = ?2")
    List<Integer> findPackageIdsByUserIdAndExpire(int userId, YesNo yesNo);

    @Query("Select u from HealthTipPackageUser u where u.user.userId = ?1 and u.healthTipPackage.packageId = ?2 and u.isExpire = 'No'")
    List<HealthTipPackageUser> findByUserIdAndPackageId(Integer userId, Integer categoryId);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "JOIN HealthTipPackageCategories h ON u.healthTipPackage.packageId = h.healthTipPackage.packageId " +
            "WHERE u.createdAt = ?1 AND u.isCancel = ?2 AND u.healthTipPackage.type = ?3 AND " +
            "u.healthTipPackage.packageName LIKE %?4% AND h.healthTipCategoryMaster.categoryId = ?5 AND u.user.userId = ?6")
    Page<HealthTipPackageUser> findByCreatedAtIsCanceledTypePackageNameCategoryIdUserId(LocalDate createdDate, YesNo yesNo,
                                                                                        PackageType type, String packageName,
                                                                                        Integer categoryId, Integer userId, Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            " WHERE u.createdAt = ?1 AND u.isCancel = ?2 " +
            "AND u.healthTipPackage.type = ?3 AND u.healthTipPackage.packageName LIKE %?4% AND " +
            " u.user.userId = ?5")
    Page<HealthTipPackageUser> findByCreatedAtIsCanceledTypePackageNameUserId(LocalDate createdDate, YesNo yesNo,
                                                                              PackageType type, String packageName,
                                                                              Integer userId, Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "JOIN HealthTipPackageCategories h ON u.healthTipPackage.packageId = h.healthTipPackage.packageId " +
            "WHERE u.createdAt = ?1 AND u.isCancel = ?2 AND  u.healthTipPackage.packageName LIKE %?3% AND " +
            "h.healthTipCategoryMaster.categoryId = ?4 AND u.user.userId = ?5")
    Page<HealthTipPackageUser> findByCreatedAtIsCanceledPackageNameCategoryIdUserId(LocalDate createdDate, YesNo yesNo,
                                                                                    String packageName, Integer categoryId,
                                                                                    Integer userId, Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "JOIN HealthTipPackageCategories h ON u.healthTipPackage.packageId = h.healthTipPackage.packageId " +
            "WHERE u.isCancel = ?1 AND u.healthTipPackage.type = ?2 AND " +
            "u.healthTipPackage.packageName LIKE %?3% AND h.healthTipCategoryMaster.categoryId = ?4 AND u.user.userId = ?5")
    Page<HealthTipPackageUser> findByIsCanceledTypePackageNameCategoryIdUserId(YesNo yesNo, PackageType type,
                                                                               String packageName, Integer categoryId,
                                                                               Integer userId, Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u  WHERE u.isCancel = ?1 " +
            "AND u.healthTipPackage.type = ?2 AND u.healthTipPackage.packageName LIKE %?3% AND " +
            " u.user.userId = ?4")
    Page<HealthTipPackageUser> findByIsCanceledTypePackageNameUserId(YesNo yesNo, PackageType type,
                                                                     String packageName, Integer userId,
                                                                     Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "JOIN HealthTipPackageCategories h ON u.healthTipPackage.packageId = h.healthTipPackage.packageId " +
            "WHERE u.isCancel = ?1 AND  u.healthTipPackage.packageName LIKE %?2% AND " +
            "h.healthTipCategoryMaster.categoryId = ?3 AND u.user.userId = ?4")
    Page<HealthTipPackageUser> findByIsCanceledPackageNameCategoryIdUserId(YesNo yesNo, String packageName,
                                                                           Integer categoryId, Integer userId,
                                                                           Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "WHERE u.isCancel = ?1 AND  u.healthTipPackage.packageName LIKE %?2% AND " +
            " u.user.userId = ?3")
    Page<HealthTipPackageUser> findByIsCanceledPackageNameUserId(YesNo yesNo, String packageName,
                                                                            Integer userId,
                                                                           Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "JOIN HealthTipPackageCategories h ON u.healthTipPackage.packageId = h.healthTipPackage.packageId " +
            "WHERE u.createdAt = ?1 AND u.healthTipPackage.type = ?2 AND " +
            "u.healthTipPackage.packageName LIKE %?3% AND h.healthTipCategoryMaster.categoryId = ?4 AND u.user.userId = ?5")
    Page<HealthTipPackageUser> findByCreatedAtTypePackageNameCategoryIdUserId(LocalDate createdDate, PackageType type,
                                                                              String packageName, Integer categoryId,
                                                                              Integer userId, Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u WHERE u.createdAt = ?1 AND " +
            "u.healthTipPackage.type = ?2 AND u.healthTipPackage.packageName LIKE %?3% AND u.user.userId = ?4")
    Page<HealthTipPackageUser> findByCreatedAtTypePackageNameUserId(LocalDate createdDate, PackageType type,
                                                                    String packageName, Integer userId,
                                                                    Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "JOIN HealthTipPackageCategories h ON u.healthTipPackage.packageId = h.healthTipPackage.packageId " +
            "WHERE u.createdAt = ?1 AND  u.healthTipPackage.packageName LIKE %?2% AND " +
            "h.healthTipCategoryMaster.categoryId = ?3 AND u.user.userId = ?4")
    Page<HealthTipPackageUser> findByCreatedAtPackageNameCategoryIdUserId(LocalDate createdDate, String packageName,
                                                                          Integer categoryId, Integer userId,
                                                                          Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "JOIN HealthTipPackageCategories h ON u.healthTipPackage.packageId = h.healthTipPackage.packageId " +
            "WHERE u.healthTipPackage.type = ?1 AND u.healthTipPackage.packageName LIKE %?2% AND " +
            "h.healthTipCategoryMaster.categoryId = ?3 AND u.user.userId = ?4")
    Page<HealthTipPackageUser> findByTypePackageNameCategoryIdUserId(PackageType type, String packageName,
                                                                     Integer categoryId, Integer userId,
                                                                     Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u  WHERE u.healthTipPackage.type = ?1 AND " +
            "u.healthTipPackage.packageName LIKE %?2% AND u.user.userId = ?3")
    Page<HealthTipPackageUser> findByTypePackageNameUserId(PackageType type, String packageName,
                                                           Integer userId, Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            "JOIN HealthTipPackageCategories h ON u.healthTipPackage.packageId = h.healthTipPackage.packageId " +
            " WHERE u.healthTipPackage.packageName LIKE %?1% AND " +
            "h.healthTipCategoryMaster.categoryId = ?2 AND u.user.userId = ?3")
    Page<HealthTipPackageUser> findByPackageNameCategoryIdUserId(String packageName, Integer categoryId,
                                                                 Integer userId, Pageable pageable);

    @Query("SELECT u FROM HealthTipPackageUser u " +
            " WHERE u.healthTipPackage.packageName LIKE %?1% AND " +
            " u.user.userId = ?2")
    Page<HealthTipPackageUser> findByPackageNameUserId(String packageName, Integer userId, Pageable pageable);

    @Query("SELECT h FROM HealthTipPackageUser h WHERE " +
            "(:packageName IS NULL OR h.healthTipPackage.packageName = :packageName) AND " +
            "(:filterDate IS NULL OR DATE(h.createdAt) = :filterDate)")
    Page<HealthTipPackageUser> findByPackageNameAndCreatedAtDate(
            @Param("packageName") String packageName,
            @Param("filterDate") LocalDate filterDate,
            Pageable pageable);

    List<HealthTipPackageUser> findByHealthTipPackage(HealthTipPackage healthTipPackage);
}
