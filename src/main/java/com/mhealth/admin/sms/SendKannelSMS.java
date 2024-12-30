package com.mhealth.admin.sms;

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
public class SendKannelSMS implements SendMessages {


    @Value("${kannel.api.url}")
    private String smsApiUrl;

    @Value("${kannel.password}")
    private String password;

    @Value("${kannel.from}")
    private String from;

    @Value("${kannel.username}")
    private String username;


    @Override
    public boolean supports(SMSAggregator smsAggregator) {
        return smsAggregator.equals(SMSAggregator.KANNEL);
    }

    @Override
    public void sendSms(String msisdn, String message) {
        sendSMSByKANNEL(msisdn, message);
    }

    public void sendSMSByKANNEL(String to, String msg) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URIBuilder uriBuilder = new URIBuilder(smsApiUrl)
                    .addParameter("charset", "UTF-8")
                    .addParameter("password", password)
                    .addParameter("from", from)
                    .addParameter("to", to)
                    .addParameter("text", msg)
                    .addParameter("username", username);

            String requestUrl = uriBuilder.build().toString();
            log.info("Sending SMS To Kannel - Request URL: {}", requestUrl);

            HttpGet request = new HttpGet(requestUrl);
            request.setHeader("Accept", "application/json");

            client.execute(request, httpResponse -> {
                String responseBody = EntityUtils.toString(httpResponse.getEntity());
                log.info("Received SMS From Kannel - Response: {}", responseBody);
                return true;
            });

        } catch (Exception e) {
            log.error("exception occurred while sending sms via kannel: ", e);
        }
    }
}
