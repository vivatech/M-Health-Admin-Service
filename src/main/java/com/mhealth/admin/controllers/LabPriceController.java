package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.dto.LabPriceDto;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.dto.request.LabPriceRequest;
import com.mhealth.admin.model.LabPrice;
import com.mhealth.admin.service.LabPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Lab Price Management", description = "APIs for managing lab prices")
@RequestMapping("/api/v1/lab-prices")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class LabPriceController {

    @Autowired
    private LabPriceService labPriceService;

    @Operation(summary = "Create a new Lab Price", responses = {
            @ApiResponse(responseCode = "200", description = "Lab price created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping
    public ResponseEntity<Response> createLabPrice(
            @Valid @RequestBody LabPriceRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return labPriceService.createLabPrice(request, locale);
    }

    @Operation(summary = "Update an existing Lab Price", responses = {
            @ApiResponse(responseCode = "200", description = "Lab price updated successfully"),
            @ApiResponse(responseCode = "404", description = "Lab price not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateLabPrice(
            @PathVariable Integer id,
            @Valid @RequestBody LabPriceRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return labPriceService.updateLabPrice(id, request, locale);
    }

    @Operation(summary = "Find Lab Price by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Lab price retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Lab price not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findLabPriceById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return labPriceService.findLabPriceById(id, locale);
    }

    @Operation(summary = "Search Lab Prices", responses = {
            @ApiResponse(responseCode = "200", description = "Lab prices retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "No lab prices found")
    })
    @GetMapping("/search")
    public ResponseEntity<PaginationResponse<LabPriceDto>> searchLabPrices(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer subCategoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return labPriceService.searchLabPrices(categoryId, subCategoryId, page, size, locale);
    }

    @Operation(summary = "Delete a Lab Price by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Lab price deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Lab price not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteLabPrice(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return labPriceService.deleteLabPrice(id, locale);
    }
}
