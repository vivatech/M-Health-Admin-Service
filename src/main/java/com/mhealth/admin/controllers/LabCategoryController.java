package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.request.LabCategoryRequest;
import com.mhealth.admin.dto.request.LabCategorySearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.LabCategoryMaster;
import com.mhealth.admin.service.LabCategoryService;
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
@Tag(name = "Lab Category Management", description = "APIs for managing lab categories")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/lab-categories")
public class LabCategoryController {

    @Autowired
    private LabCategoryService service;

    @Operation(summary = "Add a new lab category", responses = {
            @ApiResponse(responseCode = "200", description = "Lab category added successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "Lab category already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> addLabCategory(
            @Valid @RequestBody LabCategoryRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.addLabCategory(request, locale);
    }

    @Operation(summary = "Update an existing lab category", responses = {
            @ApiResponse(responseCode = "200", description = "Lab category updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Lab category not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateLabCategory(
            @PathVariable Integer id,
            @Valid @RequestBody LabCategoryRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateLabCategory(id, request, locale);
    }

    @Operation(summary = "Change the status of a lab category by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Lab category not found", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> changeStatus(
            @PathVariable Integer id,
            @RequestParam CategoryStatus status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.changeStatus(id, status, locale);
    }

    @Operation(summary = "Delete a lab category by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Lab category deleted successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Lab category not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteLabCategory(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteLabCategory(id, locale);
    }

    @Operation(summary = "Search lab categories by name or status with pagination", responses = {
            @ApiResponse(responseCode = "200", description = "Lab categories fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<LabCategoryMaster>> searchLabCategories(
            @Valid @RequestBody LabCategorySearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchLabCategories(request, locale);
    }

    @Operation(summary = "Find a lab category by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Lab category fetched successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Lab category not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findLabCategoryById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.findLabCategoryById(id, locale);
    }
}