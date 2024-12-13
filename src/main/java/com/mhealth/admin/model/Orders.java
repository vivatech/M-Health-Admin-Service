package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.CommissionType;
import com.mhealth.admin.dto.enums.OrderStatus;
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
@Table(name = "mh_orders")
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "case_id", referencedColumnName = "case_id")
    private Consultation caseId;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Users patientId;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private Users doctorId;

    @ManyToOne
    @JoinColumn(name = "package_id", referencedColumnName = "package_id")
    private Packages packageId;

    @ManyToOne
    @JoinColumn(name = "healthtip_package_id", referencedColumnName = "package_id")
    private HealthTipPackage healthtipPackageId;

    @Enumerated(EnumType.STRING)
    @Column(name = "commission_type", nullable = false)
    private CommissionType commissionType;

    @Column(name = "doctor_amount")
    private Float doctorAmount;

    @Column(name = "commision")
    private Float commission;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "currency_amount")
    private Float currencyAmount;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @ManyToOne
    @JoinColumn(name = "coupon_id", referencedColumnName = "id")
    private Coupon couponId;

    @Column(name = "agent_user_id")
    private Long agentUserId;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

}
