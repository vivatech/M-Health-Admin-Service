package com.mhealth.admin.report.controller;


import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.RequestType;
import com.mhealth.admin.dto.enums.YesNo;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.*;
import com.mhealth.admin.report.controller.dto.HealthTipsPaymentDto;
import com.mhealth.admin.report.controller.dto.PaymentHistoryDto;
import com.mhealth.admin.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.criteria.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Payment History", description = "APIs for managing payment history")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/payment-history")
public class PaymentHistoryController {

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

    @Operation(summary = "Get payment history", description = "Fetch payment history details based on package patient name, doctor name and consultation date with pagination")
    @GetMapping("/get")
    public PaginationResponse<PaymentHistoryDto> getPaymentHistory(
            @RequestParam(value = "patientName", required = false) String patientName,
            @RequestParam(value = "doctorName", required = false) String doctorName,
            @RequestParam(value = "consultationDate", required = false) String consultationDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            LocalDate temConsultationDate = (consultationDate == null || consultationDate.isEmpty()) ? null : LocalDate.parse(consultationDate);

            Page<Orders> orderList = ordersRepository.fetchOrders(patientName, doctorName, temConsultationDate, RequestType.Book, pageable);

            List<PaymentHistoryDto> paymentHistoryDtos = orderList.getContent().stream()
                    .map(order -> {
                        WalletTransaction walletTransaction = walletTransactionRepository.findByOrderIdAndPatientIdAndServiceTypeAndisDebitCredit(
                                order.getId(), order.getPatientId().getUserId());

                        if (walletTransaction == null) {
                            return null; // Skip this order if walletTransaction is null
                        }

                        PaymentHistoryDto dto = new PaymentHistoryDto();
                        dto.setPatientName(order.getPatientId().getFirstName() + " " + order.getPatientId().getLastName());
                        dto.setDoctorName(order.getDoctorId().getFirstName() + " " + order.getDoctorId().getLastName());
                        dto.setDoctorCharge(order.getDoctorAmount());
                        dto.setPatientPaidCharge(order.getAmount());
                        dto.setCaseId(order.getCaseId().getCaseId());
                        dto.setAdminCharge(order.getCommission());

                        dto.setTransactionId(walletTransaction.getTransactionId());

                        Consultation consultation = consultationRepository.findByCaseIdAndRequestType(order.getCaseId().getCaseId(), RequestType.Book);
                        dto.setConsultationDate(consultation.getConsultationDate());
                        dto.setConsultationType(consultation.getConsultType());
                        dto.setConsultationTime(consultation.getSlotId().getSlotTime());

                        Users hospital = usersRepository.findById(order.getDoctorId().getHospitalId()).orElse(null);
                        dto.setClinicName(hospital != null ? hospital.getClinicName() : null);

                        return dto;
                    })
                    .filter(Objects::nonNull) // Filter out null PaymentHistoryDto objects
                    .collect(Collectors.toList());


            return new PaginationResponse<>(Status.SUCCESS, Constants.SUCCESS, "", paymentHistoryDtos, orderList.getTotalElements(), (long) size, (long) page);

        } catch (Exception e) {
            e.printStackTrace();
            return new PaginationResponse<>(e);
        }
    }



}
