package com.mhealth.admin.repository;

import com.mhealth.admin.model.NurseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NurseServiceRepository extends JpaRepository<NurseService, Integer> {
    @Query("Select u from NurseService u where u.status LIKE ?1")
    List<NurseService> findByStatus(String a);

    @Query("Select u from NurseService u where u.status LIKE ?2 and u.id in ?1")
    List<NurseService> findByIdsAndStatus(List<Integer> serviceIntIds, String a);

    @Query("Select u from NurseService u where u.id in ?1")
    List<NurseService> findByIds(List<Integer> ids);

    @Query("SELECT u FROM NurseService u " +
            "WHERE (:serviceName IS NULL OR :serviceName = '' OR u.seviceName LIKE %:serviceName%) " +
            "AND (:status IS NULL OR :status = '' OR u.status LIKE %:status%)")
    Page<NurseService> findBySeviceNameContainingIgnoreCaseAndStatusContainingIgnoreCase(
            @Param("serviceName") String serviceName,
            @Param("status") String status,
            Pageable pageable);

}