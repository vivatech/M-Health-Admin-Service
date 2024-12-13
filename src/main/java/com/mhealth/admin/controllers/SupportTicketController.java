package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.SupportTicket;
import com.mhealth.admin.service.SupportTicketService;
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
@Tag(name = "Support Ticket Management", description = "APIs for managing support tickets")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/support-tickets")
public class SupportTicketController {

    @Autowired
    private SupportTicketService service;

    @Operation(summary = "Search and paginate support tickets by title and status", responses = {
            @ApiResponse(responseCode = "200", description = "Tickets fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @GetMapping
    public ResponseEntity<PaginationResponse<SupportTicket>> searchSupportTickets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String status,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.searchSupportTickets(page, size, title, status, locale);
    }

    @Operation(summary = "Find a support ticket by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Ticket found successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Ticket not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findSupportTicketById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale) {
        return service.findSupportTicketById(id, locale);
    }


}
