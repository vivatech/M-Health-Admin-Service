package com.mhealth.admin.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SafaricomPaymentGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(SafaricomPaymentGatewayService.class);

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

    @Autowired
    private ObjectMapper objectMapper;

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
        logger.info("Sending Request To URL: {}", url);
        logger.info("Request Headers: {}", headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

            // Logging the response
            logger.info("Response Status: {}", response.getStatusCode());
            logger.info("Response Body: {}", response.getBody());

            // Parse the response to a HashMap
            HashMap<String, Object> responseMap = objectMapper.readValue(response.getBody(), HashMap.class);

            // Logging the parsed response map
            logger.info("Parsed Response Map: {}", responseMap);

            return responseMap;

        } catch (Exception e) {
            logger.error("Error occurred while fetching access token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch access token: " + e.getMessage(), e);
        }
    }

    public HashMap<String, Object> simulateC2BTransaction(String msisdn, String amount) {
        // Generate Reference Number
        String refNumber = generateRefNum();

        // Fetch the access token
        HashMap<String, Object> accessTokenResponse = getAccessToken();
        String accessToken = (String) accessTokenResponse.get("access_token");

        // Build the URL
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/mpesa/c2b/v1/simulate").toUriString();

        // Create the request body
        HashMap<String, Object> requestBody = new HashMap<>();
        requestBody.put("ShortCode", Integer.parseInt(businessShortCode));
        requestBody.put("CommandID", "CustomerPayBillOnline");
        requestBody.put("Amount", amount);
        requestBody.put("Msisdn", Long.parseLong(msisdn));
        requestBody.put("BillRefNumber", refNumber);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(requestBody, headers);

        // Logging the request
        logger.info("Sending request to URL: {}", url);
        logger.info("Request Headers: {}", headers);
        logger.info("Request Body: {}", requestBody);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            // Logging the response
            logger.info("Response Status: {}", response.getStatusCode());
            logger.info("Response Body: {}", response.getBody());

            // Parse and return the response as HashMap
            HashMap<String, Object> responseMap = objectMapper.readValue(response.getBody(), HashMap.class);
            logger.info("Parsed Response Map: {}", responseMap);

            return responseMap;

        } catch (Exception e) {
            logger.error("Error occurred during C2B transaction simulation: {}", e.getMessage(), e);
            throw new RuntimeException("C2B transaction simulation failed: " + e.getMessage(), e);
        }
    }

    public HashMap<String, Object> makeB2CPayment(String msisdn, String amount) {
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
        logger.info("Sending request to URL: {}", b2cUrl);
        logger.info("Request Headers: {}", headers);
        logger.info("Request Body: {}", requestBody);

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
            logger.info("Response Status: {}", response.getStatusCode());
            logger.info("Response Body: {}", response.getBody());

            // Parse and return the response as HashMap
            HashMap<String, Object> responseMap = objectMapper.readValue(response.getBody(), HashMap.class);
            logger.info("Parsed Response Map: {}", responseMap);

            return responseMap;

        } catch (Exception e) {
            logger.error("Error while making B2C payment: {}", e.getMessage(), e);
            throw new RuntimeException("Error while making B2C payment: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> sendC2BReversalRequest(String transactionId, String amount) {
        // Generate Reference Number
        String refNumber = generateRefNum();

        // Fetch the access token
        HashMap<String, Object> accessTokenResponse = getAccessToken();
        String accessToken = (String) accessTokenResponse.get("access_token");


        // Build the URL
        String c2bReversalUrl = baseUrl + "/mpesa/reversal/v1/request";

        // Prepare the payload
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("Initiator", reversalInitiator);
        requestBody.put("SecurityCredential", reversalSecurityCredential);
        requestBody.put("CommandID", "TransactionReversal");
        requestBody.put("TransactionID", transactionId);
        requestBody.put("Amount", amount);
        requestBody.put("ReceiverParty", reversalReceiverParty);
        requestBody.put("RecieverIdentifierType", reversalReceiverIdentifierType);
        requestBody.put("ResultURL", reversalResultUrl);
        requestBody.put("QueueTimeOutURL", reversalQueueTimeoutUrl);
        requestBody.put("Remarks", "Reversal");
        requestBody.put("Occasion", refNumber);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Logging the request
        logger.info("Sending request to URL: {}", c2bReversalUrl);
        logger.info("Request Headers: {}", headers);
        logger.info("Request Body: {}", requestBody);

        // Build the request entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Send Request
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(c2bReversalUrl, HttpMethod.POST, requestEntity, String.class);

            // Logging the response
            logger.info("Response Status: {}", response.getStatusCode());
            logger.info("Response Body: {}", response.getBody());

            // Parse and return the response as HashMap
            HashMap<String, Object> responseMap = objectMapper.readValue(response.getBody(), HashMap.class);
            logger.info("Parsed Response Map: {}", responseMap);

            return responseMap;
        } catch (Exception e) {
            logger.error("Error while making reversal payment: {}", e.getMessage(), e);
            throw new RuntimeException("Error while making reversal payment: " + e.getMessage(), e);
        }
    }

    private String generateRefNum() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
        return LocalDateTime.now().format(formatter);
    }
}
