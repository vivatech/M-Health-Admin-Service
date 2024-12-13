package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthTipSearchRequest {

    private String topic;

    private StatusAI status;

    @NotNull(message = "Page number is required")
    @Min(value = 0, message = "Page number must be 0 or greater")
    private Integer page;

    @NotNull(message = "Page size is required")
    @Min(value = 1, message = "Page size must be 1 or greater")
    private Integer size;
}