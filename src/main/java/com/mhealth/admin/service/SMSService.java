package com.mhealth.admin.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SMSService {
    @Value("${sms.api.url}")
    private String smsApiUrl;

    @Value("${sms.password}")
    private String password;

    @Value("${sms.from}")
    private String from;

    @Value("${sms.username}")
    private String username;

    public void sendOTPSMS(String to, String msg) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(smsApiUrl)
                    .addParameter("charset", "UTF-8")
                    .addParameter("password", password)
                    .addParameter("from", from)
                    .addParameter("to", to)
                    .addParameter("text", msg)
                    .addParameter("username", username);

            String requestUrl = uriBuilder.build().toString();
            log.info("Sending SMS - Request URL: {}", requestUrl);

            HttpGet request = new HttpGet(requestUrl);
            request.setHeader("Accept", "application/json");

            client.execute(request, httpResponse -> {
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                log.info("Received SMS - Response: {}", responseBody);
                return responseBody;
            });

        } catch (Exception e) {
            log.error("Failed to send SMS", e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
}
