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
public class HealthTipCategorySearchRequest {

    private String name;

    private String status;

    @Min(value = 0, message = "Page number must be 0 or greater")
    private int page;

    //@Min(value = 1, message = "Page size must be 1 or greater")
    private Integer size;
    private String sortBy;
    private String sortDirection;
}
