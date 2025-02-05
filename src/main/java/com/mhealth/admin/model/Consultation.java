package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "mh_consultation")
public class Consultation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Integer caseId;

    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Users patientId;

    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private Users doctorId;

    @ManyToOne
    @JoinColumn(name = "package_id", referencedColumnName = "package_id")
    private HealthTipPackage packageId;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "consultation_date", nullable = false)
    private LocalDate consultationDate;

    @Column(name = "consult_type")
    private String consultType;

    @ManyToOne
    @JoinColumn(name = "slot_id", referencedColumnName = "slot_id", nullable = false)
    private SlotMaster slotId;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "zone_id")
    private Integer zoneId;

    @Column(name = "landmark")
    private String landmark;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @Column(name = "cancel_message", length = 255)
    private String cancelMessage;

    @Column(name = "agent_user_id")
    private Integer agentUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "consultation_type", nullable = false)
    private ConsultationType consultationType;

    @Column(name = "follow_up")
    private Integer followUp;

    @Enumerated(EnumType.STRING)
    @Column(name = "added_type", nullable = false)
    private AddedType addedType;

    @Column(name = "report_suggested", columnDefinition = "ENUM('0', '1')",  nullable = false)
    private String reportSuggested;

    @Enumerated(EnumType.STRING)
    @Column(name = "consult_status", nullable = false)
    private ConsultStatus consultStatus;

    @Column(name = "assigned_to")
    private Integer assignedTo;

    @Column(name = "added_by")
    private Integer addedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Channel channel;

    @Column(name = "meeting_id")
    private String meetingId;
}
