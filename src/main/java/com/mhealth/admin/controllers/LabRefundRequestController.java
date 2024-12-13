package com.mhealth.admin.controllers;

import com.mhealth.admin.dto.LabRefundRequestResponseDTO;
import com.mhealth.admin.dto.request.SearchRefundRequest;
import com.mhealth.admin.service.LabRefundRequestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Lab Refund requests", description = "APIs for lab refund requests")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/lab-refund-requests")
public class LabRefundRequestController {

    @Autowired
    private LabRefundRequestService labRefundRequestService;

    @GetMapping("/search")
    public ResponseEntity<Page<LabRefundRequestResponseDTO>>
    searchRefundRequests(@Valid @RequestBody SearchRefundRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<LabRefundRequestResponseDTO> refundRequests = labRefundRequestService
                .searchRefundRequests(request.getPatientName(), request.getLabName(), pageable);
        return ResponseEntity.ok(refundRequests);
    }
}
