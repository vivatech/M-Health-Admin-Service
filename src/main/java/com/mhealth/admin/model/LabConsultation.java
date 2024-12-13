package com.mhealth.admin.model;
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
@Table(name = "mh_lab_consultation")
public class LabConsultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lab_consult_id")
    private Integer labConsultId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", referencedColumnName = "case_id")
    private Consultation caseId; // Assuming this is the ID of the consultation case

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_orders_id", referencedColumnName = "id")
    private LabOrders labOrdersId; // Assuming this is the ID of the lab order

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "cat_id", nullable = false)
    private LabCategoryMaster categoryId;

    @Column(name = "sub_cat_id")
    private Integer subCatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_consult_patient_id", referencedColumnName = "user_id", nullable = false)
    private Users patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_consult_doctor_id", referencedColumnName = "user_id")
    private Users doctor;

    @Column(name = "lab_consult_doc_prescription")
    private String doctorPrescription;

    @Column(name = "lab_consult_created_at", nullable = false)
    private LocalDateTime labConsultCreatedAt;
}

