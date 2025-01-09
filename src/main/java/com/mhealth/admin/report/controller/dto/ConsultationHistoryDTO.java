package com.mhealth.admin.report.controller.dto;

import com.mhealth.admin.dto.enums.ConsultationType;
import com.mhealth.admin.dto.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationHistoryDTO {

    private int caseId;
    private String patientName;
    private String doctorName;
    private LocalDate consultationDate;
    private String time;
    private String consultType;
    private RequestType status;
    private ConsultationType consultationType;
    private String charge;
}
