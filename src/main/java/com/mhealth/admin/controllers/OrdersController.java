package com.mhealth.admin.controllers;

import com.mhealth.admin.config.Constants;
import com.mhealth.admin.dto.request.OrdersSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.dto.response.Response;
import com.mhealth.admin.model.Orders;
import com.mhealth.admin.service.OrdersService;
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
@Tag(name = "Orders Management", description = "APIs for managing orders")
@RequestMapping("/api/v1/admin/orders")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
public class OrdersController {

    @Autowired
    private OrdersService service;

    @Operation(summary = "Search orders by patient, doctor, or consultation date", responses = {
            @ApiResponse(responseCode = "200", description = "Orders fetched successfully", content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<Orders>> searchOrders(
            @Valid @RequestBody OrdersSearchRequest request,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale
    ) {
        return service.searchOrders(request,locale);
    }

    @Operation(summary = "Find an order by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Order fetched successfully", content = @Content(schema = @Schema(implementation = Response.class))),
            @ApiResponse(responseCode = "404", description = "Order not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Response> findOrderById(
            @PathVariable Integer id,
            @RequestHeader(name = "X-localization", required = false, defaultValue = Constants.DEFAULT_LOCALE) Locale locale
    ) {
        return service.findById(id,locale);
    }
}
