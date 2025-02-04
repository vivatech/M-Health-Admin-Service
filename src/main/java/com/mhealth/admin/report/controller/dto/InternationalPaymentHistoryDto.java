package com.mhealth.admin.report.controller.dto;

import lombok.Data;

@Data
public class InternationalPaymentHistoryDto {

    private Integer doctorId;
    private String doctorName;
    private String amount;
    private String transactionId;
    private String paymentDate;
    private String proofFile;
}
