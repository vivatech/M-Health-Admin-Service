package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.PaymentStatus;
import com.mhealth.admin.dto.enums.StatusFullName;
import com.mhealth.admin.model.NurseDemandOrders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface NurseDemandOrdersRepository extends JpaRepository<NurseDemandOrders, Integer> {
    @Query("Select u from NurseDemandOrders u where u.patientId.userId = ?1 and u.nurseId IS NOT NULL and u.paymentStatus = ?2 and u.status = ?3")
    List<NurseDemandOrders> findByPatientNurseNotNullPaymentStatusStatus(Integer userId, PaymentStatus paymentStatus, StatusFullName status);

    @Query("Select COUNT(u) from NurseDemandOrders u where u.status != ?1")
    Long countActiveNurseBookings(StatusFullName statusFullName);
    @Query("""
            SELECT n FROM NurseDemandOrders n
            WHERE (:patientName IS NULL OR LOWER(n.patientId.firstName) LIKE LOWER(CONCAT('%', :patientName, '%')))
            AND (:nurseName IS NULL OR LOWER(n.nurseId.name) LIKE LOWER(CONCAT('%', :nurseName, '%')))
            AND (:consultationDate IS NULL OR DATE(n.createdAt) = :consultationDate)
            """)
    Page<NurseDemandOrders> searchOrders(
            @Param("patientName") String patientName,
            @Param("nurseName") String nurseName,
            @Param("consultationDate") LocalDate consultationDate,
            Pageable pageable);
}