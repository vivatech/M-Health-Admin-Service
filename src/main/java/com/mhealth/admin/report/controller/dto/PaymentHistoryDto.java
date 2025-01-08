package com.mhealth.admin.report.controller.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentHistoryDto {

    private Integer caseId;
    private String patientName;
    private String transactionId;
    private String doctorName;
    private String consultationType;
    private LocalDate consultationDate;
    private Float doctorCharge;
    private Float patientPaidCharge;
    private String clinicName;
    private Float adminCharge;
    private String consultationTime;

}
