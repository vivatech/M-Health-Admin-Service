package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.PackageType;
import com.mhealth.admin.dto.enums.StatusAI;
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
@Table(name = "mh_healthtip_packages")
public class HealthTipPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "package_name_sl")
    private String packageNameSl;

    @ManyToOne
    @JoinColumn(name = "package_duration", referencedColumnName = "duration_id", nullable = false)
    private HealthTipDuration healthTipDuration;

    @Column(name = "package_price")
    private Float packagePrice;

    @Column(name = "package_price_video")
    private Float packagePriceVideo;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PackageType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAI status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
