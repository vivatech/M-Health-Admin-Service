package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipDurationRequest;
import com.mhealth.admin.dto.request.HealthTipDurationSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTipDuration;
import com.mhealth.admin.service.HealthTipDurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@RequestMapping("/api/v1/admin/healthtip-durations")
@Tag(name = "Health Tip Duration Management", description = "APIs for managing Health Tip Durations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class HealthTipDurationController {

    @Autowired
    private HealthTipDurationService service;

    @PostMapping
    @Operation(summary = "Create a new Health Tip Duration", responses = {
            @ApiResponse(responseCode = "200", description = "Duration created successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    public ResponseEntity<Response> createDuration(
            @Valid @RequestBody HealthTipDurationRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.createDuration(request,locale);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Health Tip Duration", responses = {
            @ApiResponse(responseCode = "200", description = "Duration updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Duration not found", content = @Content)
    })
    public ResponseEntity<Response> updateDuration(
            @PathVariable Integer id,
            @Valid @RequestBody HealthTipDurationRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateDuration(id, request,locale);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Change the status of a Health Tip Duration", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Duration not found", content = @Content)
    })
    public ResponseEntity<Response> changeStatus(@PathVariable Integer id, @RequestParam StatusAI status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.changeStatus(id, status,locale);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Health Tip Duration by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Duration deleted successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Duration not found", content = @Content)
    })
    public ResponseEntity<Response> deleteDuration(@PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteDuration(id,locale);
    }

    @PostMapping("/search")
    @Operation(summary = "Search Health Tip Durations by status and name", responses = {
            @ApiResponse(responseCode = "200", description = "Durations fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    public ResponseEntity<PaginationResponse<HealthTipDuration>> searchDurations(
            @Valid @RequestBody HealthTipDurationSearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchDurations(request,locale);
    }
}