package com.mhealth.admin.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
    private String paymentNumber;
    private Double amount;
    private String transactionId;
    private Integer transactionInitiatedBy;
    private PaymentTypes transactionType;
    private Integer userId; //pass this when payment is for patient

}
