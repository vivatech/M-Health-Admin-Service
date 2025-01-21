package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.IsTransfered;
import com.mhealth.admin.dto.enums.StatusFullName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mh_nurse_demand_orders")
public class NurseDemandOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "trip_id")
    private String tripId;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Users patientId;

    @ManyToOne
    @JoinColumn(name = "nurse_id", referencedColumnName = "id", nullable = false)
    private PartnerNurse nurseId;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "nurse_mobile", nullable = false)
    private String nurseMobile;

    @Column(name = "service_fee")
    private Float serviceFee;

    @Column(name = "distance_fee")
    private Float distanceFee;

    @Column(name = "commission", nullable = false)
    private Float commission;

    @Column(name = "service_amount", nullable = false)
    private Float serviceAmount;

    @Column(name = "slsh_service_amount", nullable = false)
    private Float slshServiceAmount;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "slsh_amount")
    private Float slshAmount;

    @Column(name = "sls_rate")
    private Float slsRate;

    @Column(name = "currency")
    private String currency;

    @Column(name = "payment_number", nullable = false)
    private String paymentNumber;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusFullName status;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "refund_amount")
    private Float refundAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_transfered", nullable = false)
    private IsTransfered isTransfered;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "issuer_transaction_id")
    private String issuerTransactionId;

    @Column(name = "ref_id")
    private String refId;

    @Column(name = "paid_by")
    private Integer paidBy;

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
