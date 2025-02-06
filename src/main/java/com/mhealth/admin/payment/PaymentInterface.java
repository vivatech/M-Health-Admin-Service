package com.mhealth.admin.payment;

import com.mhealth.admin.dto.response.Response;

public interface PaymentInterface {

    boolean supports(PaymentAggregator paymentAggregator);

    Response sendPayment(PaymentDto paymentDto);

    Response reversePayment(String msisdn, String transactionId);
}
