package com.mhealth.admin.report.controller.dto;

import com.mhealth.admin.dto.enums.RequestType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsultationHistoryRequestDto {
    private String patientName;
    private RequestType status;
    private String doctorId;
    private String fromDate;
    private String toDate;
    private int page;
    private int size;
}
