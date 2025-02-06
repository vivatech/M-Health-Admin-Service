package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponseDto {
    private Integer caseId;
    private String patientName;
    private String doctorName;
    private String clinicName;
    private String date;
    private String time;
    private String amount;
    private String status;
}
