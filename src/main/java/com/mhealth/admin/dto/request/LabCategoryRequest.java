package com.mhealth.admin.dto.request;

import com.mhealth.admin.dto.enums.CategoryStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String catName;

    private String catNameSl;
    private String profilePicture;
    private CategoryStatus catStatus;
}
