package com.mhealth.admin.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.exception.AdminModuleExceptionHandler;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class SafariPayment implements PaymentInterface{
    private final UsersRepository usersRepository;

    public SafariPayment(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Value("${safaricom.api.base-url}")
    private String baseUrl;

    @Value("${safaricom.api.consumer-key}")
    private String consumerKey;

    @Value("${safaricom.api.consumer-secret}")
    private String consumerSecret;

    @Value("${safaricom.api.business-shortcode}")
    private String businessShortCode;

    @Value("${safaricom.b2c.queue-timeout-url}")
    private String b2cQueueTimeoutUrl;

    @Value("${safaricom.b2c.result-url}")
    private String b2cResultUrl;

    @Value("${safaricom.b2c.security-credential}")
    private String b2cSecurityCredential;

    @Value("${safaricom.b2c.initiator-name}")
    private String b2cInitiatorName;

    @Value("${safaricom.b2c.party-a}")
    private String b2cPartyA;

    @Value("${safaricom.reversal.queue-timeout-url}")
    private String reversalQueueTimeoutUrl;

    @Value("${safaricom.reversal.result-url}")
    private String reversalResultUrl;

    @Value("${safaricom.reversal.security-credential}")
    private String reversalSecurityCredential;

    @Value("${safaricom.reversal.initiator}")
    private String reversalInitiator;

    @Value("${safaricom.reversal.receiver-party}")
    private String reversalReceiverParty;

    @Value("${safaricom.reversal.receiver-identifier-type}")
    private String reversalReceiverIdentifierType;

    @Override
    public boolean supports(PaymentAggregator paymentAggregator) {
        return paymentAggregator.equals(PaymentAggregator.SAFARI);
    }

    @Override
    public Response sendPayment(String msisdn, Double amount) {
        return makeB2CPayment(msisdn, String.valueOf(amount));
    }

    @Override
    public Response reversePayment(String msisdn, String transactionId) {
        return null;
    }

    private String generateRefNum() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
        return LocalDateTime.now().format(formatter);
    }

    public HashMap<String, Object> getAccessToken() {
        // Build the URL
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/oauth/v1/generate")
                .queryParam("grant_type", "client_credentials")
                .toUriString();

        // Create the Authorization header
        String credentials = consumerKey + ":" + consumerSecret;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encodedCredentials);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);

        // Logging the request
        log.info("Sending Request To URL: {}", url);
        log.info("Request Headers: {}", headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            // Logging the response
            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());

            // Parse the response to a HashMap
            HashMap<String, Object> responseMap = new ObjectMapper().readValue(response.getBody(), HashMap.class);

            // Logging the parsed response map
            log.info("Parsed Response Map: {}", responseMap);

            return responseMap;

        } catch (Exception e) {
            log.error("Error occurred while fetching access token: {}", e.getMessage(), e);
            throw new AdminModuleExceptionHandler("Failed to fetch access token: " + e.getMessage());
        }
    }

    public Response makeB2CPayment(String msisdn, String amount) {
        Users users = usersRepository.findByContactNumber(msisdn).orElseThrow(() -> new AdminModuleExceptionHandler("User not found"));
        // Generate Reference Number
        String refNumber = generateRefNum();

        // Fetch the access token
        HashMap<String, Object> accessTokenResponse = getAccessToken();
        String accessToken = (String) accessTokenResponse.get("access_token");

        // Build the URL
        String b2cUrl = baseUrl + "/mpesa/b2c/v1/paymentrequest";

        // Build Request Payload
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("OriginatorConversationID", UUID.randomUUID().toString());
        requestBody.put("InitiatorName", b2cInitiatorName);
        requestBody.put("SecurityCredential", b2cSecurityCredential);
        requestBody.put("CommandID", "BusinessPayment");
        requestBody.put("Amount", amount);
        requestBody.put("PartyA", b2cPartyA);
        requestBody.put("PartyB", msisdn);
        requestBody.put("Remarks", "Business Payment");
        requestBody.put("QueueTimeOutURL", b2cQueueTimeoutUrl);
        requestBody.put("ResultURL", b2cResultUrl);
        requestBody.put("Occasion", refNumber);


        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Logging the request
        log.info("Sending request to URL: {}", b2cUrl);
        log.info("Request Headers: {}", headers);
        log.info("Request Body: {}", requestBody);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Send Request
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    b2cUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // Logging the response
            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());



            return new Response(Status.SUCCESS, Constants.SUCCESS_CODE, null, response.getBody());

        } catch (Exception e) {
            log.error("Error while making B2C payment: {}", e.getMessage(), e);
            throw new AdminModuleExceptionHandler("Error while making B2C payment: " + e.getMessage());
        }
    }
}
