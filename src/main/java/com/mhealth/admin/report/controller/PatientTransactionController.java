package com.mhealth.admin.report.controller;


import com.mhealth.admin.constants.Constants;
import com.mhealth.admin.dto.Status;
import com.mhealth.admin.dto.enums.Channel;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.*;
import com.mhealth.admin.report.controller.dto.PatientTransactionDto;
import com.mhealth.admin.report.controller.dto.PatientTransactionRequestDto;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@Tag(name = "Patient Transaction", description = "APIs for managing patient transaction")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/patient-transaction")
public class PatientTransactionController {

    @Autowired
    private SystemTransactionRepository systemTransactionRepository;

    @Autowired
    private UsersRepository usersRepository;

    @PostMapping("/get")
    @Operation(summary = "Get patient transaction", description = "Fetch patient transaction details with filters and pagination")
    public PaginationResponse<PatientTransactionDto> getPatientTransaction(
            @RequestBody PatientTransactionRequestDto request) {
        try {
            // Create Pageable objects for both queries to apply pagination
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

            // Fetch system transactions with pagination
            Page<SystemTransaction> systemTransactionPage = systemTransactionRepository.fetchPatientTransaction(
                    request.getServiceType(),
                    request.getStatus(),
                    request.getOrderType(),
                    request.getChannel(),
                    request.getFromDate(),
                    request.getToDate(),
                    pageable
            );

            // Fetch nod transactions with pagination
            Page<NodLog> nodLogPage = systemTransactionRepository.fetchNodTransaction(
                    request.getServiceType(),
                    request.getStatus(),
                    request.getOrderType(),
                    request.getChannel(),
                    request.getFromDate(),
                    request.getToDate(),
                    pageable
            );

            // Combine and map transactions to DTOs
            List<PatientTransactionDto> patientTransactionDTOs = new ArrayList<>();

            // Map SystemTransaction to DTO
            systemTransactionPage.getContent().forEach(transaction -> {
                PatientTransactionDto dto = mapSystemTransactionToDto(transaction);
                patientTransactionDTOs.add(dto);
            });

            // Map NodLog to DTO
            nodLogPage.getContent().forEach(nodLog -> {
                PatientTransactionDto dto = mapNodLogToDto(nodLog);
                patientTransactionDTOs.add(dto);
            });

            // Sort the DTOs by createdAt (descending)
            patientTransactionDTOs.sort(Comparator.comparing(PatientTransactionDto::getCreatedAt).reversed());

            // Calculate total count from both pages
            long totalElements = systemTransactionPage.getTotalElements() + nodLogPage.getTotalElements();

            // Apply pagination manually if needed
            int start = Math.min(request.getPage() * request.getSize(), patientTransactionDTOs.size());
            int end = Math.min((request.getPage() + 1) * request.getSize(), patientTransactionDTOs.size());
            List<PatientTransactionDto> paginatedList = patientTransactionDTOs.subList(start, end);

            // Return the paginated response
            return new PaginationResponse<>(
                    Status.SUCCESS,
                    Constants.SUCCESS,
                    "",
                    paginatedList,
                    totalElements,
                    (long) request.getSize(),
                    (long) request.getPage()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new PaginationResponse<>(e);
        }
    }



    private PatientTransactionDto mapSystemTransactionToDto(SystemTransaction transaction) {
        PatientTransactionDto dto = new PatientTransactionDto();
        if (transaction.getRefId() != null) {
            Consultation consultation = transaction.getRefId();
            dto.setRefId(consultation.getCaseId() != null ? consultation.getCaseId().toString() : null);
            if (consultation.getDoctorId() != null) {
                dto.setDoctorOrNurse(consultation.getDoctorId().getFirstName() + " " +
                        consultation.getDoctorId().getLastName());
                Integer hospitalId = consultation.getDoctorId().getHospitalId();
                if (hospitalId != null) {
                    Users hospital = usersRepository.findById(hospitalId).orElse(null);
                    if (hospital != null) {
                        dto.setClinicName(hospital.getClinicName());
                    }
                }
            }
            if (consultation.getPatientId() != null) {
                dto.setMobile(consultation.getPatientId().getContactNumber());
                dto.setPatientName(consultation.getPatientId().getFirstName() + " " +
                        consultation.getPatientId().getLastName());
                dto.setAge(consultation.getPatientId().getDob());
                dto.setGender(consultation.getPatientId().getGender());
                dto.setPatientAddress(consultation.getPatientId().getResidenceAddress());
            }
        }
        dto.setServiceType(transaction.getTransactionType());
        dto.setOrderType(transaction.getOrderType());
        dto.setStatus(transaction.getStatus());
        dto.setChannel(transaction.getChannel());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
    private PatientTransactionDto mapNodLogToDto(NodLog nodLog) {
        PatientTransactionDto dto = new PatientTransactionDto();
        if (nodLog.getSearchId() != null) {
            dto.setRefId(nodLog.getSearchId());
            Users user = usersRepository.findById(nodLog.getUserId()).orElse(null);
            if (user != null) {
                dto.setMobile(user.getContactNumber());
                dto.setPatientName(user.getFirstName() + " " + user.getLastName());
                dto.setAge(user.getDob());
                dto.setGender(user.getGender());
                dto.setPatientAddress(user.getResidenceAddress());
            }
        }
        dto.setServiceType(nodLog.getTransactionType());
        dto.setOrderType(nodLog.getOrderType());
        dto.setStatus(nodLog.getStatus());
        dto.setChannel(nodLog.getChannel());
        dto.setCreatedAt(nodLog.getCreatedAt());
        return dto;
    }



}
