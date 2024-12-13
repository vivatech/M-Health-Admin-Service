package com.mhealth.admin.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request object for searching nurse demand orders")
public class NurseDemandOrdersSearchRequest {

    @Schema(description = "Patient's full name", example = "John Doe")
    private String patientName;

    @Schema(description = "Nurse's full name", example = "Jane Doe")
    private String nurseName;

    @Schema(description = "Consultation date", example = "2024-12-01")
    private LocalDate consultationDate;

    @Schema(description = "Page number for pagination", example = "0")
    private int page = 0;

    @Schema(description = "Page size for pagination", example = "10")
    private int size = 10;
}
