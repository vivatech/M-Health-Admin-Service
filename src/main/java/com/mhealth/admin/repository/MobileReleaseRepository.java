package com.mhealth.admin.repository;

import com.mhealth.admin.model.MobileRelease;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MobileReleaseRepository extends JpaRepository<MobileRelease,Integer> {

    @Query("SELECT u FROM MobileRelease u WHERE (:appVersion IS NULL OR :appVersion = '' OR u.appVersion LIKE %:appVersion%)")
    Page<MobileRelease> findByAppVersionContainingIgnoreCase(@Param("appVersion") String appVersion, Pageable pageable);



}
