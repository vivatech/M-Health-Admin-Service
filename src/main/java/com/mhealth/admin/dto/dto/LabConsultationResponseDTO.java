package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabConsultationResponseDTO {
    private Integer labConsultId;
    private Integer caseId;
    private Integer labOrderId;
    private Integer categoryId;
    private Integer subCatId;
    private String patientName;
    private String doctorName;
    private String doctorPrescription;
    private LocalDateTime labConsultCreatedAt;
    private LocalDate consultationDate;
}
