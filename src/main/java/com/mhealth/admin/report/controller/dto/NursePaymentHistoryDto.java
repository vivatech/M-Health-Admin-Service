package com.mhealth.admin.report.controller.dto;

import com.mhealth.admin.dto.enums.IsTransfered;
import com.mhealth.admin.dto.enums.PaymentStatus;
import com.mhealth.admin.dto.enums.State;
import com.mhealth.admin.dto.enums.StatusFullName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NursePaymentHistoryDto {

    private Integer id;
    private String patientName;
    private String transactionId;
    private String nurseName;
    private String paymentStatus;
    private String serviceStatus;
    private IsTransfered paymentTransferToNurse;
    private LocalDateTime consultationDate;
    private String nurseCharge;
    private String patientPaidCharge;
    private Float adminCharge;
    private String paidType;

}
