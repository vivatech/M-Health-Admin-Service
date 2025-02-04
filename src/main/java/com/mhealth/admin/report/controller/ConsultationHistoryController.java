package com.mhealth.admin.report.controller;

import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.ConsultationType;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.Consultation;
import com.mhealth.admin.model.Orders;
import com.mhealth.admin.model.SlotMaster;
import com.mhealth.admin.model.Users;
import com.mhealth.admin.report.controller.dto.ConsultationHistoryDTO;
import com.mhealth.admin.report.controller.dto.ConsultationHistoryRequestDto;
import com.mhealth.admin.report.controller.dto.PatientTransactionDto;
import com.mhealth.admin.repository.ConsultationRepository;
import com.mhealth.admin.repository.OrdersRepository;
import com.mhealth.admin.repository.SlotMasterRepository;
import com.mhealth.admin.repository.UsersRepository;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Consultation History", description = "APIs for managing consultation history")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/consultation-history")
public class ConsultationHistoryController {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private SlotMasterRepository slotMasterRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    // Optimized Controller Method
    @PostMapping("/get")
    @Operation(summary = "Get consultation history", description = "Fetch consultation history details with filters and pagination")
    public PaginationResponse<ConsultationHistoryDTO> getConsultationHistory(
            @RequestBody ConsultationHistoryRequestDto request) {

        try {
            // Set pagination and sorting
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "consultationDate"));
            // Convert date strings to LocalDateTime
            LocalDate fromDate = (request.getFromDate() != null && !request.getFromDate().isEmpty())
                    ? LocalDate.parse(request.getFromDate())
                    : null;
            LocalDate toDate = (request.getToDate() != null && !request.getToDate().isEmpty())
                    ? LocalDate.parse(request.getToDate())
                    : null;

            // Fetch consultations with filters
            Page<Consultation> dataList = consultationRepository.fetchConsultationList(
                    request.getDoctorId(),
                    request.getStatus(),
                    request.getPatientName(),
                    fromDate,
                    toDate,
                    pageable
            );

            // Map consultations to DTOs
            List<ConsultationHistoryDTO> consultationHistoryDTOS = dataList.stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());

            // Return the paginated response
            return new PaginationResponse<>(
                    Status.SUCCESS,
                    Constants.SUCCESS,
                    "",
                    consultationHistoryDTOS,
                    dataList.getTotalElements(),
                    (long) request.getSize(),
                    (long) request.getPage()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new PaginationResponse<>(e);
        }
    }

    // Optimized Mapping Method
    private ConsultationHistoryDTO mapToDto(Consultation in) {
        ConsultationHistoryDTO dto = new ConsultationHistoryDTO();

        dto.setCaseId(in.getCaseId());
        dto.setPatientName(in.getPatientId().getFirstName() + " " + in.getPatientId().getLastName());
        dto.setDoctorName(in.getDoctorId().getFirstName() + " " + in.getDoctorId().getLastName());
        dto.setConsultationDate(in.getConsultationDate());
        dto.setTime(in.getSlotId().getSlotTime());
        dto.setConsultType(in.getConsultType());
        dto.setStatus(in.getRequestType());
        dto.setConsultationType(in.getConsultationType());

        if (ConsultationType.Paid.equals(in.getConsultationType())) {
            Orders orders = ordersRepository.findByCaseId(in);
            dto.setCharge(orders != null ? orders.getCurrency() + " " + orders.getAmount() : "");
        } else {
            dto.setCharge("Free");
        }
        return dto;
    }
}
