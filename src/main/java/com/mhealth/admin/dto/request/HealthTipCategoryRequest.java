package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.StatusAI;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class HealthTipCategoryRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Name in second language is required")
    private String nameSl;

    @NotBlank(message = "Description is required")
    private String description;

    private String descriptionSl;

    @NotNull(message = "Status is required")
    private StatusAI status;

    private MultipartFile photo;

    @NotBlank(message = "Is Featured is required")
    @Pattern(regexp = "0|1", message = "Is Featured must be 0 or 1")
    private String isFeatured;

    private Integer priority;

    public HealthTipCategoryRequest(String name, String nameSl, String description, String descriptionSl,
                                    StatusAI status, MultipartFile photo, String isFeatured, Integer priority) {
        this.name = name;
        this.nameSl = nameSl;
        this.description = description;
        this.descriptionSl = descriptionSl;
        this.status = status;
        this.photo = photo;
        this.isFeatured = isFeatured;
        this.priority = priority;
    }
}
