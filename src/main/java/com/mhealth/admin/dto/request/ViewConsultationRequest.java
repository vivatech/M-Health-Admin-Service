package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewConsultationRequest {
    private LocalDate consultationDate;
    private Integer caseId;
    private String patientName;
    private String phoneNumber;
    @NotNull(message = "Page no is required")
    @Min(value = 0, message = "Page number must be 0 or greater")
    private Integer page;
    private Integer size;

}
