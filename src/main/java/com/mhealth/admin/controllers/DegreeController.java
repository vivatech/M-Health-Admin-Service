package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.DegreeRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Degree;
import com.mhealth.admin.service.DegreeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@RestController
@Tag(name = "Degree Management", description = "APIs for managing degrees")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/degrees")
public class DegreeController {

    @Autowired
    private DegreeService service;

    @Operation(summary = "Add a new degree", responses = {
            @ApiResponse(responseCode = "200", description = "Degree added successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "409", description = "Degree already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> addDegree(
            @Valid @RequestBody DegreeRequest degreeRequest,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        Degree degree = new Degree();
        degree.setName(degreeRequest.getName());
        degree.setDescription(degreeRequest.getDescription());
        degree.setStatus(degreeRequest.getStatus());
        degree.setCreatedAt(LocalDateTime.now());

        return service.addDegree(degree, locale);
    }

    @Operation(summary = "Update an existing degree", responses = {
            @ApiResponse(responseCode = "200", description = "Degree updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
            @ApiResponse(responseCode = "404", description = "Degree not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateDegree(
            @PathVariable Integer id,
            @RequestBody Degree degreeRequest,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {

        return service.updateDegree(id, degreeRequest, locale);
    }

    @Operation(summary = "Change the status of a degree", responses = {
            @ApiResponse(responseCode = "200", description = "Degree status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Degree not found", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> changeStatus(
            @PathVariable Integer id,
            @RequestParam StatusAI status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.changeStatus(id, status, locale);
    }

    @Operation(summary = "Delete a degree by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Degree deleted successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Degree not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteDegree(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteDegree(id, locale);
    }

    @Operation(summary = "Get all degrees", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched all degrees", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Degree>> getAllDegrees() {
        return service.getAllDegrees();
    }

    @Operation(summary = "Search degrees by name and status", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched degrees", content = @Content(schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<Degree>> searchDegrees(
            @RequestParam String name,
            @RequestParam StatusAI status) {
        return service.searchDegrees(name, status);
    }
}