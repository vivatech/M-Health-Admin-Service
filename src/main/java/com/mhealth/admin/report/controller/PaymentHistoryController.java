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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Payment History", description = "APIs for managing payment history")
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
    public PaginationResponse<PaymentHistoryDto> getPaymentHistory(@RequestParam(value = "patientName", required = false) String patientName, @RequestParam(value = "doctorName", required = false) String doctorName, @RequestParam(value = "consultationDate", required = false) String consultationDate, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            // Set pagination and sorting
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            LocalDate temConsultationDate = (consultationDate == null || consultationDate.isEmpty()) ? null : LocalDate.parse(consultationDate);

            // Fetch orders using custom query (searchOrders)
            Page<Orders> orderList = ordersRepository.fetchOrders(patientName, doctorName, temConsultationDate, pageable);

            // List to hold payment history DTOs
            List<PaymentHistoryDto> paymentHistoryDtos = new ArrayList<>();

            for (Orders order : orderList.getContent()) {
                PaymentHistoryDto paymentHistoryDto = new PaymentHistoryDto();

                // Set patient and doctor names
                paymentHistoryDto.setPatientName(order.getPatientId().getFirstName() + " " + order.getPatientId().getLastName());
                paymentHistoryDto.setDoctorName(order.getDoctorId().getFirstName() + " " + order.getDoctorId().getLastName());

                // Set doctor charge and patient paid charge
                paymentHistoryDto.setDoctorCharge(order.getDoctorAmount());
                paymentHistoryDto.setPatientPaidCharge(order.getAmount());
                paymentHistoryDto.setCaseId(order.getCaseId().getCaseId());
                paymentHistoryDto.setAdminCharge(order.getCommission());

                // Find wallet transaction for the order (debit type)
                WalletTransaction walletTransaction = walletTransactionRepository.findByOrderIdAndPatientIdAndServiceTypeAndisDebitCredit(order.getId(), order.getPatientId().getUserId());
                if (walletTransaction == null) {
                    continue;  // This skips the record; user might see fewer results than the requested page size
                }
                paymentHistoryDto.setTransactionId(walletTransaction.getTransactionId());

                // Find consultation based on caseId and requestType
                Consultation consultation = consultationRepository.findByCaseIdAndRequestType(order.getCaseId().getCaseId(), RequestType.Book);
                if (consultation == null) {
                    continue;  // Same as above
                }

                // Set consultation details
                paymentHistoryDto.setConsultationDate(consultation.getConsultationDate());
                paymentHistoryDto.setConsultationType(consultation.getConsultType());
                paymentHistoryDto.setConsultationTime(consultation.getSlotId().getSlotTime());

                // Find hospital details and set clinic name
                Integer hospitalId = order.getDoctorId().getHospitalId();
                Users hospital = usersRepository.findById(hospitalId).orElse(null);
                if (hospital == null) {
                    continue;  // Same as above
                }
                paymentHistoryDto.setClinicName(hospital.getClinicName());

                // Add the payment history DTO to the list
                paymentHistoryDtos.add(paymentHistoryDto);
            }


            // Return the paginated response
            return new PaginationResponse<>(Status.SUCCESS, Constants.SUCCESS, "", paymentHistoryDtos, orderList.getTotalElements(), (long) size, (long) page);

        } catch (Exception e) {
            e.printStackTrace();
            // Handle and return error response
            return new PaginationResponse<>(e);
        }
    }


}
