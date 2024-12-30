package com.mhealth.admin.sms;

import com.mhealth.admin.dto.SDPAuthResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SendSdpSMS implements SendMessages{

    @Value("${sdp.base.url}")
    private String sdpBaseUrl;

    @Value("${sdp.auth.username}")
    private String sdpAuthUsername;

    @Value("${sdp.auth.password}")
    private String sdpAuthPassword;

    @Value("${sdp.bulksms.oa}")
    private String sdpBulkSmsOA;

    @Value("${sdp.bulksms.channel}")
    private String sdpBulkSmsChannel;

    @Value("${sdp.bulksms.username}")
    private String sdpBulkSmsUsername;

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public boolean supports(SMSAggregator smsAggregator) {
        return smsAggregator.equals(SMSAggregator.SDP);
    }

    @Override
    public void sendSms(String msisdn, String message) {
        sendSmsBySDP(msisdn, message);
    }

    public String getSDPAuthToken() {
        // Generate auth url
        String sdpAuthUrl = sdpBaseUrl = "/api/auth/login";

        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("X-Requested-With", "XMLHttpRequest");
        headers.set("X-Authorization", "Bearer");

        // Request body
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", sdpAuthUsername);
        requestBody.put("password", sdpAuthPassword);

        // Create HTTP request entity
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Request log
        log.info("Auth Request Send To SDP: {}", requestEntity);

        // Send POST request
        ResponseEntity<SDPAuthResponse> response = restTemplate.exchange(
                sdpAuthUrl,
                HttpMethod.POST,
                requestEntity,
                SDPAuthResponse.class
        );

        //Response log
        log.info("Auth Response Received From SDP: {}", response);

        // Extract and return token
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody().getToken();
        } else {
            throw new RuntimeException("Failed To Authenticate With SDP.");
        }
    }

    public void sendSmsBySDP(String msisdn, String message) {
        // Generate bulk sms url
        String bulkSmsUrl = sdpBaseUrl = "/api/public/CMS/bulksms";

        // Get Auth Token
        String authToken = getSDPAuthToken();

        // Generate Unique Id
        String uniqueId = generateUniqueId();

        // Setup data set for request body
        Map<String, Object> dataSetEntry = new HashMap<>();
        dataSetEntry.put("oa", sdpBulkSmsOA);
        dataSetEntry.put("channel", sdpBulkSmsChannel);
        dataSetEntry.put("userName", sdpBulkSmsUsername);
        dataSetEntry.put("msisdn", msisdn);
        dataSetEntry.put("message", message);
        dataSetEntry.put("uniqueId", uniqueId);

        // Request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("timeStamp", String.valueOf(System.currentTimeMillis()));
        requestBody.put("dataSet", new Map[]{dataSetEntry});

        // Set up HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", "application/json");
        headers.set("X-Authorization", "Bearer " + authToken);

        // Create HTTP request entity
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        // Request log
        log.info("Bulk SMS Request Send To SDP: {}", requestEntity);

        // Send POST request
        ResponseEntity<String> response = restTemplate.exchange(
                bulkSmsUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        //Response log
        log.info("Bulk SMS Response Received From SDP: {}", response);

        // Extract and return body
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            log.info("sms sent successfully to msisdn: {}, message: {}", msisdn, message);
        } else {
            throw new RuntimeException("Failed To Send SMS Via SDP.");
        }
    }

    private String generateUniqueId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
        return "SDP" + LocalDateTime.now().format(formatter);
    }
}
