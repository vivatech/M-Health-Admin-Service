package com.mhealth.admin.sms;

public interface SendMessages {

    boolean supports(SMSAggregator smsAggregator);

    void sendSms(String msisdn, String message);

}
