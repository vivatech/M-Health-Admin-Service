package com.mhealth.admin.model;

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
@Table(name = "mh_healthtip_package_categories")
public class HealthTipPackageCategories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "healthtip_package_category_id")
    private Integer healthTipPackageCategoryId;

    @ManyToOne
    @JoinColumn(name = "healthtip_package_id", referencedColumnName = "package_id", nullable = false)
    private HealthTipPackage healthTipPackage;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private HealthTipCategoryMaster healthTipCategoryMaster;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

