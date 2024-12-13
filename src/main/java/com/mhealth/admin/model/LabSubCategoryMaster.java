package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.enums.YesNo;
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
@Table(name = "mh_lab_sub_cat_master")
public class LabSubCategoryMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sub_cat_id")
    private Integer subCatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cat_id", referencedColumnName = "cat_id", nullable = false)
    private LabCategoryMaster labCategory;

    @Column(name = "sub_cat_name", nullable = false)
    private String subCatName;

    @Column(name = "sub_cat_name_sl")
    private String subCatNameSl;

//    @Column(name = "image")
//    private String image;

//    @Column(name = "alt_text")
//    private String altText;

//    @Column(name = "description")
//    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_cat_status", nullable = false)
    private CategoryStatus subCatStatus = CategoryStatus.Active;
//
//    @Column(name = "what_test_is")
//    private String whatTestIs;

//    @Column(name = "test_prepration")
//    private String testPreparation;
//
//    @Column(name = "understand_result")
//    private String understandResult;
//
//    @Column(name = "expect_deliver")
//    private String expectDeliver;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "is_featured", nullable = false)
//    private YesNo isFeatured = YesNo.No;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_home_consultant_available", nullable = false)
    private YesNo isHomeConsultantAvailable = YesNo.No;

    @Column(name = "sub_cat_created_at", nullable = false)
    private LocalDateTime subCatCreatedAt;

    @Column(name = "sub_cat_updated_at", nullable = false)
    private LocalDateTime subCatUpdatedAt;
}
