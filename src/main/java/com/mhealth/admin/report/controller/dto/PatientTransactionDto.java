package com.mhealth.admin.report.controller.dto;

import lombok.Data;

@Data
public class PatientTransactionDto {

    private String refId;
    private String serviceType;
    private String orderType;
    private String doctorOrNurse;
    private String clinicName;
    private String mobile;
    private String patientName;
    private int age;
    private String gender;
    private String patientAddress;
    private String status;
    private String channel;
    private String createdAt;
}
