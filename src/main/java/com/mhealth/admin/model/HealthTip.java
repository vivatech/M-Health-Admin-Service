package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_healthtip")
public class HealthTip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "healthtip_id")
    private Integer healthTipId;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
    private HealthTipCategoryMaster healthTipCategory;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "photo", nullable = false)
    private String photo;

    @Column(name = "video")
    private String video;

    @Column(name = "video_thumb")
    private String videoThumb;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAI status;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}

