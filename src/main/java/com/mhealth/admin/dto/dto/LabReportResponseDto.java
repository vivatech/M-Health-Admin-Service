package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LabReportResponseDto {
    private Integer reportId;
    private Integer caseId;
    private String patientName;
    private String doctorName;
    private Date reportDate;
    private Date deliveryDate;
    private String slot;
    private String paymentStatus;
    private String status;
    private ViewRequestInformation viewRequestInformation;
    private List<ReportDocumentDto> reportDocument;

}
