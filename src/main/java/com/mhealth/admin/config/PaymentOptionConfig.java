package com.mhealth.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaymentOptionConfig {

    @Value("${payment.option.currency.data}")
    private String paymentOptionCurrency;

    @Value("${payment.option.method}")
    private String paymentOptionMethod;

    public Map<String, String> getCurrency() {
        Map<String,String> response = new HashMap<>();
        String[] data = paymentOptionCurrency.split("\\|");
        for(String value:data){
            String[] keyValue = value.split(":");
            response.put(keyValue[0],keyValue[1]);
        }
        return response;
    }

    public Map<String, String> getPaymentMethod() {
        Map<String,String> response = new HashMap<>();
        String[] data = paymentOptionMethod.split("\\|");
        for(String value:data){
            String[] keyValue = value.split(":");
            response.put(keyValue[0],keyValue[1]);
        }
        return response;
    }
}
