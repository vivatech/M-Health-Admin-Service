package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_doctor_payment")
public class DoctorPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_slot_id")
    private Integer id;

    @Column(name = "doctor_id")
    private Integer doctorId;

    @Column(name = "hospital_id")
    private Integer hospitalId;

    @Column(name = "case_id")
    private Integer caseId;

    @Column(name = "amount")
    private Integer amount;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "paid_by")
    private Integer paidBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
