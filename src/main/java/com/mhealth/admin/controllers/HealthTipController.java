package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipRequest;
import com.mhealth.admin.dto.request.HealthTipSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTip;
import com.mhealth.admin.service.HealthTipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Health Tips Management", description = "APIs for managing health tips")
@RequestMapping("/api/v1/admin/health-tips")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class HealthTipController {

    @Autowired
    private HealthTipService healthTipService;

    @Operation(summary = "Create a new health tip", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<Response> createHealthTip(
            @Valid @ModelAttribute HealthTipRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return healthTipService.createHealthTip(request,locale);
    }

    @Operation(summary = "Update an existing health tip", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip updated successfully"),
            @ApiResponse(responseCode = "404", description = "Health tip not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateHealthTip(
            @PathVariable Integer id,
            @Valid @ModelAttribute HealthTipRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return healthTipService.updateHealthTip(id, request,locale);
    }

    @Operation(summary = "Update the status of a health tip", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Health tip not found")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Response> updateStatus(
            @PathVariable Integer id,
            @RequestParam StatusAI status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return healthTipService.updateStatus(id, status,locale);
    }

    @Operation(summary = "Search health tips by topic and status", responses = {
            @ApiResponse(responseCode = "200", description = "Search successful")
    })
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<HealthTip>> searchHealthTips(
            @Valid @RequestBody HealthTipSearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return healthTipService.searchHealthTips(request,locale);
    }

    @Operation(summary = "Delete a health tip by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Health tip not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteHealthTip(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return healthTipService.deleteHealthTip(id,locale);
    }
}
