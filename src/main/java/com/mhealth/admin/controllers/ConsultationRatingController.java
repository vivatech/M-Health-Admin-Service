package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.enums.ConsultationStatus;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.ConsultationRating;
import com.mhealth.admin.service.ConsultationRatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;

@RestController
@Tag(name = "Consultation Rating Management", description = "APIs for managing consultation ratings")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/consultation-ratings")
public class ConsultationRatingController {

    @Autowired
    private ConsultationRatingService service;

    @Operation(summary = "Paginated consultation ratings", responses = {
            @ApiResponse(responseCode = "200", description = "Ratings fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PaginationResponse<ConsultationRating>> getConsultationRatings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.getPaginatedRatings(page, size, locale);
    }

    @Operation(summary = "Change status of a consultation rating", responses = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Rating not found", content = @Content)
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<Response> changeStatus(
            @PathVariable Integer id,
            @RequestParam ConsultationStatus status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.changeRatingStatus(id, status, locale);
    }
}
