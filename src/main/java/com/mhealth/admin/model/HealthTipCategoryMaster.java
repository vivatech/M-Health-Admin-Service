package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_healthtip_category_master")
public class HealthTipCategoryMaster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "name_sl", nullable = false)
    private String nameSl;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "description_sl")
    private String descriptionSl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAI status;

    @Column(name = "photo", length = 50)
    private String photo;

    @Column(name = "is_featured", nullable = false)
    private String isFeatured;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
