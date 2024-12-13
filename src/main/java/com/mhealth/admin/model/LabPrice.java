package com.mhealth.admin.model;

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
@Table(name = "mh_lab_price")
public class LabPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_price_id")
    private Integer labPriceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "user_id", nullable = false)
    private Users labUser; // Assuming you have a User entity representing Lab Users

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", referencedColumnName = "cat_id", nullable = false)
    private LabCategoryMaster catId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_cat_id", referencedColumnName = "sub_cat_id", nullable = false)
    private LabSubCategoryMaster subCatId;

    @Column(name = "lab_price", nullable = false)
    private Float labPrice;

    @Column(name = "lab_price_comment")
    private String labPriceComment;

    @Column(name = "lab_price_created_at", nullable = false)
    private LocalDateTime labPriceCreatedAt;

    @Column(name = "lab_price_updated_at", nullable = false)
    private LocalDateTime labPriceUpdatedAt;

}

