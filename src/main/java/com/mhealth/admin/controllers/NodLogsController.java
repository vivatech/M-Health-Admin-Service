package com.mhealth.admin.controllers;

import com.mhealth.admin.dto.request.NodLogSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.NodLog;
import com.mhealth.admin.service.NodLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "NOD Log Management", description = "APIs for managing NOD Logs")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/nod-logs")
public class NodLogsController {

    @Autowired
    private NodLogService service;

    @PostMapping("/fetch-all")
    @Operation(summary = "Fetch all NOD logs with pagination and optional filters")
    public ResponseEntity<PaginationResponse<NodLog>> fetchAll(
            @Valid @RequestBody NodLogSearchRequest request) {
        return ResponseEntity.ok(service.fetchAll(request));
    }
}
