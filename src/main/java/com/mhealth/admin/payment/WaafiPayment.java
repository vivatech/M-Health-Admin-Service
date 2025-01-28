package com.mhealth.admin.payment;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.exception.AdminModuleExceptionHandler;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.model.WalletTransaction;
import com.mhealth.admin.repository.UsersRepository;
import com.mhealth.admin.repository.WalletTransactionRepository;
import com.mhealth.admin.sms.SMSApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class WaafiPayment implements PaymentInterface {
    private final WalletTransactionRepository walletTransactionRepository;
    private final UsersRepository usersRepository;

    public WaafiPayment(UsersRepository usersRepository,
                        WalletTransactionRepository walletTransactionRepository) {
        this.usersRepository = usersRepository;
        this.walletTransactionRepository = walletTransactionRepository;
    }

    @Value("${waafi.api.url}")
    private String waafiApiUrl;

    @Value("${waafi.payment.merchantUid}")
    private String merchantUid;

    @Value("${waafi.payment.userId}")
    private String userId;

    @Value("${waafi.payment.apiKey}")
    private String apiKey;

    @Value("${waafi.project.name}")
    private String projectName;

    @Value("${m-health.country.dialing.code}")
    private String countryCode;

    @Value("${m-health.country.currency}")
    private String currencyCode;

    @Value("${m-health.country}")
    private String mHealthCountry;

    @Value("${app.sms.sent}")
    private boolean smsSent;

    @Autowired
    private SMSApiService smsApiService;

    @Autowired
    private ProcessPayment processPayment;

    @Override
    public boolean supports(PaymentAggregator paymentAggregator) {
        return paymentAggregator.equals(PaymentAggregator.WAAFI);
    }

    @Override
    public Response sendPayment(String msisdn, Double amount) {
        Map<String, Object> apiParams = apiParamsInit();
        Users users = usersRepository.findByContactNumber(msisdn).orElseThrow(() -> new AdminModuleExceptionHandler("User not found"));
        apiParams.put("serviceParams", generatePaymentParams(users, amount));

        //Prepare the request
        Map<String, Object> response = null; //sendRequestToWaafi(apiParams, msisdn);

        if (response != null) {
            if ("0".equals(response.get("errorCode"))) {
                Map<String, Object> customResponse = new HashMap<>();
                customResponse.put("transactionId", response.get("params.transactionId"));
                customResponse.put("issuerTransactionId", response.get("params.issuerTransactionId"));
                customResponse.put("referenceId", response.get("params.referenceId"));
                customResponse.put("currency", currencyCode);
                customResponse.put("state", response.get("responseMsg"));
                String message = "Payment Successful for " + projectName + " for case id : " + users.getUserId() + " with transaction id : " + response.get("params.transactionId");
                sendPaymentNotification("+" + countryCode + msisdn, message);
                processPayment.completePayment();
                return new Response(Status.SUCCESS, Constants.SUCCESS_CODE, null, customResponse);
            } else {
                String errorMessage = getErrorMessage(response);
                // TODO: Notification will sent
                sendPaymentNotification("+" + countryCode + msisdn, errorMessage);
                processPayment.failedPayment();
                return new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE, errorMessage);
            }
        } else {
            // TODO: Notification will sent
            sendPaymentNotification("Payment Failed", msisdn);
            return new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE, "Payment Failed");
        }

    }

    @Override
    public Response reversePayment(String msisdn, String transactionId) {

        Users users = usersRepository.findByContactNumber(msisdn).orElseThrow(() -> new AdminModuleExceptionHandler("User not found"));

        WalletTransaction walletTransaction = walletTransactionRepository.findByPatientIdAndTransactionIdAndIsDebitCredit(users, transactionId, "debit").orElseThrow(() -> new AdminModuleExceptionHandler("Transaction not found"));
        String referenceNumber = StringUtils.isEmpty(walletTransaction.getReferenceNumber()) ? users.getUserId().toString() : walletTransaction.getReferenceNumber();

        Map<String, Object> apiParams = apiParamsInit();
        apiParams.put("serviceName", PaymentTypes.API_REFUND.toString());
        apiParams.put("serviceParams", generateRefundParams(walletTransaction, referenceNumber));

        //Prepare the request
        Map<String, Object> response = sendRequestToWaafi(apiParams, msisdn);

        if (response != null) {
            if ("0".equals(response.get("errorCode"))) {
                Map<String, Object> customResponse = new HashMap<>();
                customResponse.put("transactionId", response.get("params.transactionId"));
                customResponse.put("issuerTransactionId", response.get("params.issuerTransactionId"));
                customResponse.put("referenceId", response.get("params.referenceId"));
                customResponse.put("currency", currencyCode);
                customResponse.put("state", response.get("responseMsg"));
                //Notification will not be sent
                return new Response(Status.SUCCESS, Constants.SUCCESS_CODE, null, customResponse);
            } else {
                String errorMessage = getErrorMessage(response);
                return new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE, errorMessage);
            }
        } else {
            return new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE, "Payment Failed");
        }

    }

    private Map<String, Object> sendRequestToWaafi(Map<String, Object> apiParams, String msisdn) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(apiParams, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(waafiApiUrl, HttpMethod.POST, requestEntity, String.class);
        String responseBody = responseEntity.getBody();

        Map<String, Object> response = null;

        try {
            response = new ObjectMapper().readValue(responseBody, new TypeReference<>() {});

            // Logging
            log.info(String.format(
                    "[PAYMENT_FROM : %s][REQUEST_MODE : orderPayment][REQUEST : %s][RESPONSE : %s][DATE : %s]%n%n",
                    countryCode + msisdn,
                    apiParams,
                    new ObjectMapper().writeValueAsString(response),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(Instant.now())
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private Map<String, Object> apiParamsInit() {
        Map<String, Object> apiParams = new HashMap<>();
        apiParams.put("schemaVersion", "1.0");
        apiParams.put("requestId", Instant.now().getEpochSecond());
        apiParams.put("timestamp", Instant.now().getEpochSecond());
        apiParams.put("channelName", "WEB");
        return apiParams;
    }

    private Map<String, Object> generatePaymentParams(Users users, Double amount) {
        Map<String, Object> serviceParams = new HashMap<>();
        serviceParams.put("merchantUid", merchantUid);
        serviceParams.put("apiUserId", userId);
        serviceParams.put("apiKey", apiKey);
        serviceParams.put("paymentMethod", "mwallet_account");
        serviceParams.put("payerInfo", Map.of("accountNo", countryCode + users.getContactNumber()));
        serviceParams.put("transactionInfo", Map.of(
                "invoiceId", Instant.now().getEpochSecond(),
                "amount", amount,
                "description", "Payment from " + projectName,
                "referenceId", users.getUserId(),
                "currency", currencyCode
        ));
        return serviceParams;
    }

    private Map<String, Object> generateRefundParams(WalletTransaction walletTransaction, String referenceNumber) {

        Map<String, Object> serviceParams = new HashMap<>();
        serviceParams.put("merchantUid", merchantUid);
        serviceParams.put("apiUserId", userId);
        serviceParams.put("apiKey", apiKey);
        serviceParams.put("userReferenceId", walletTransaction.getTransactionId());
        serviceParams.put("transactionId", walletTransaction.getTransactionId());
        serviceParams.put("amount", walletTransaction.getAmount());
        serviceParams.put("description", "Refunded from " + projectName + " for case id : " + referenceNumber);
        serviceParams.put("referenceId", referenceNumber);
        return serviceParams;
    }

    private String getErrorMessage(Map<String, Object> response) {
        String errorCode = (String) response.get("errorCode");
        return switch (errorCode) {
            case "E10205" -> "Incorrect PIN";
            case "5310" -> "Payment cancelled or rejected";
            default -> "Payment failed";
        };
    }

    private void sendPaymentNotification(String msisdn, String message) {
        if(smsSent){
            String patientNumber = "+" + countryCode + msisdn;
            smsApiService.sendMessage(patientNumber, message, mHealthCountry);
        }
    }
}
