package com.mhealth.admin.repository;

import com.mhealth.admin.model.AppBanner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppBannerRepository extends JpaRepository<AppBanner, Integer> {
    @Query("Select u from AppBanner u order by u.id desc")
    List<AppBanner> findAllByIdDesc();

    @Query("SELECT b FROM AppBanner b WHERE LOWER(b.iname) LIKE LOWER(CONCAT('%', :iname, '%'))")
    List<AppBanner> searchByIname(@Param("iname") String iname);
}