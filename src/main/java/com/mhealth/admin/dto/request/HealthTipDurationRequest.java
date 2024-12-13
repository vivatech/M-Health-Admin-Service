package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.DurationType;
import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthTipDurationRequest {

    @NotBlank(message = "Duration name is required")
    @Size(max = 255, message = "Duration name cannot exceed 255 characters")
    private String durationName;

    @NotNull(message = "Duration type is required")
    private DurationType durationType;

    @NotNull(message = "Duration value is required")
    @Min(value = 1, message = "Duration value must be at least 1")
    private Integer durationValue;

    @NotNull(message = "Status is required")
    private StatusAI status;
}
