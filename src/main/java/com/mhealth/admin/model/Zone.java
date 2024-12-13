package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mh_zone")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Zone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "status", nullable = false, columnDefinition = "ENUM('A','I')")
    private StatusAI status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
