package com.mhealth.admin.controllers;


import com.mhealth.admin.dto.request.NurseDemandOrdersSearchRequest;
import com.mhealth.admin.dto.response.PaginationResponse;
import com.mhealth.admin.model.NurseDemandOrders;
import com.mhealth.admin.service.NurseDemandOrdersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "NOD Report orders", description = "APIs for managing NOD orders")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*")
@RequestMapping("/api/v1/admin/nurse-demand-orders")
public class NurseDemandOrdersController {

    @Autowired
    private NurseDemandOrdersService service;

    @Operation(
            summary = "Search Nurse Demand Orders with optional filters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Search results fetched successfully",
                            content = @Content(schema = @Schema(implementation = PaginationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
            }
    )
    @PostMapping("/search")
    public ResponseEntity<PaginationResponse<NurseDemandOrders>> searchOrders(
            @RequestBody NurseDemandOrdersSearchRequest request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return ResponseEntity.ok(service.searchOrders(
                request.getPatientName(),
                request.getNurseName(),
                request.getConsultationDate(),
                pageable));
    }
}