package com.mhealth.admin.report.controller.dto;

import com.mhealth.admin.dto.enums.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Setter
@Getter
@Data
public class PatientTransactionDto {
    // Getters and Setters
    private Integer userId;
    private String contactNumber;
    private String channel;
    private String status;
    private String patientName;
    private String address;
    private String refId;
    private String createdBy;
    private String orderType;
    private String dob;
    private String gender;
    private String drName;
    private String clinicName;
    private String transactionType;

    // Constructor
    public PatientTransactionDto(Integer userId, String contactNumber, String channel, String status,
                                 String patientName, String address, String refId, String createdBy,
                                 String orderType, String dob, String gender, String drName,
                                 String clinicName, String transactionType) {
        this.userId = userId;
        this.contactNumber = contactNumber;
        this.channel = channel;
        this.status = status;
        this.patientName = patientName;
        this.address = address;
        this.refId = refId;
        this.createdBy = createdBy;
        this.orderType = orderType;
        this.dob = dob;
        this.gender = gender;
        this.drName = drName;
        this.clinicName = clinicName;
        this.transactionType = transactionType;
    }

}

