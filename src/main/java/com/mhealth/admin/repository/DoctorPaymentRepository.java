package com.mhealth.admin.repository;

import com.mhealth.admin.model.DoctorPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorPaymentRepository extends JpaRepository<DoctorPayment,Integer> {
    @Query("Select SUM(u.amount) from DoctorPayment u")
    Double getTotalCompletedPayments();

    DoctorPayment findByCaseId(Integer caseId);
}
