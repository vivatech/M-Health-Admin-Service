package com.mhealth.admin.dto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LabDashBoardResponse {
    private Integer reportId;
    private String patientName;
    private String doctorName;
    private String viewReport;
    private String paymentStatus;
    private String createdAt;
}
