package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.ConsultationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "mh_consultation_rating")
public class ConsultationRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "case_id")
    private Integer caseId;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id")
    private Users patientId;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id")
    private Users doctorId;

    @Column(name="rating")
    private Float rating;

    @Column(name = "comment")
    private String comment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ConsultationStatus status;
}
