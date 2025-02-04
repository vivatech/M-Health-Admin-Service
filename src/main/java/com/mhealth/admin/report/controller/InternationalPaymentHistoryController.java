package com.mhealth.admin.report.controller;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.RequestType;
import com.mhealth.admin.dto.enums.UserType;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.*;
import com.mhealth.admin.report.controller.dto.InternationalPaymentHistoryDto;
import com.mhealth.admin.report.controller.dto.PaymentHistoryDto;
import com.mhealth.admin.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "International Payment History", description = "APIs for managing international payment history")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/international-payment-history")
public class InternationalPaymentHistoryController {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private SlotMasterRepository slotMasterRepository;

    @Autowired
    private GroupPaymentRepository groupPaymentRepository;

    @Operation(summary = "Get international payment history", description = "Fetch international payment history details based on package transaction id and payment date with pagination")
    @GetMapping("/get")
    public PaginationResponse<InternationalPaymentHistoryDto> getInternationalPaymentHistory(
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "paymentDate", required = false) String paymentDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "paymentDate"));

            LocalDateTime startOfDay = null;
            LocalDateTime endOfDay = null;

            if (paymentDate != null && !paymentDate.isEmpty()) {
                try {
                    // Parse the date (assume format "yyyy-MM-dd")
                    LocalDate localDate = LocalDate.parse(paymentDate);

                    // Set start and end of the day
                    startOfDay = localDate.atStartOfDay();
                    endOfDay = localDate.atTime(23, 59, 59);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid date format. Expected format: yyyy-MM-dd");
                }
            }

            // Fetch data with date range
            Page<GroupPayment> paymentList = groupPaymentRepository.findByTransactionIdAndPaymentDateRangeAndUserType(
                    transactionId,
                    startOfDay,
                    endOfDay,
                    UserType.InternationalDoctor,
                    pageable
            );

            List<InternationalPaymentHistoryDto> paymentHistoryDtos = new ArrayList<>();

            for (GroupPayment payment : paymentList.getContent()) {
                InternationalPaymentHistoryDto paymentHistoryDto = new InternationalPaymentHistoryDto();

                paymentHistoryDto.setTransactionId(payment.getTransactionId());
                paymentHistoryDto.setAmount(String.valueOf(payment.getAmount())); // Assuming amount is of type float
                paymentHistoryDto.setPaymentDate(payment.getPaymentDate().toString()); // Assuming paymentDate is of type LocalDateTime

                 paymentHistoryDto.setDoctorId(payment.getUserId().getUserId());
                 paymentHistoryDto.setDoctorName(payment.getUserId().getFirstName() + " " + payment.getUserId().getLastName());

                paymentHistoryDtos.add(paymentHistoryDto);
            }

            return new PaginationResponse<>(Status.SUCCESS, Constants.SUCCESS, "", paymentHistoryDtos, paymentList.getTotalElements(), (long) size, (long) page);

        } catch (Exception e) {
            // Handle and return error response
            return new PaginationResponse<>(e);
        }
    }


}
