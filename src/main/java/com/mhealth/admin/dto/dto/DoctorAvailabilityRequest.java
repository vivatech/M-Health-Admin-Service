package com.mhealth.admin.dto.dto;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorAvailabilityRequest {
    @NotNull(message = "Patient Id is required!")
    private Integer patientId;

    @NotNull(message = "Doctor Id is required!")
    private Integer doctorId;

    private String consultType;

    @NotNull(message = "Page no is required!")
    @Min(value = 0, message = "Page no must be greater than or equal to 0")
    private Integer pageNo;

    @NotNull(message = "Page size is required!")
    @Min(value = 1, message = "Page size must be greater than or equal to 1")
    private Integer pageSize;

}
