package com.mhealth.admin.model;

import com.mhealth.admin.dto.enums.AddedType;
import com.mhealth.admin.dto.enums.StatusAI;
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
@Table(name = "mh_lab_report_docs")
public class LabReportDoc {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_report_doc_id")
    private Integer id;

    @Column(name = "case_id")
    private Integer caseId;

    @Column(name = "lab_orders_id", nullable = false)
    private Integer labOrdersId;

    @Column(name = "lab_report_doc_name", length = 250)
    private String labReportDocName;

    @Column(name = "lab_report_doc_type", nullable = false, length = 255)
    private String labReportDocType;

    @Column(name = "lab_report_doc_display_name", length = 100)
    private String labReportDocDisplayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "added_type", columnDefinition = "enum('Patient','Lab') default 'Lab'")
    private AddedType addedType = AddedType.Lab;

    @Column(name = "added_by")
    private Integer addedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "enum('A','I') default 'A'")
    private StatusAI status = StatusAI.A;

    @Column(name = "lab_report_doc_created_at", columnDefinition = "datetime default current_timestamp")
    private LocalDateTime createdAt;
}
