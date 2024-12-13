package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabCategorySearchRequest {
    @NotNull(message = "Page is required")
    private Integer page;

    @NotNull(message = "Size is required")
    private Integer size;

    private String catName;

    private StatusAI catStatus;
}
