package com.mhealth.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabPriceRequest {

    @NotNull(message = "Lab User ID is required.")
    @NotBlank(message = "Lab User ID is required.")
    private Integer labUserId;

    @NotNull(message = "Category ID is required.")
    @NotBlank(message = "Category ID is required.")
    private Integer categoryId;

    @NotNull(message = "Subcategory ID is required.")
    @NotBlank(message = "Subcategory ID is required.")
    private Integer subCategoryId;

    @NotNull(message = "Lab price is required.")
    @NotBlank(message = "Lab price is required.")
    private Float labPrice;

    @NotBlank(message = "Lab price comment.")
    private String labPriceComment;
}

