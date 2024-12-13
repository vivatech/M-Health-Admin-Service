package com.mhealth.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LabRefundRequestResponseDTO {
    private Integer refundRequestId;
    private Integer labOrderId;
    private String transactionId;
    private Float amount;
    private String paymentMethod;
    private String refundTransactionId;
    private String status;
    private String rejectBy;
    private LocalDateTime createdAt;

    private String patientFirstName;
    private String patientLastName;
    private String labFirstName;
    private String labLastName;

    public LabRefundRequestResponseDTO(
            Integer refundRequestId, Integer labOrderId, String transactionId,
           Float amount, String paymentMethod, String refundTransactionId,
           String status, String rejectBy, LocalDateTime createdAt,
           String patientFirstName, String patientLastName, String labFirstName,
           String labLastName) {
        this.refundRequestId = refundRequestId;
        this.labOrderId = labOrderId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.refundTransactionId = refundTransactionId;
        this.status = status;
        this.rejectBy = rejectBy;
        this.createdAt = createdAt;
        this.patientFirstName = patientFirstName;
        this.patientLastName = patientLastName;
        this.labFirstName = labFirstName;
        this.labLastName = labLastName;
    }
}
