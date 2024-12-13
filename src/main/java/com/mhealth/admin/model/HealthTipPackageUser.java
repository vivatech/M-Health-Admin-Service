package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.YesNo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mh_healthtip_package_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthTipPackageUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "healthtip_package_id", referencedColumnName = "package_id", nullable = false)
    private HealthTipPackage healthTipPackage;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private Users user;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Column(name = "is_expire", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesNo isExpire;

    @Column(name = "is_cancel", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesNo isCancel;

    @Column(name = "is_video", nullable = false)
    @Enumerated(EnumType.STRING)
    private YesNo isVideo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
