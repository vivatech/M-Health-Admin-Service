package com.mhealth.admin.repository;

import com.mhealth.admin.model.HealthTipOrders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HealthTipOrdersRepository extends JpaRepository<HealthTipOrders, Integer> {
    @Query("Select u from HealthTipOrders u where u.patientId.userId = ?1 and u.healthTipPackage.packageId = ?2")
    List<HealthTipOrders> findByPatientIdAndHathTipPackageId(Integer userId, Integer packageId);
}