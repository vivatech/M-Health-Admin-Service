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
    @NotNull(message = "Name is required")
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Name (local) is required")
    @NotBlank(message = "Name (local) is required")
    private String nameSl;

    @NotNull(message = "Photo is required")
    @NotBlank(message = "Photo is required")
    private String photo;

    @NotNull(message = "Description is required")
    @NotBlank(message = "Description is required")
    private String description;

    private String descriptionSl;

    @NotNull(message = "Status is required")
    private StatusAI status;

    @NotNull(message = "Is Featured field is required")
    private Integer isFeatured;
}
