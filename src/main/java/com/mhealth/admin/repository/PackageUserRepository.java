package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.model.PackageUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PackageUserRepository extends JpaRepository<PackageUser, Integer> {

    @Query("Select u from PackageUser u where u.user.userId = ?1 and u.isExpire = ?2 order by u.id desc")
    List<PackageUser> getActivePackageDetail(Integer userId, YesNo expired, Pageable pageable);

    @Query("Select u from PackageUser u where u.user.userId = ?1 and u.packageInfo.packageId = ?2")
    List<PackageUser> findByUserIdAndPackageId(Integer userId, Integer packageId);
}