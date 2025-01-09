package com.mhealth.admin.report.controller.dto;

import com.mhealth.admin.dto.enums.Channel;
import com.mhealth.admin.dto.enums.OrderType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PatientTransactionDto {

    private String refId;
    private String serviceType;
    private String orderType;
    private String doctorOrNurse;
    private String clinicName;
    private String mobile;
    private String patientName;
    private LocalDate age;
    private String gender;
    private String patientAddress;
    private String status;
    private Channel channel;
    private LocalDateTime createdAt;
}
