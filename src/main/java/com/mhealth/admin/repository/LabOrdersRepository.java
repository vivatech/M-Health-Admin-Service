package com.mhealth.admin.repository;

import com.mhealth.admin.model.LabOrders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LabOrdersRepository extends JpaRepository<LabOrders, Integer> {
    @Query("Select u from LabOrders u where u.caseId.caseId = ?1")
    List<LabOrders> findByConsultationId(Integer caseId);

    @Query("Select u from LabOrders u where u.patientId.userId = ?1")
    Page<LabOrders> findByPatientId(Integer userId, Pageable pageable);

    @Query("Select u from LabOrders u where u.patientId.userId = ?1 and DATE(u.createdAt) >= ?2 and DATE(u.createdAt) <= ?3")
    Page<LabOrders> findByPatientIdAndDate(Integer userId, LocalDate start,LocalDate end, Pageable pageable);

    @Query(value = "SELECT u from LabOrders u where u.lab.userId = ?1 order by id desc")
    List<LabOrders> findByLabOrders(Integer labId);
}