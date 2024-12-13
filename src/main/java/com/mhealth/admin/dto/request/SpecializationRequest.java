package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecializationRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Name (local) is required")
    private String nameSl;

    @NotBlank(message = "Photo is required")
    private String photo;

    @NotBlank(message = "Description is required")
    private String description;

    private String descriptionSl;

    @NotNull(message = "Status is required")
    private StatusAI status;

    @NotNull(message = "Is Featured field is required")
    private Integer isFeatured;
}
