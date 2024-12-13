package com.mhealth.admin.repository;

import com.mhealth.admin.dto.LabRefundRequestResponseDTO;
import com.mhealth.admin.model.LabRefundRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LabRefundRequestRepository extends JpaRepository<LabRefundRequest, Integer> {
    @Query("Select u from LabRefundRequest u where u.labOrderId = ?1")
    List<LabRefundRequest> findByLabOrderId(Integer id);

    @Query("SELECT new com.mhealth.admin.dto.LabRefundRequestResponseDTO(" +
            "r.id, r.labOrderId, r.transactionId, r.amount, r.paymentMethod, " +
            "r.refundTransactionId, r.status, r.rejectBy, r.createdAt, " +
            "p.firstName, p.lastName, l.firstName, l.lastName) " +
            "FROM LabRefundRequest r " +
            "JOIN LabOrders o ON r.labOrderId = o.id " +
            "JOIN Users p ON o.patientId.userId = p.userId " +
            "JOIN Users l ON o.lab.userId = l.userId " +
            "WHERE (:patientName IS NULL OR CONCAT(p.firstName, ' ', p.lastName) LIKE %:patientName%) " +
            "AND (:labName IS NULL OR CONCAT(l.firstName, ' ', l.lastName) LIKE %:labName%)")
    Page<LabRefundRequestResponseDTO> findByPatientNameAndLabName(
            @Param("patientName") String patientName,
            @Param("labName") String labName,
            Pageable pageable);
}