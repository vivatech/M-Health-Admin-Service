package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.CategoryStatus;
import com.mhealth.admin.dto.request.LabSubCategoryRequest;
import com.mhealth.admin.dto.request.LabSubCategorySearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.LabSubCategoryMaster;
import com.mhealth.admin.service.LabSubCategoryService;
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
@Tag(name = "Lab Sub Category Management", description = "APIs for managing lab subcategories")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/lab-sub-categories")
public class LabSubCategoryController {

    @Autowired
    private LabSubCategoryService service;

    @Operation(summary = "Add a new lab subcategory", responses = {
            @ApiResponse(responseCode = "200", description = "Lab subcategory added successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "Lab subcategory already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> addLabSubCategory(
            @Valid @RequestBody LabSubCategoryRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.addLabSubCategory(request, locale);
    }

    @Operation(summary = "Update an existing lab subcategory", responses = {
            @ApiResponse(responseCode = "200", description = "Lab subcategory updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Lab subcategory not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateLabSubCategory(
            @PathVariable Integer id,
            @Valid @RequestBody LabSubCategoryRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateLabSubCategory(id, request, locale);
    }

    @Operation(summary = "Change the status of a lab subcategory by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Lab subcategory not found", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> changeStatus(
            @PathVariable Integer id,
            @RequestParam CategoryStatus status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.changeStatus(id, status, locale);
    }

    @Operation(summary = "Delete a lab subcategory by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Lab subcategory deleted successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Lab subcategory not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteLabSubCategory(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteLabSubCategory(id, locale);
    }

    @Operation(summary = "Search lab subcategories by name, category ID, or status with pagination", responses = {
            @ApiResponse(responseCode = "200", description = "Lab subcategories fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<LabSubCategoryMaster>> searchLabSubCategories(
            @Valid @RequestBody LabSubCategorySearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchLabSubCategories(request, locale);
    }

    @Operation(summary = "Find a lab subcategory by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Lab subcategory fetched successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Lab subcategory not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findLabSubCategoryById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.findLabSubCategoryById(id, locale);
    }
}