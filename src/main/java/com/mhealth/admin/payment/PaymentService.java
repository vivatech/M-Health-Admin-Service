package com.mhealth.admin.payment;

import com.mhealth.admin.dto.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class PaymentService {

    private final List<PaymentInterface> sortedProcessors;

    public PaymentService(List<PaymentInterface> sortedProcessors) {
        this.sortedProcessors = sortedProcessors;
    }

    public PaymentInterface getMatchedProcessor(PaymentAggregator paymentAggregator) {
        for (PaymentInterface processor : sortedProcessors) {
            if(processor.supports(paymentAggregator)) {
                return processor;
            }
        }
        return null;
    }

    public Response sendPayment(String msisdn, Double amount, String country){
        PaymentInterface matchedProcessor = getMatchedProcessor(getPaymentAggregator(country));
        return matchedProcessor.sendPayment(msisdn, amount);
    }

    public Response refundPayment(String msisdn, String transactionId, String country){
        PaymentInterface matchedProcessor = getMatchedProcessor(getPaymentAggregator(country));
        return matchedProcessor.reversePayment(msisdn, transactionId);
    }

    public PaymentAggregator getPaymentAggregator(String country) {
        HashMap<String, PaymentAggregator> map = new HashMap<>();
        map.put("SO", PaymentAggregator.WAAFI);
        map.put("KE", PaymentAggregator.SAFARI);
        return map.get(country);
    }

}
