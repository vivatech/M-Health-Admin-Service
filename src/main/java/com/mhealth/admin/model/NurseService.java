package com.mhealth.admin.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mh_nurse_service")
public class NurseService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sevice_name", nullable = false, length = 191)
    private String seviceName;

    @Column(name = "service_image", length = 50)
    private String serviceImage;

    @Column(name = "service_price", nullable = false)
    private Float servicePrice;

    @Column(name = "sevice_name_sl", length = 200)
    private String seviceNameSl;

    @Column(name = "description_sl", columnDefinition = "MEDIUMTEXT")
    private String descriptionSl;

    @Column(name = "admin_commission", nullable = false)
    private Float adminCommission;

    @Column(name = "total_service_price", nullable = false)
    private Float totalServicePrice;

    @Column(name = "commission_type", nullable = false, length = 20)
    private String commissionType;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
