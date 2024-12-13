package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "mh_user_otp")
@Data
public class UserOTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "otp")
    private String otp;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "status")
    private String status ;

    @Column(name = "is_from")
    private String isFrom;

    @Column(name = "type")
    private String type;


}
