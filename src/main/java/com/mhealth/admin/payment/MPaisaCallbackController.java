package com.mhealth.admin.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.dto.B2CCallbackRequestDto;
import com.mhealth.admin.dto.C2BCallbackRequestDto;
import com.mhealth.admin.dto.C2BReversalCallbackRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/patient/callback")
public class MPaisaCallbackController {

    @Autowired
    private MPaisaCallbackService mPaisaCallbackService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/c2b")
    public ResponseEntity<?> handleC2BCallback(@RequestBody C2BCallbackRequestDto callbackRequest) {
        try {
            // Log the received callback
            String callbackData = objectMapper.writeValueAsString(callbackRequest);
            log.info("Received C2B Callback: {}", callbackData);

            // Respond to Safaricom with a success message
            Map<String, String> response = new HashMap<>();
            response.put("ResultCode", "0");
            response.put("ResultDesc", "Success");

            // TODO: Handle Operation After Successful Transaction. Also, Match BillRefNumber To Identify The Transaction

            return ResponseEntity.ok(response);
        }  catch (Exception e) {
            log.error("Error processing C2B Callback: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error processing callback");
        }

    }

    @PostMapping("/b2c")
    public ResponseEntity<String> handleB2CCallback(@RequestBody B2CCallbackRequestDto callbackRequest) {
        try {
            // Log the received callback
            String callbackData = objectMapper.writeValueAsString(callbackRequest);
            log.info("Received B2C Callback: {}", callbackData);

            // Process the callback
            mPaisaCallbackService.processB2CCallback(callbackRequest);

            // Return success response
            return ResponseEntity.ok("Callback received successfully");
        } catch (Exception e) {
            log.error("Error processing B2C Callback: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error processing callback");
        }
    }

    @PostMapping("/reversal")
    public ResponseEntity<String> handleC2BReversalCallback(@RequestBody C2BReversalCallbackRequestDto callbackRequest) {
        try {
            // Log the received callback
            String callbackData = objectMapper.writeValueAsString(callbackRequest);
            log.info("Received C2B Reversal Callback: {}", callbackData);

            // Process the callback
            mPaisaCallbackService.processC2BReversalCallback(callbackRequest);

            // Return success response
            return ResponseEntity.ok("Callback received successfully");
        } catch (Exception e) {
            log.error("Error processing C2B Reversal Callback: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error processing callback");
        }
    }
}
