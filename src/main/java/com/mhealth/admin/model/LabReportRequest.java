package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.LabReportPaymentStatus;
import com.mhealth.admin.dto.enums.LabReportRequestStatus;
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
@Table(name = "mh_lab_report_request")
public class LabReportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_report_req_id")
    private Integer labReportReqId;

    @ManyToOne
    @JoinColumn(name = "lab_consult_id", referencedColumnName = "lab_consult_id", nullable = false)
    private LabConsultation labConsultId;

    @ManyToOne
    @JoinColumn(name = "lab_id", referencedColumnName = "user_id", nullable = false)
    private Users labId;

    @Enumerated(EnumType.STRING)
    @Column(name = "lab_report_req_status", nullable = false)
    private LabReportRequestStatus labReportReqStatus = LabReportRequestStatus.Pending;

    @Enumerated(EnumType.STRING)
    @Column(name = "lab_report_payment_status", nullable = false)
    private LabReportPaymentStatus labReportPaymentStatus = LabReportPaymentStatus.Pending;

    @Column(name = "lab_report_req_created_at", nullable = false)
    private LocalDateTime labReportReqCreatedAt;

    @Column(name = "lab_report_req_updated_at", nullable = false)
    private LocalDateTime labReportReqUpdatedAt;
}

