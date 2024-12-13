package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mh_users_promo_code")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersPromoCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "promo_code", nullable = false, length = 10)
    private String promoCode;
}
