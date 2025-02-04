package com.mhealth.admin.repository;

import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.model.GroupPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface GroupPaymentRepository extends JpaRepository<GroupPayment, Integer> {

    @Query("SELECT gp FROM GroupPayment gp " +
            "WHERE (:transactionId IS NULL OR gp.transactionId = :transactionId) " +
            "AND (:startOfDay IS NULL OR :endOfDay IS NULL OR gp.paymentDate BETWEEN :startOfDay AND :endOfDay) " +
            "AND (:userType IS NULL OR gp.userType = :userType)")
    Page<GroupPayment> findByTransactionIdAndPaymentDateRangeAndUserType(
            @Param("transactionId") String transactionId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("userType") UserType userType,
            Pageable pageable
    );


}