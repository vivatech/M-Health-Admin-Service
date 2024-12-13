package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.DurationType;
import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Setter
@Getter
@Table(name = "mh_healthtip_duration")
@NoArgsConstructor
@AllArgsConstructor
public class HealthTipDuration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "duration_id")
    private Integer durationId;

    @Column(name = "duration_name", nullable = false)
    private String durationName;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_type", nullable = false)
    private DurationType durationType;

    @Column(name = "duration_value", nullable = false)
    private Integer durationValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusAI status;
}
