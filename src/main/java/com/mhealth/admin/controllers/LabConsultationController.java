package com.mhealth.admin.controllers;

import com.mhealth.admin.dto.dto.LabConsultationResponseDTO;
import com.mhealth.admin.service.LabConsultationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@Tag(name = "Lab Consultation Management", description = "APIs for managing lab consultation")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/lab-consultations")
public class LabConsultationController {
    @Autowired
    private LabConsultationService labConsultationService;

    @GetMapping("/search")
    public ResponseEntity<Page<LabConsultationResponseDTO>> searchLabConsultations(
            @RequestParam(required = false) String patientName,
            @RequestParam(required = false) String doctorName,
            @RequestParam(required = false) Integer caseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate consultationDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<LabConsultationResponseDTO> consultations = labConsultationService.searchLabConsultations(
                patientName, doctorName, caseId, consultationDate, pageable);
        return ResponseEntity.ok(consultations);
    }
}