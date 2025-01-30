package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.HealthTipCategoryRequest;
import com.mhealth.admin.dto.request.HealthTipCategorySearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.HealthTipCategoryMaster;
import com.mhealth.admin.service.HealthTipCategoryMasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;


@RestController
@Tag(name = "Health Tip Category Management", description = "APIs for managing health tip categories")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/health-tip-categories")
public class HealthTipCategoryMasterController {

    @Autowired
    private HealthTipCategoryMasterService service;

    @Operation(summary = "Add a new health tip category", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip category added successfully"),
            @ApiResponse(responseCode = "409", description = "Category already exists")
    })
    @PostMapping
    public ResponseEntity<Response> addCategory(
            @Valid @ModelAttribute HealthTipCategoryRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) throws Exception {
        return service.addCategory(request, locale);
    }

    @Operation(summary = "Update an existing health tip category", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateCategory(
            @PathVariable Integer id,
            @Valid @ModelAttribute HealthTipCategoryRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) throws Exception {
        return service.updateCategory(id, request, locale);
    }

    @Operation(summary = "Change the status of a health tip category", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> changeStatus(
            @PathVariable Integer id,
            @RequestParam StatusAI status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.changeStatus(id, status, locale);
    }

    @Operation(summary = "Update an existing health tip category", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteCategory(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteCategory(id, locale);
    }

    @Operation(summary = "Search health tip categories by name and status with pagination")
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<HealthTipCategoryMaster>> searchCategories(
            @Valid @RequestBody HealthTipCategorySearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchCategories(request, locale);
    }

    @Operation(summary = "Getting category master by id", responses = {
            @ApiResponse(responseCode = "200", description = "Health tip category found success"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> getCategoryById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.getCategoryById(id, locale);
    }

}
