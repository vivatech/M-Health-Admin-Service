package com.mhealth.admin.payment;

import com.mhealth.admin.dto.B2CCallbackRequestDto;
import com.mhealth.admin.dto.C2BReversalCallbackRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MPaisaCallbackService {

    @Autowired
    private ProcessPayment processPayment;

    public void processB2CCallback(B2CCallbackRequestDto callback) {
        // Extract and process required fields from the callback
        Map<String, Object> result = callback.getResult();
        if (result != null) {
            String resultCode = String.valueOf(result.get("ResultCode"));
            String resultDesc = String.valueOf(result.get("ResultDesc"));
            String conversationId = String.valueOf(result.get("ConversationID"));
            String transactionId = String.valueOf(result.get("TransactionID"));

            log.info("Result Code: {}", resultCode);
            log.info("Result Description: {}", resultDesc);
            log.info("Conversation ID: {}", conversationId);
            log.info("Transaction ID: {}", transactionId);

            // TODO: Handle Operation After Successful Transaction. Also, Match ConversationID To Identify The Transaction
            processPayment.completePayment();

        }
    }

    public void processC2BReversalCallback(C2BReversalCallbackRequestDto callback) {
        // Extract and process required fields from the callback
        Map<String, Object> result = callback.getResult();
        if (result != null) {
            String resultCode = String.valueOf(result.get("ResultCode"));
            String resultDesc = String.valueOf(result.get("ResultDesc"));
            String conversationId = String.valueOf(result.get("ConversationID"));
            String transactionId = String.valueOf(result.get("TransactionID"));

            log.info("Result Code: {}", resultCode);
            log.info("Result Description: {}", resultDesc);
            log.info("Conversation ID: {}", conversationId);
            log.info("Transaction ID: {}", transactionId);

            // TODO: Handle Operation After Successful Transaction. Also, Match ConversationID To Identify The Transaction
            processPayment.completePayment();
        }
    }
}
