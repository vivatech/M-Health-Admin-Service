package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mh_package")
public class Packages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_id")
    private Integer packageId;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @ManyToOne
    @JoinColumn(name = "package_duration", nullable = false)
    private PackageDuration packageDuration;

    @Column(name = "package_price", nullable = false)
    private Double packagePrice;

    @Column(name = "package_type", nullable = false)
    private String packageType;

    @Column(name = "total_chat")
    private Integer totalChat;

    @Column(name = "total_video_call")
    private Integer totalVideoCall;

    @Column(name = "total_health_tip")
    private Integer totalHealthTip;

    @Column(name = "status", nullable = false)
    private Character status;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
