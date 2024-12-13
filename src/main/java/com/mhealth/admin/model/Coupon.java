package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.CouponCategory;
import com.mhealth.admin.dto.enums.DiscountType;
import com.mhealth.admin.dto.enums.OfferType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mh_coupon")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "coupon_code", nullable = false)
    private String couponCode;

    @Column(name = "no_of_used", nullable = false)
    private Integer numberOfUsed;

    @Column(name = "offer_for_number_of_users", nullable = false)
    private Integer offerForNumberOfUsers;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OfferType type;

    @Column(name = "discount_amount", nullable = false)
    private Integer discountAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType discountType;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private CouponCategory category;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "end_date")
    private LocalDateTime endDate;
}
