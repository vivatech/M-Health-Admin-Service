package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.StatusAI;
import com.mhealth.admin.dto.request.ZoneRequest;
import com.mhealth.admin.dto.request.ZoneSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Zone;
import com.mhealth.admin.service.ZoneService;
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
@Tag(name = "Zone Management", description = "APIs for managing zones")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/zones")
public class ZoneController {

    @Autowired
    private ZoneService service;

    @Operation(summary = "Add a new zone", responses = {
            @ApiResponse(responseCode = "200", description = "Zone added successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "409", description = "Zone already exists", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> addZone(
            @Valid @RequestBody ZoneRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.addZone(request, locale);
    }

    @Operation(summary = "Update an existing zone", responses = {
            @ApiResponse(responseCode = "200", description = "Zone updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Zone not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateZone(
            @PathVariable Integer id,
            @Valid @RequestBody ZoneRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.updateZone(id, request, locale);
    }

    @Operation(summary = "Change the status of a zone by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Zone not found", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> changeStatus(
            @PathVariable Integer id,
            @RequestParam StatusAI status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.changeStatus(id, status, locale);
    }

    @Operation(summary = "Delete a zone by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Zone deleted successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Zone not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteZone(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.deleteZone(id, locale);
    }

    @Operation(summary = "Search zones by name and status with pagination", responses = {
            @ApiResponse(responseCode = "200", description = "Zones fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<Zone>> searchZones(
            @Valid @RequestBody ZoneSearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchZones(request, locale);
    }

    @Operation(summary = "Find a zone by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Zone fetched successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Zone not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findZoneById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.findZoneById(id, locale);
    }
}
