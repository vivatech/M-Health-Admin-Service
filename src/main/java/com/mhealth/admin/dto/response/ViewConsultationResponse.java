package com.mhealth.admin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewConsultationResponse {

    private Integer caseId;
    private String patientName;
    private String doctorName;
    private String patientContactNo;
    private String doctorContactNo;
    private String patientAge;
    private String patientAddress;
    private LocalDate consultationDate;
    private String consultationTime;
    private Float doctorCharge;
    private Double adminCommission;
    private Double finalPrice;
    private String status;

}
