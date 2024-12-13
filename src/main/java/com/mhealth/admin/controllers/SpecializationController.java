package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.SpecializationRequest;
import com.mhealth.admin.dto.request.SpecializationSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Specialization;
import com.mhealth.admin.service.SpecializationService;
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
@Tag(name = "Specialization Management", description = "APIs for managing specializations")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/specializations")
public class SpecializationController {

    @Autowired
    private SpecializationService service;

    @Operation(summary = "Add a new specialization", responses = {
            @ApiResponse(responseCode = "200", description = "Specialization added successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "Specialization already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> addSpecialization(
            @Valid @RequestBody SpecializationRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.addSpecialization(request, locale);
    }

    @Operation(summary = "Update an existing specialization", responses = {
            @ApiResponse(responseCode = "200", description = "Specialization updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Specialization not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateSpecialization(
            @PathVariable Integer id,
            @Valid @RequestBody SpecializationRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateSpecialization(id, request, locale);
    }

    @Operation(summary = "Change the status of a specialization by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Specialization not found", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> changeStatus(
            @PathVariable Integer id,
            @RequestParam StatusAI status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.changeStatus(id, status, locale);
    }

    @Operation(summary = "Delete a specialization by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Specialization deleted successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Specialization not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteSpecialization(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteSpecialization(id, locale);
    }

    @Operation(summary = "Search specializations by name and status with pagination", responses = {
            @ApiResponse(responseCode = "200", description = "Specializations fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<Specialization>> searchSpecializations(
            @Valid @RequestBody SpecializationSearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchSpecializations(request, locale);
    }

    @Operation(summary = "Find a specialization by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Specialization fetched successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Specialization not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findSpecializationById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.findSpecializationById(id, locale);
    }
}