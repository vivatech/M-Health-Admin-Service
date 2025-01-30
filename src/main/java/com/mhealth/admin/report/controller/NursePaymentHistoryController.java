package com.mhealth.admin.report.controller;


import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.RequestType;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.*;
import com.mhealth.admin.report.controller.dto.NursePaymentHistoryDto;
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
import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Nurse Payment History", description = "APIs for managing nurse payment history")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/nurse-payment-history")
public class NursePaymentHistoryController {


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
    private NurseServiceStateRepository nurseServiceStateRepository;

    @Autowired
    private NurseDemandOrdersRepository nurseDemandOrdersRepository;

    @Autowired
    private PartnerNurseRepository partnerNurseRepository;

    @Operation(summary = "Get nurse payment history", description = "Fetch nurse payment history details based on package patient name, nurse name and consultation date with pagination")
    @GetMapping("/get")
    public PaginationResponse<NursePaymentHistoryDto> getNursePaymentHistory(
            @RequestParam(value = "patientName", required = false) String patientName,
            @RequestParam(value = "nurseName", required = false) String nurseName,
            @RequestParam(value = "consultationDate", required = false) String consultationDate,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        try {
            // Set pagination and sorting
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

            LocalDate tempConsultationDate = (consultationDate == null || consultationDate.isEmpty()) ? null : LocalDate.parse(consultationDate);

            // Fetch orders using custom query (searchOrders)
            Page<NurseDemandOrders> orderList = nurseDemandOrdersRepository.fetchOrders(patientName, nurseName, tempConsultationDate, pageable);

            // List to hold payment history DTOs
            List<NursePaymentHistoryDto> paymentHistoryDtos = new ArrayList<>();

            for (NurseDemandOrders order : orderList.getContent()) {
                NursePaymentHistoryDto dto = new NursePaymentHistoryDto();

                // Set patient and doctor names
                dto.setPatientName(order.getPatientId().getFirstName() + " " + order.getPatientId().getLastName());
                dto.setNurseName(order.getNurseId().getName());

                // Set doctor charge and patient paid charge
                dto.setAdminCharge(order.getCommission());
                dto.setPaymentStatus(order.getPaymentStatus());
                dto.setPaidType(order.getCurrency());
                dto.setId(order.getId());
                dto.setNurseCharge(order.getCurrency() + " " + order.getServiceAmount() + " / SLSH " + order.getSlshServiceAmount() );
                dto.setPatientPaidCharge(order.getCurrency() + " " + order.getAmount() + " / SLSH " + order.getSlshAmount());
                dto.setConsultationDate(order.getCreatedAt());

                dto.setPaymentTransferToNurse(order.getIsTransfered());
                dto.setTransactionId(order.getTripId());

                String serviceState = nurseServiceStateRepository.findBySearchId(order.getTripId());
                dto.setServiceStatus(serviceState);

                // Add the payment history DTO to the list
                paymentHistoryDtos.add(dto);
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
