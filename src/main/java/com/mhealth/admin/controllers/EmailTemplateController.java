package com.mhealth.admin.controllers;

import com.mhealth.admin.dto.request.EmailTemplateRequest;
import com.mhealth.admin.dto.request.EmailTemplateSearchRequest;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.service.EmailTemplateService;
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
@Tag(name = "EmailTemplate Management", description = "APIs for managing email templates")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/email-templates")
public class EmailTemplateController {

    @Autowired
    private EmailTemplateService service;

    @Operation(summary = "Create a new email template", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully created", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "400", description = "Validation error", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Response> createEmailTemplate(
            @Valid @RequestBody EmailTemplateRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.createEmailTemplate(request, locale);
    }

    @Operation(summary = "Update an existing email template", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully updated", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Email template not found", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<Response> updateEmailTemplate(
            @PathVariable Integer id,
            @RequestBody EmailTemplateRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.updateEmailTemplate(id, request, locale);
    }

    @Operation(summary = "Search email templates by key or value", responses = {
            @ApiResponse(responseCode = "200", description = "Search successful", content = @Content(schema = @Schema(implementation = Response.class)))
    })
    @PostMapping("/search")
    public ResponseEntity<Response> searchEmailTemplates(
            @RequestBody EmailTemplateSearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.searchEmailTemplates(request.getKey(), request.getSubject(), locale);
    }

    @Operation(summary = "Get email template by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Email template not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> getEmailTemplateById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.getEmailTemplateById(id, locale);
    }

    @Operation(summary = "Delete email template by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted", content = @Content),
            @ApiResponse(responseCode = "404", description = "Email template not found", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteEmailTemplateById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = "so") Locale locale) {
        return service.deleteEmailTemplateById(id, locale);
    }
}
