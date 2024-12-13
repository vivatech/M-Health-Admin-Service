package com.mhealth.admin.repository;

import com.mhealth.admin.model.MobileRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileReleaseRepository extends JpaRepository<MobileRelease,Integer> {

    @Query("Select u from MobileRelease u where u.appVersion LIKE %:appVersion%")
    List<MobileRelease> findByAppVersionContainingIgnoreCase(@Param("appVersion") String appVersion);
}
