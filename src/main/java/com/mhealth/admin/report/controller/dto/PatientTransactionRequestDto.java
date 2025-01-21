package com.mhealth.admin.report.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientTransactionRequestDto {
    private String serviceType;
    private String status;
    private String orderType;
    private String channel;
    private String fromDate;
    private String toDate;
    private int page;
    private int size;
}
