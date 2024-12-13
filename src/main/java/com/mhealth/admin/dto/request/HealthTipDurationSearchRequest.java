package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthTipDurationSearchRequest {

    @Size(max = 255, message = "Duration name cannot exceed 255 characters")
    private String durationName;

    @NotNull(message = "Status is required")
    private StatusAI status;

    @NotNull(message = "Page number is required")
    @Min(value = 0, message = "Page number must be 0 or greater")
    private Integer page;

    @NotNull(message = "Page size is required")
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size;
}

