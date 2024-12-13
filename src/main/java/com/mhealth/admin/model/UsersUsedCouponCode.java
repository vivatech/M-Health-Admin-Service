package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mh_users_used_coupon_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersUsedCouponCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "coupon_id", nullable = false)
    private Integer couponId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
