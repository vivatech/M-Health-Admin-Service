package com.mhealth.admin.payment;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.B2CPaymentDto;
import com.mhealth.admin.dto.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Admin Module Payment Operations", description = "APIs for Payment Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Value("${m-health.country}")
    private String mHealthCountry;

    @Operation(summary = "Create a B2C payment by add msisdn and amount in the request body", responses = {
            @ApiResponse(responseCode = "200", description = "Payment Success"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid"),
            @ApiResponse(responseCode = "405", description = "Payment is already in progress")
    })
    @PostMapping("/send-b2c")
    public ResponseEntity<Response> createPayment(
            @RequestBody B2CPaymentDto request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        if (StringUtils.isEmpty(request.getMsisdn()) || request.getAmount() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE, "Request body is invalid"));
        }
        return ResponseEntity.ok(paymentService.sendPayment(request.getMsisdn(), request.getAmount(), mHealthCountry));
    }

    @PostMapping("/refund-payment")
    public ResponseEntity<Response> refundPayment(@RequestBody B2CPaymentDto request) {
        return ResponseEntity.ok(paymentService.refundPayment(request.getMsisdn(), request.getTransactionId(), mHealthCountry));
    }
}
