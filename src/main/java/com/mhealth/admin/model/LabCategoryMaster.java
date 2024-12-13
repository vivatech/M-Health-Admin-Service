package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.CategoryStatus;
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
@Table(name = "mh_lab_cat_master")
public class LabCategoryMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cat_id")
    private Integer catId;

    @Column(name = "cat_name", nullable = false)
    private String catName;

    @Column(name = "cat_name_sl", nullable = false)
    private String catNameSl;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Enumerated(EnumType.STRING)
    @Column(name = "cat_status", nullable = false)
    private CategoryStatus catStatus = CategoryStatus.Active;

    @Column(name = "cat_created_at", nullable = false)
    private LocalDateTime catCreatedAt;

    @Column(name = "cat_updated_at", nullable = false)
    private LocalDateTime catUpdatedAt;

  }


