package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.DurationType;
import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "mh_package_duration")
public class PackageDuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "duration_id")
    private Long durationId;

    @Column(name = "duration_name", nullable = false)
    private String durationName;

    @Column(name = "duration_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DurationType durationType;

    @Column(name = "duration_value", nullable = false)
    private Integer durationValue;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusAI status;
}
