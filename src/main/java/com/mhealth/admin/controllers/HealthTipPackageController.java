package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipPackageRequest;
import com.mhealth.admin.dto.request.HealthTipPackageSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTipPackage;
import com.mhealth.admin.model.HealthTipPackageCategories;
import com.mhealth.admin.service.HealthTipPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Health Tip Package Management", description = "APIs for managing health tip packages")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/health-tip-packages")
public class HealthTipPackageController {

    @Autowired
    private HealthTipPackageService service;

    @Operation(summary = "Add a new health tip package", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip package added successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "Health tip package already exists", content = @Content),
            @ApiResponse(responseCode = "404", description = "Health tip duration not found", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> addHealthTipPackage(
            @Valid @RequestBody HealthTipPackageRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.createHealthTipPackage(request, locale);
    }

    @Operation(summary = "Update an existing health tip package", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip package updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Health tip package not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateHealthTipPackage(
            @PathVariable Integer id,
            @RequestBody HealthTipPackageRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateHealthTipPackage(id, request, locale);
    }

    @Operation(summary = "Change the status of a health tip package by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Health tip package not found", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> changeStatus(
            @PathVariable Integer id,
            @RequestParam StatusAI status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateStatus(id, status, locale);
    }

    @Operation(summary = "Delete a health tip package by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip package deleted successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Health tip package not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteHealthTipPackage(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteHealthTipPackage(id, locale);
    }
    @Operation(summary = "Search health tip packages by name, duration, or status with pagination", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip packages fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<HealthTipPackageCategories>> searchHealthTipPackages(
            @Valid @RequestBody HealthTipPackageSearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchHealthTipPackages(request, locale);
    }


    @Operation(
            summary = "Search health tip packages by name, duration, or status with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Health tip packages fetched successfully",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
            }
    )
    @PostMapping("/search-report")
    public ResponseEntity<PaginationResponse<HealthTipPackage>> searchHealthTipPackages(
            @RequestBody HealthTipPackageSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize() != null ? request.getSize() : Constants.DEFAULT_PAGE_SIZE);
        return service.searchPackages(request.getPackageName(), request.getStartDate(), request.getEndDate(), pageable);
    }

    @Operation(summary = "Find a health tip package by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip package fetched successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Health tip package not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findHealthTipPackageById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.findHealthTipPackageById(id, locale);
    }
}
