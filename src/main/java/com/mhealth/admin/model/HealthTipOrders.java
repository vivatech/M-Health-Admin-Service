package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mh_healthtip_orders")
public class HealthTipOrders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Users patientId;

    @ManyToOne
    @JoinColumn(name = "healthtip_package_id", referencedColumnName = "package_id")
    private HealthTipPackage healthTipPackage;

    @Column(name = "amount", nullable = false)
    private Float amount;

    @Column(name = "currency_amount")
    private Float currencyAmount;

    @Column(name = "currency")
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "create_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
