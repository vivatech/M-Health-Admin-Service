package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RescheduleRequest {

    @NotNull(message = "Case ID is required")
    @Min(value = 1, message = "Case ID must be greater than 0")
    private Integer caseId;
    @NotNull(message = "Consultation Date is required")
    private LocalDate consultationDate;
    @NotNull(message = "Slot ID is required")
    @Min(value = 1, message = "Slot ID must be greater than 0")
    private Integer slotId;
    @NotNull(message = "Messages is required")
    private String messages;
}
