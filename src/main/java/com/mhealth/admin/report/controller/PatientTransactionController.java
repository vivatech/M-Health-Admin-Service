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
            // Set pagination and sorting
            Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), Sort.by(Sort.Direction.DESC, "created_at"));

            // Fetch system transactions
            Page<SystemTransaction> systemTransactionPage = systemTransactionRepository.fetchPatientTransaction(
                    request.getServiceType(),
                    request.getStatus(),
                    request.getOrderType(),
                    request.getChannel(),
                    request.getFromDate(),
                    request.getToDate(),
                    pageable
            );

            // Fetch nod transactions
            List<Object[]> nodLogPage = systemTransactionRepository.fetchNodTransaction(
                    request.getServiceType(),
                    request.getStatus(),
                    request.getOrderType(),
                    request.getChannel(),
                    request.getFromDate(),
                    request.getToDate(),
                    pageable.getPageSize(),
                    pageable.getPageNumber() * pageable.getPageSize()
            );

            // Fetch the total count of records (for pagination)
            long totalNodElements = systemTransactionRepository.countNodTransaction(
                    request.getServiceType(),
                    request.getStatus(),
                    request.getOrderType(),
                    request.getChannel(),
                    request.getFromDate(),
                    request.getToDate()
            );

            // Combine and map transactions to DTOs
            List<PatientTransactionDto> patientTransactionDTOs = new ArrayList<>();

            // Map SystemTransaction to DTO
            systemTransactionPage.getContent().forEach(transaction -> {
                PatientTransactionDto dto = mapSystemTransactionToDto(transaction);
                patientTransactionDTOs.add(dto);
            });

            // Map NodLog to DTO
            for (Object[] row : nodLogPage) {
                PatientTransactionDto dto = mapNodLogToDto(row);
                patientTransactionDTOs.add(dto);
            }

            // Sort DTOs by createdAt (already descending from the query, redundant but safe)
            patientTransactionDTOs.sort(Comparator.comparing(PatientTransactionDto::getCreatedAt).reversed());

            // Return the paginated response
            return new PaginationResponse<>(
                    Status.SUCCESS,
                    Constants.SUCCESS,
                    "",
                    patientTransactionDTOs,
                    systemTransactionPage.getTotalElements() + totalNodElements,
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

    private PatientTransactionDto mapNodLogToDto(Object[] row) {
        PatientTransactionDto dto = new PatientTransactionDto();

        if (row[2] != null) {
            dto.setRefId((String) row[2]);
            Users user = usersRepository.findById((Integer) row[1]).orElse(null);
            if (user != null) {
                dto.setMobile(user.getContactNumber());
                dto.setPatientName(user.getFirstName() + " " + user.getLastName());
                dto.setAge(user.getDob());
                dto.setGender(user.getGender());
                dto.setPatientAddress(user.getResidenceAddress());
            }
        }


        dto.setServiceType((String) row[8]);
        dto.setOrderType(Character.toString((Character) row[7]));
        dto.setStatus((String) row[9]);
        dto.setChannel(Channel.valueOf((String) row[6]));
        dto.setCreatedAt(((Timestamp) row[10]).toLocalDateTime());

        return dto;
    }



}
