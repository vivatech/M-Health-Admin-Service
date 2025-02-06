package com.mhealth.admin.report.controller;


import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.*;
import com.mhealth.admin.report.controller.dto.PatientTransactionDto;
import com.mhealth.admin.report.controller.dto.PatientTransactionRequestDto;
import com.mhealth.admin.repository.CityRepository;
import com.mhealth.admin.repository.SystemTransactionRepository;
import com.mhealth.admin.repository.UsersRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Patient Transaction", description = "APIs for managing patient transaction")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/patient-transaction")
public class PatientTransactionController {

    @Autowired
    private SystemTransactionRepository systemTransactionRepository;

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private CityRepository cityRepository;

    @PostMapping("/get")
    @Operation(summary = "Get patient transaction", description = "Fetch patient transaction details with filters and pagination")
    public PaginationResponse<PatientTransactionDto> getPatientTransaction(
            @RequestBody PatientTransactionRequestDto request) {
        try {
            // Create Pageable object for pagination
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

            // Execute the query with pagination and filters
            Page<Object[]> resultPage = systemTransactionRepository.searchWithFilters(
                    request.getServiceType(),
                    request.getStatus(),
                    request.getOrderType(),
                    request.getChannel(),
                    request.getFromDate(),
                    request.getToDate(),
                    pageable
            );

            // Map results to DTO
            List<PatientTransactionDto> responseList = mapResultsToPatientTransactionReportResponseDto(resultPage.getContent());

            return new PaginationResponse<>(
                    Status.SUCCESS,
                    Constants.SUCCESS,
                    "",
                    responseList,
                    resultPage.getTotalElements(),
                    (long) request.getSize(),
                    (long) request.getPage()
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new PaginationResponse<>(e);
        }
    }

    private List<PatientTransactionDto> mapResultsToPatientTransactionReportResponseDto(List<Object[]> results) {
        return results.stream().map(row -> {
            Integer userId = (Integer) row[0];
            String contactNumber = Objects.toString(row[1], "") + Objects.toString(row[2], "");
            String channel = Objects.toString(row[3], "");
            String status = Objects.toString(row[4], "");
            String patientName = Objects.toString(row[5], "") + " " + Objects.toString(row[6], "");
            String address = Objects.toString(row[7], "") + " - " + Objects.toString(row[8], "");
            String refId = Objects.toString(row[9], "");
            String createdBy = Objects.toString(row[10], "");
            String orderType = Objects.toString(row[11], "");
            String dob = Objects.toString(row[12], "");
            String gender = Objects.toString(row[13], "");
            String drName = Objects.toString(row[14], "");
            String clinicName = Objects.toString(row[15], "");
            String transactionType = Objects.toString(row[16], "");

            return new PatientTransactionDto(
                    userId, contactNumber, channel, status, patientName, address, refId, createdBy, orderType, dob, gender,
                    drName, clinicName, transactionType
            );
        }).collect(Collectors.toList());
    }






}
