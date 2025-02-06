package com.mhealth.admin.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.request.B2CPaymentDto;
import com.mhealth.admin.dto.response.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Locale;

@Slf4j
@RestController
@Tag(name = "Admin Module Payment Operations", description = "APIs for Payment Operations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/payment")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${m-health.country}")
    private String mHealthCountry;

    @Autowired
    private ProcessConsultationRequest consultationService;

    @Operation(summary = "Create a B2C payment by add msisdn and amount in the request body", responses = {
            @ApiResponse(responseCode = "200", description = "Payment Success"),
            @ApiResponse(responseCode = "400", description = "Request body is invalid"),
            @ApiResponse(responseCode = "405", description = "Payment is already in progress")
    })
    @Transactional
    @PostMapping("/send-b2c")
    public ResponseEntity<Response> createPayment(
            @RequestBody B2CPaymentDto request,
            @RequestHeader(name = "country-code", required = false, defaultValue = Constants.DEFAULT_COUNTRY) String countryCode,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        if (StringUtils.isEmpty(request.getMsisdn()) || request.getAmount() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE, "Request body is invalid"));
        }
        PaymentDto paymentDto = PaymentDto.builder()
                .paymentNumber(request.getMsisdn())
                .amount(request.getAmount())
                .transactionInitiatedBy(request.getTransactionInitiatedBy())
                .transactionType(PaymentTypes.B2C)
                .build();
        return ResponseEntity.ok(paymentService.sendPayment(paymentDto, countryCode.toUpperCase()));
    }

    @PostMapping("/send-c2b")
    public ResponseEntity<Response> sendCustomerToBusinessPayment(
            @RequestBody B2CPaymentDto request,
            @RequestHeader(name = "country-code", required = false, defaultValue = Constants.DEFAULT_COUNTRY) String countryCode,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        if (StringUtils.isEmpty(request.getMsisdn()) || request.getAmount() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(Status.FAILED, Constants.INTERNAL_SERVER_ERROR_CODE, "Request body is invalid"));
        }

        PaymentDto paymentDto = PaymentDto.builder()
                .paymentNumber(request.getMsisdn())
                .amount(request.getAmount())
                .transactionInitiatedBy(request.getTransactionInitiatedBy())
                .transactionType(PaymentTypes.B2C)
                .build();

        return ResponseEntity.ok(paymentService.sendPayment(paymentDto, countryCode.toUpperCase()));
    }

    //agent is booking consultation on the behalf of patient
    @PostMapping("/consultation-booking")
    public ResponseEntity<Response> bookConsultationForPatient(
            @RequestBody ConsultationPaymentDto request,
            @RequestHeader(name = "country-code", required = false, defaultValue = Constants.DEFAULT_COUNTRY) String countryCode,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        request.setCountry(countryCode);
        return ResponseEntity.ok(consultationService.paymentGateway(request, locale));
    }



    @PostMapping("/refund-payment")
    public ResponseEntity<Response> refundPayment(@RequestBody B2CPaymentDto request) {
        return ResponseEntity.ok(paymentService.refundPayment(request.getMsisdn(), request.getTransactionId(), mHealthCountry));
    }

    @RequestMapping(value = "/refund", method = RequestMethod.GET)
    public ResponseEntity<?> getRefundList(@RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale,
                                                    @RequestParam(required = false) Integer caseId,
                                                    @RequestParam(required = false) String doctorName,
                                                    @RequestParam(required = false) String patientName,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                    @RequestParam(required = false) String sortField,
                                                    @RequestParam(defaultValue = "1") String sortBy,
                                                    @RequestParam(defaultValue = "1") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        try {
            log.info("Request Received For /api/v1/admin/payment/refund");
            log.info("Request Parameters: caseId={}, doctorName={}, patientName={}, startDate={}, endDate={}, sortField={}, sortBy={}, page={}, size={}", caseId, doctorName, patientName, startDate, endDate, sortField, sortBy, page, size);

            Object response = paymentService.getRefundList(locale, caseId, doctorName, patientName, startDate, endDate, sortField, sortBy, page, size);

            log.info("Response Sent For /api/v1/admin/payment/refund: {}", objectMapper.writeValueAsString(response));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception: ", e);
            return new ResponseEntity<>(com.mhealth.admin.constants.Constants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
