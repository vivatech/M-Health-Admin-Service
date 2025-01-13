package com.mhealth.admin.repository;

import com.mhealth.admin.model.AppBanner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppBannerRepository extends JpaRepository<AppBanner, Integer> {
    @Query("Select u from AppBanner u order by u.id desc")
    List<AppBanner> findAllByIdDesc();

    @Query("SELECT b FROM AppBanner b WHERE (:iname IS NULL OR :iname = '' OR LOWER(b.iname) LIKE LOWER(CONCAT('%', :iname, '%')))")
    Page<AppBanner> searchByIname(@Param("iname") String iname, Pageable pageable);

}